package br.com.moneyTracker.domain.service;

import br.com.moneyTracker.api.exceptions.EmailAlreadyExistException;
import br.com.moneyTracker.api.exceptions.PasswordNullOrEmptyException;
import br.com.moneyTracker.api.exceptions.SamePasswordException;
import br.com.moneyTracker.api.exceptions.UserNotFoundException;
import br.com.moneyTracker.domain.model.entities.User;
import br.com.moneyTracker.api.dto.request.AuthRegisterRequestDTO;
import br.com.moneyTracker.domain.model.enums.USER_ROLES;
import br.com.moneyTracker.interfaces.UserServiceInterface;
import br.com.moneyTracker.infrastructure.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceInterface {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void updateUserPassword(String email, String newPassword) {
        logger.info("Attempting to update password for user: {}", email);

        if (email.isEmpty()) {
            logger.error("Empty email provided for password update");
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            logger.error("Empty or null password provided for user: {}", email);
            throw new PasswordNullOrEmptyException("Password cannot be null or empty");
        }

        User userRecovery = findUserByEmail(email);
        logger.debug("User found for password update: {}", email);

        if(passwordEncoder.matches(newPassword, userRecovery.getPassword())) {
            logger.warn("Attempt to set same password for user: {}", email);
            throw new SamePasswordException("Passwords must be different");
        }
        userRecovery.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userRecovery);
        logger.info("Password successfully updated for user: {}", email);
    }

    @Override
    public User registerUser(AuthRegisterRequestDTO authRegisterRequestDTO) {
        logger.info("Starting registration process for email: {}", authRegisterRequestDTO.email());
        String email = authRegisterRequestDTO.email().trim();

        if(authRegisterRequestDTO.name() == null || authRegisterRequestDTO.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if(authRegisterRequestDTO.password() == null || authRegisterRequestDTO.password().trim().isEmpty()) {
            throw new PasswordNullOrEmptyException("Password cannot be null or empty");
        }

        userRepository.findUserByEmail(email)
                .ifPresent(user -> {
                    throw new EmailAlreadyExistException(
                            String.format("User with this email %s already exists", email));
                });

        User newUser = createNewUser(authRegisterRequestDTO);
        logger.info("User successfully registered with email: {}", email);
        return userRepository.save(newUser);

    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with this email : " + email + " not found"));
    }

    public User createNewUser(AuthRegisterRequestDTO dto) {
        logger.debug("Creating new user entity for email: {}", dto.email());
        String name = dto.name();
        String email = dto.email();
        String encodedPassword = passwordEncoder.encode(dto.password());

        User user = new User(name, email, encodedPassword);
        user.setRoles(dto.rolesOrDefault());
        return user;
    }
}
