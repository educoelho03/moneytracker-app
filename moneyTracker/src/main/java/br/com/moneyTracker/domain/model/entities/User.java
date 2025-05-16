package br.com.moneyTracker.domain.model.entities;

import br.com.moneyTracker.domain.model.enums.USER_ROLES;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "saldo", nullable = false)
    private double saldo;

    @Enumerated(EnumType.STRING)
    @Column(name = "roles", nullable = false)
    private USER_ROLES roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transactions> transactions = new ArrayList<>();

    public User(Long user_id, String name, String email, String password, double saldo) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.saldo = saldo;
    }

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
        this.saldo = 0;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.roles.getRole().equals(USER_ROLES.ROLE_ADMIN.getRole())) {
          return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getUsername() {
        return name;
    }
}
