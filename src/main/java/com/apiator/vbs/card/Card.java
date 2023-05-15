package com.apiator.vbs.card;

import com.apiator.vbs.account.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


import javax.persistence.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO
    )
    @JsonIgnore
    private Long id;
    @Column(unique = true, nullable = false)
    @Size(max = 4, min = 4)
    private String number;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient
    @Size(max = 3, min = 3)
    private String pin;
    @JsonIgnore
    @NotNull
    private String pinHash;
    @Column(columnDefinition = "integer default 0")
    private int balance;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnore
    private Account owner;

    public Card(String number, String pin) {
        this.number = number;
        this.pin = pin;
    }

    public void hashPin(PasswordEncoder passwordEncoder) {
        pinHash = passwordEncoder.encode(pin);
        System.out.println(pinHash);
    }
}
