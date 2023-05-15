package com.apiator.vbs.card;

import com.apiator.vbs.account.Account;
import com.apiator.vbs.account.AccountRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CardRepositoryTest {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountRepository accountRepository;
    private final String accountUUID = "cdb518d0-a369-4e8c-ad32-a41dd3344413";
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private Account account;

    @AfterEach
    void tearDown(){
        cardRepository.deleteAll();
        accountRepository.deleteAll();
    }


    @BeforeAll
    public void setUp(){
        Account account = new Account(UUID.fromString(accountUUID));
        accountRepository.save(account);
        this.account = accountRepository.getAccountById(UUID.fromString(accountUUID)).orElseThrow(
                () -> new RuntimeException("Can't start CardRepositoryTest because of errors in AccountRepository")
        );
    }

    @Test
    void findCardShouldExist() {
        Card card = new Card(
                "1234",
                "123"
        );
        card.setOwner(account);
        card.hashPin(passwordEncoder);
        cardRepository.save(card);

        Optional<Card> cardByNumber = cardRepository.findCardByNumber(card.getNumber());
        assertThat(cardByNumber.isPresent()).isTrue();
    }

    @Test
    void findCardShouldNotExist() {
        String number = "9999";

        Optional<Card> cardByNumber = cardRepository.findCardByNumber(number);
        assertThat(cardByNumber.isPresent()).isFalse();
    }

    @Test
    void deleteCardByNumber() {
        Card card = new Card(
                "1111",
                "111"
        );
        card.setOwner(account);
        card.hashPin(passwordEncoder);
        cardRepository.save(card);

        cardRepository.deleteCardByNumber(card.getNumber());
        Optional<Card> cardByNumber = cardRepository.findCardByNumber(card.getNumber());
        assertThat(cardByNumber.isPresent()).isFalse();
    }

    @Test
    void updateCardBalanceByNumber() {
        String number = "1234";
        int balance = 125;

        Card card = new Card(
                number,
                "123"
        );
        card.setOwner(account);
        card.hashPin(passwordEncoder);
        cardRepository.save(card);

        cardRepository.updateCardBalanceByNumber(number, balance);

        Optional<Card> cardByNumber = cardRepository.findCardByNumber(number);
        assertThat(cardByNumber.get().getBalance()).isEqualTo(balance);
    }
}