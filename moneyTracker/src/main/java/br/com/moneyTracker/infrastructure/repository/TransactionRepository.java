package br.com.moneyTracker.infrastructure.repository;

import br.com.moneyTracker.domain.model.entities.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Integer> {
}
