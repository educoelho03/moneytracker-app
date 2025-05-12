package br.com.moneyTracker.api.dto.request;

import br.com.moneyTracker.domain.model.entities.Transactions;

public record TransactionRequestDTO(String token, Transactions transaction) {
}
