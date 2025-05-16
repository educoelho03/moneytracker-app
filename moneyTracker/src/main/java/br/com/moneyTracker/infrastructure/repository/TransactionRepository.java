package br.com.moneyTracker.infrastructure.repository;

import br.com.moneyTracker.domain.model.entities.Transactions;
import br.com.moneyTracker.domain.model.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Integer> {
    Page<Transactions> findAllByUser(User user, Pageable pageable);
}
