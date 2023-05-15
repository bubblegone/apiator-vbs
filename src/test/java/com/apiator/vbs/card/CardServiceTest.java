package com.apiator.vbs.card;

import com.apiator.vbs.JwtGenerator;
import com.apiator.vbs.account.Account;
import com.apiator.vbs.account.AccountService;
import com.apiator.vbs.exception.ApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @Mock
    private CardRepository cardRepository;
    @Mock
    private AccountService accountService;
    @InjectMocks
    private CardService cardService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Captor
    ArgumentCaptor<Card> cardCaptor;

    @Test
    void getCurrentUserCardsPassedSubEqualsToInitial(){
        String sub = "6ba2427c-f8ac-4c57-a858-fec3779df597";
        Jwt jwt = JwtGenerator.jwtFromSub(sub);
        cardService.getCurrentUserCards(jwt);
        verify(cardRepository).findAllByOwner(UUID.fromString(sub));
    }

    @Test
    void createNewCardFailBecauseCardNumberIsTaken(){
        String sub = "6ba2427c-f8ac-4c57-a858-fec3779df597";
        Jwt jwt = JwtGenerator.jwtFromSub(sub);
        Card card = new Card();
        card.setNumber("1234");
        when(cardRepository.findCardByNumber(card.getNumber())).thenReturn(Optional.of(card));

        ApiException thrownException = Assertions.assertThrows(ApiException.class, () -> {
            cardService.createNewCard(jwt, card);
        });
        assertEquals(thrownException.getMessage(), "Card number is taken");
    }

    @Test
    void createNewCardSuccess(){
        String sub = "e92f9924-4697-4fad-bb05-5ce65cc16129";
        Jwt jwt = JwtGenerator.jwtFromSub(sub);

        Account owner = new Account();
        owner.setId(UUID.fromString(sub));

        Card card = new Card();
        card.setNumber("2345");
        card.setOwner(owner);
        card.setPin("234");

        when(cardRepository.findCardByNumber(card.getNumber())).thenReturn(Optional.empty());
        when(accountService.getAccountByUUID(owner.getId())).thenReturn(owner);

        cardService.createNewCard(jwt, card);

        verify(cardRepository).save(cardCaptor.capture());
        Card savedCard = cardCaptor.getValue();
        assertThat(savedCard).usingRecursiveComparison().isEqualTo(card);
    }
}