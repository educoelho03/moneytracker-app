package br.com.moneyTracker.domain.model.entities;

import br.com.moneyTracker.domain.model.enums.TRANSACTION_CATEGORY;
import br.com.moneyTracker.domain.model.enums.TRANSACTION_TYPE;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.core.util.Json;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transaction_id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TRANSACTION_TYPE transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_category", nullable = false)
    private TRANSACTION_CATEGORY transactionCategory;

    @Column(name = "date", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; 

    public Transactions(String name, double amount, TRANSACTION_TYPE transactionType, TRANSACTION_CATEGORY transactionCategory, LocalDate date, User user) {
        this.name = name;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionCategory = transactionCategory;
        this.date = date;
        this.user = user;
    }

    public Transactions() {
        this.date = LocalDate.now(); // quando o spring deserializa o json ele usa o construtor padrao
    }

}
