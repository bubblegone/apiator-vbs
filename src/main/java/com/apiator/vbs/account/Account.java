package com.apiator.vbs.account;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.security.oauth2.jwt.Jwt;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {
    @Id
    @Type(type="uuid-char")
    private UUID id;
    private Role role;

    public Account(UUID id) {
        this.id = id;
        role = Role.USER;
    }

    public static UUID getAccountUUIDFromJwt(Jwt jwt){
        return UUID.fromString(jwt.getSubject());
    }
}
