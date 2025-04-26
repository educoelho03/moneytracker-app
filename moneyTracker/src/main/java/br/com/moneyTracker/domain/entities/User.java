package br.com.moneyTracker.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    private String name;
    private String email;
    private String password;
    private double saldo;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transactions> transactions = new ArrayList<>();

    public User(Long user_id, String name, String email, String password) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.saldo = 0;
    }

    public User(Long user_id, String name, String email, String password, double saldo) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.saldo = saldo;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User() {
    }


    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transactions> transactions) {
        this.transactions = transactions;
    }
}
