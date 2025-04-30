package br.com.moneyTracker.service;

import br.com.moneyTracker.domain.entities.User;
import br.com.moneyTracker.dto.request.AuthRegisterRequestDTO;
import br.com.moneyTracker.exceptions.PasswordNullException;
import br.com.moneyTracker.exceptions.SamePasswordException;
import br.com.moneyTracker.exceptions.UserAlreadyExistsException;
import br.com.moneyTracker.exceptions.UserNotFoundException;
import br.com.moneyTracker.interfaces.UserServiceInterface;
import br.com.moneyTracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new PasswordNullException("Password cannot be null or empty");
        }

        User userRecovery = findUserByEmail(email);

        if(passwordEncoder.matches(newPassword, userRecovery.getPassword())) {
            throw new SamePasswordException("Passwords must be different");
        }
        userRecovery.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userRecovery);
    }

    @Override
    public User registerUser(AuthRegisterRequestDTO authRegisterRequestDTO) {
        String email = authRegisterRequestDTO.email().trim();
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        userRepository.findUserByEmail(email)
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException(
                            String.format("User with email %s already exists", email));
                });

        User newUser = createNewUser(authRegisterRequestDTO);
        return userRepository.save(newUser);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with this email : " + email + " not found"));
    }

    public User createNewUser(AuthRegisterRequestDTO dto) {
        String name = dto.name();
        String email = dto.email();
        String encodedPassword = passwordEncoder.encode(dto.password());


        return new User(name, email, encodedPassword);
    }

}
