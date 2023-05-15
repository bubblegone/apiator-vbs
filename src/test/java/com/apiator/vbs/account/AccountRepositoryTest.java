package com.apiator.vbs.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    void tearDown(){
        accountRepository.deleteAll();
    }

    @Test
    void getAccountShouldExist(){
        UUID accountUUID = UUID.fromString("d63acec9-f1e2-4723-bdb7-587ed0963cf9");
        Account account = new Account(accountUUID);
        accountRepository.save(account);

        assertThat(accountRepository.getAccountById(accountUUID)).isPresent();
    }

    @Test
    void getAccountShouldNotExist(){
        UUID accountUUID = UUID.fromString("25ccb9db-d2bb-4d9c-abc4-19c25e7a878c");
        Account account = new Account(accountUUID);
        accountRepository.save(account);

        assertThat(accountRepository.getAccountById(UUID.fromString("ada2249d-1dc0-4511-80ad-1486bd98d33e")))
                .isEmpty();
    }
}