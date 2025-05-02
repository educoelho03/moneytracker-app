package br.com.moneyTracker.service;

import br.com.moneyTracker.domain.entities.Transactions;
import br.com.moneyTracker.domain.entities.User;
import br.com.moneyTracker.domain.enums.TRANSACTION_TYPE;
import br.com.moneyTracker.dto.response.TransactionResponseDTO;
import br.com.moneyTracker.exceptions.AmountInsufficientException;
import br.com.moneyTracker.exceptions.InvalidTransactionException;
import br.com.moneyTracker.infra.security.TokenService;
import br.com.moneyTracker.repository.TransactionRepository;
import br.com.moneyTracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TokenService tokenService;
    private final UserService userService;
    private UserRepository userRepository;
    private TransactionRepository transactionRepository;

    public TransactionService(UserRepository userRepository , TransactionRepository transactionRepository, TokenService tokenService, UserService userService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    public List<TransactionResponseDTO> listTransactionsByEmail(String token) { // TODO: DUVIDA AQUI, É UMA BOA PRATICA FAZER A CONVERSAO DE ENTITY PARA RESPONSE DENTRO DA SERVICE?
        String tokenModified = token.replace("Bearer ", "");
        String userEmail = tokenService.validateToken(tokenModified);
        User user = userService.findUserByEmail(userEmail);

        logger.info("Listing transactions for user: {}", userEmail);

        return user.getTransactions().stream()
                .map(transactions -> new TransactionResponseDTO(
                        transactions.getTransaction_id(),
                        transactions.getName(),
                        transactions.getAmount(),
                        transactions.getTransactionType(),
                        transactions.getTransactionCategory(),
                        transactions.getDate()
                )).collect(Collectors.toList());
    }

    public Transactions createNewTransaction(String token, Transactions transaction) {
        String userEmail = tokenService.validateToken(token); // TODO: REVER ESSE PONTO
        User user = userService.findUserByEmail(userEmail);

        logger.info("Creating new transaction for user: {}", userEmail);

        transaction.setUser(user);

        calculateAmount(user, transaction);
        Transactions savedTransaction = transactionRepository.save(transaction);
        userRepository.save(user);

        logger.info("Transaction created successfully with ID: {}", savedTransaction.getTransaction_id());
        return savedTransaction;
    }

    public void calculateAmount(User user, Transactions transaction) {
        try {
            if (transaction.getTransactionType() == TRANSACTION_TYPE.DESPESA) {
                subtractToBalance(user, transaction.getAmount());
            } else if (transaction.getTransactionType() == TRANSACTION_TYPE.DEPOSITO) {
                addToBalance(user, transaction.getAmount());
            }
        } catch (AmountInsufficientException e) {
            logger.error("Insufficient balance for user: {}", user.getEmail(), e);
            throw e;
        }
    }

    public void addToBalance(User user, double amount){
        if(amount <= 0) {
            throw new InvalidTransactionException("Amount to add must be positive");
        }
        double newBalance = user.getSaldo() + amount;
        logger.info("Adding to balance for user: {}. amount: {}", user.getEmail(), amount);
        user.setSaldo(newBalance);
        logger.info("Final balance for user: {}. amount: {}", user.getEmail(), user.getSaldo());
    }

    public void subtractToBalance(User user, double amount) {
        if(amount <= 0) {
            throw new InvalidTransactionException("Amount to subtract must be positive");
        }
        if (amount > user.getSaldo()) {
            throw new AmountInsufficientException("Saldo insuficiente para realizar a transação.");
        }
        double newBalance = user.getSaldo() - amount;
        logger.info("Subtracting to balance for user: {}. amount: {}", user.getEmail(), amount);
        user.setSaldo(newBalance);
        logger.info("Final balance for user: {}. amount: {}", user.getEmail(), user.getSaldo());

    }
}
