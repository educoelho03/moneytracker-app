package br.com.moneyTracker.user.service;

import br.com.moneyTracker.domain.entities.User;
import br.com.moneyTracker.dto.request.AuthRegisterRequestDTO;
import br.com.moneyTracker.exceptions.SamePasswordException;
import br.com.moneyTracker.exceptions.UserAlreadyExistsException;
import br.com.moneyTracker.repository.UserRepository;
import br.com.moneyTracker.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class )
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthRegisterRequestDTO authRegister;

    @BeforeEach
    void setUp(){
        authRegister = new AuthRegisterRequestDTO("John", "john.text@gmail.com", "javas@123");
    }

    @Test
    public void deveCriarUsuarioComSenhaEncriptada() { // TODO: Melhorar esse teste
        User user = userService.createNewUser(authRegister);

        assertEquals("john.text@gmail.com", user.getEmail());
        Assertions.assertNotEquals("javas@123", user.getPassword());
    }

    @Test
    public void deveRegistrarOUsuarioComSucesso(){
        User newUser = new User("John", "john.text@gmail.com", "javas@123");

        User savedUser = userService.createNewUser(authRegister);

        assertEquals(newUser.getEmail(), savedUser.getEmail());
        assertEquals(newUser.getName(), savedUser.getName());
        assertEquals(newUser.getEmail(), savedUser.getEmail());
        assertEquals(passwordEncoder.encode(newUser.getPassword()), savedUser.getPassword());
    }

    @Test
    public void deveLancarExcecaoQuandoEmailUsuarioJaExistir(){ // TODO: MELHORAR ESSE CODIGO
        User existingUser = new User("Larry", "john.text@gmail.com", "python@123");

        when(userRepository.findUserByEmail(authRegister.email())).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(authRegister), "User with this email: " + authRegister.email() + " already exists");
    }

    @Test
    public void deveAlterarASenhaDoUsuarioComSucesso(){
        String email = "john.text@gmail.com";
        String oldPassword = "oldPassword123";
        String newPassword = "oldPassword123";

        User user = new User("John", email, oldPassword);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(newPassword, oldPassword)).thenReturn(false);
        userService.updateUserPassword(email, newPassword);


        Assertions.assertFalse(passwordEncoder.matches(newPassword, user.getPassword()));
        assertEquals(email, user.getEmail());
        assertEquals("John", user.getName());

    }

    @Test
    public void deveLancarUmaExcecaoSeASenhaNaoForDiferenteDaAtual() {
        // Arrange
        String email = "john.text@gmail.com";
        String currentPassword = "java@123";
        String newPassword = currentPassword;

        User user = new User("John", email, currentPassword);

        // Configuração dos mocks
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(newPassword, currentPassword)).thenReturn(true);

        // Act & Assert
        SamePasswordException exception = assertThrows(
                SamePasswordException.class,
                () -> userService.updateUserPassword(email, newPassword)
        );

        // Verificações adicionais
        assertEquals("Password must be different", exception.getMessage());
        verify(passwordEncoder, times(1)).matches(newPassword, currentPassword);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
