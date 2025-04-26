package br.com.moneyTracker.transaction.service;

import br.com.moneyTracker.domain.entities.Transactions;
import br.com.moneyTracker.domain.entities.User;
import br.com.moneyTracker.domain.enums.TRANSACTION_CATEGORY;
import br.com.moneyTracker.dto.response.TransactionResponseDTO;
import br.com.moneyTracker.exceptions.SaldoInsuficienteException;
import br.com.moneyTracker.infra.security.TokenService;
import br.com.moneyTracker.repository.TransactionRepository;
import br.com.moneyTracker.repository.UserRepository;
import br.com.moneyTracker.service.TransactionService;
import br.com.moneyTracker.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static br.com.moneyTracker.domain.enums.TRANSACTION_TYPE.DEPOSITO;
import static br.com.moneyTracker.domain.enums.TRANSACTION_TYPE.DESPESA;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {


    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;
    private User user;
    private Transactions despesa;
    private Transactions deposito;

    @BeforeEach
    void setUp(){
        user = new User(1L, "John", "john.text@gmail.com", "javas@123", 100.0);

        // Configura transação de despesa
        despesa = new Transactions();
        despesa.setName("Compras mercado");
        despesa.setAmount(50.0);
        despesa.setTransactionType(DESPESA);
        despesa.setTransactionCategory(TRANSACTION_CATEGORY.FOOD);
        despesa.setDate(LocalDate.now());

        // Configura transação de depósito
        deposito = new Transactions();
        deposito.setName("Salário");
        deposito.setAmount(200.0);
        deposito.setTransactionType(DEPOSITO);
        deposito.setTransactionCategory(TRANSACTION_CATEGORY.SALARY);
        deposito.setDate(LocalDate.now());
    }


    @Test
    public void deveSubtrairOValorDaTransacaoDoSaldoDoUsuario(){
        transactionService.subtractToBalance(user, 100.0);

        Assertions.assertEquals(0.0, user.getSaldo());
        Assertions.assertEquals(1L, user.getUser_id());
    }

    @Test
    public void deveLancarUmaExcecaoQuandoOUsuarioNaoTiverSaldoParaATransação() {
        double valorMaiorQueSaldo = 1000.0;

        SaldoInsuficienteException exception = assertThrows(
                SaldoInsuficienteException.class,
                () -> transactionService.subtractToBalance(user, valorMaiorQueSaldo));

        assertEquals("Saldo insuficiente para realizar a transação.", exception.getMessage());
        assertEquals(100.0, user.getSaldo(), "Saldo não deveria ser alterado quando há exceção");
    }

    @Test
    public void deveAdicionarOValorDaTransacaoDoSaldoDoUsuario(){
        transactionService.addToBalance(user, 100.0);

        Assertions.assertEquals(200.0, user.getSaldo());
        Assertions.assertEquals(1L, user.getUser_id());
    }

    @Test
    public void deveSubtrairOSaldoSeATransacaooForDespesa(){
        double initalAmount = user.getSaldo();
        double despesaAmount = despesa.getAmount();
        double expectedAmount = initalAmount - despesaAmount;

        transactionService.calculateAmount(user, despesa);

        assertEquals(expectedAmount, user.getSaldo(),
                "Deveria subtrair o valor da despesa do saldo");

    }

    @Test
    public void deveAdicionarOSaldoSeATransaaoForDeposito(){
        double initalAmount = user.getSaldo();
        double despesaAmount = deposito.getAmount();
        double expectedAmount = initalAmount + despesaAmount;

        transactionService.calculateAmount(user, deposito);

        // Assert
        assertEquals(expectedAmount, user.getSaldo(),
                "Deveria adicionar o valor do depósito ao saldo");
    }

    @Test
    public void deveCriarTransacaoComSucesso() {
        String token = "validToken";
        String email = user.getEmail();

        // Configuração mínima dos mocks
        when(tokenService.validateToken(token)).thenReturn(email);
        when(userService.findUserByEmail(email)).thenReturn(user);

        when(transactionRepository.save(ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Transactions transactionCreated = transactionService.createNewTransaction(token, despesa);

        assertNotNull(transactionCreated, "A transação não deveria ser nula");
        assertEquals(despesa.getName(), transactionCreated.getName(), "O nome da transação deveria ser mantido");
        assertEquals(user, transactionCreated.getUser(), "A transação deveria estar vinculada ao usuário");
        assertEquals(50.0, user.getSaldo(), "O saldo do usuário deveria ser atualizado");

        // Verifica se os métodos foram chamados
        verify(transactionRepository).save(ArgumentMatchers.any());
        verify(userRepository).save(user);
    }

    @Test
    public void deveRetornarTodasAsTransacoesPorEmailDoUsuario() {
        // Arrange (Preparação)
        String token = "token-valido";
        String email = user.getEmail();

        // Adiciona transações ao usuário (simula o que estaria no banco de dados)
        user.getTransactions().add(despesa);
        user.getTransactions().add(deposito);

        // Configura os mocks
        when(tokenService.validateToken(anyString())).thenReturn(email);
        when(userService.findUserByEmail(anyString())).thenReturn(user);

        // Act (Execução)
        List<TransactionResponseDTO> resultado = transactionService.listTransactionsByEmail(token);

        // Assert (Verificações)
        assertEquals(2, resultado.size(), "Deveria retornar 2 transações");

        // Verifica se a primeira transação é a despesa
        assertEquals(despesa.getName(), resultado.get(0).name());
        assertEquals(despesa.getAmount(), resultado.get(0).amount());

        // Verifica se a segunda transação é o depósito
        assertEquals(deposito.getName(), resultado.get(1).name());
        assertEquals(deposito.getAmount(), resultado.get(1).amount());

        // Verifica se os métodos foram chamados corretamente
        verify(tokenService).validateToken(token);
        verify(userService).findUserByEmail(email);
    }

}
