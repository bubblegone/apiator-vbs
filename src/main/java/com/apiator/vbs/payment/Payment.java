package com.apiator.vbs.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @Type(type="uuid-char")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @JsonIgnore
    private UUID id;
    @Size(min = 4, max = 4, message = "Card number should be exactly 4 characters")
    private String sender;
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 3, max = 3, message = "Pin should be exactly 3 characters")
    private String pin;
    @Size(min = 4, max = 4, message = "Card number should be exactly 4 characters")
    private String recipient;
    @Positive(message = "Amount should be at least 1")
    private int amount;
    @Column(unique = true)
    private String paymentToken;
}
