package br.com.moneyTracker.api.dto.response;

import br.com.moneyTracker.domain.model.entities.Transactions;
import br.com.moneyTracker.domain.model.enums.TRANSACTION_CATEGORY;
import br.com.moneyTracker.domain.model.enums.TRANSACTION_TYPE;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public record TransactionResponseDTO(
        Long transaction_id,
        String name,
        double amount,
        TRANSACTION_TYPE transactionType,
        TRANSACTION_CATEGORY transactionCategory,
        LocalDate date) {

    public static TransactionResponseDTO fromEntity(Transactions transaction) { // converte para DTO
        return new TransactionResponseDTO(
                transaction.getTransaction_id(),
                transaction.getName(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getTransactionCategory(),
                transaction.getDate()
        );
    }

    public static Page<TransactionResponseDTO> fromPageEntity(Page<Transactions> transactionsPage) {
        return transactionsPage.map(TransactionResponseDTO::fromEntity);
    }

}
