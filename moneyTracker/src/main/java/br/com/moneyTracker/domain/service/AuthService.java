package br.com.moneyTracker.domain.service;

import br.com.moneyTracker.domain.model.entities.User;
import br.com.moneyTracker.api.dto.request.AuthLoginRequestDTO;
import br.com.moneyTracker.api.dto.request.AuthRegisterRequestDTO;
import br.com.moneyTracker.api.dto.response.DataResponseDTO;
import br.com.moneyTracker.api.exceptions.EmailAlreadyExistException;
import br.com.moneyTracker.api.exceptions.InvalidCredentialsException;
import br.com.moneyTracker.interfaces.AuthServiceInterface;
import br.com.moneyTracker.interfaces.EmailInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthServiceInterface {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final EmailServiceSendgrid emailService;

    public AuthService(TokenService tokenService, PasswordEncoder passwordEncoder, UserService userService, EmailServiceSendgrid emailService) {
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public DataResponseDTO loginUser(AuthLoginRequestDTO authLoginRequestDTO) {
        logger.info("Attempting login for user with email: {}", authLoginRequestDTO.email());

        User userRecovery = userService.findUserByEmail(authLoginRequestDTO.email());
        logger.debug("User found for email {}: {}", authLoginRequestDTO.email(), userRecovery);

        if (!passwordEncoder.matches(authLoginRequestDTO.password(), userRecovery.getPassword())) {
            logger.warn("Invalid password attempt for user with email: {}", authLoginRequestDTO.email());
            throw new InvalidCredentialsException("Wrong Password.");
        }

        String token = tokenService.generateToken(userRecovery);
        logger.info("User {} successfully logged in. Token generated.", userRecovery.getEmail());

        return new DataResponseDTO(userRecovery.getName(), token);
    }

    @Override
    public DataResponseDTO registerUser(AuthRegisterRequestDTO authRegisterRequestDTO) {
        logger.info("Starting user registration process for email: {}", authRegisterRequestDTO.email());

        try {
            User user = userService.registerUser(authRegisterRequestDTO);
            logger.debug("User registered successfully: {}", user);

            String token = tokenService.generateToken(user);
            logger.debug("Token generated for new user: {}", user.getEmail());

            var message = EmailInterface.Message.builder()
                    .to(user.getEmail())
                    .subject("🎉 You're in! Welcome to MoneyTracker.")
                    .body("Hi " + user.getName() + ",\n\n" +
                            "Welcome to MoneyTracker – we're glad to have you on board!\n\n" +
                            "Your account is ready, and you can now start exploring all the features we've prepared for you.\n\n" +
                            "Need help? We're here anytime.\n\n" +
                            "Let's get started! 🚀\n" +
                            "— The MoneyTracker Team"
                    ).build();

            logger.info("Sending welcome email to: {}", user.getEmail());
            emailService.sendMail(message);
            logger.info("Welcome email sent successfully to: {}", user.getEmail());

            return new DataResponseDTO(user.getName(), token);
        } catch (Exception e) {
            logger.error("Error during user registration for email: {}. Error: {}",
                    authRegisterRequestDTO.email(), e.getMessage(), e);
            throw new EmailAlreadyExistException("Error to register user.", e);
        }
    }
}