package com.apiator.vbs.card;

import com.apiator.vbs.account.Account;
import com.apiator.vbs.account.AccountService;
import com.apiator.vbs.account.Role;
import com.apiator.vbs.exception.ApiException;
import com.apiator.vbs.exception.InternalException;
import com.apiator.vbs.payment.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    @Autowired
    public CardService(CardRepository cardRepository, AccountService accountService) {
        this.cardRepository = cardRepository;
        this.accountService = accountService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<Card> getCurrentUserCards(Jwt currentUserJwt){
        UUID userUUID = Account.getAccountUUIDFromJwt(currentUserJwt);
        return cardRepository.findAllByOwner(userUUID);
    }

    public void createNewCard(Jwt currentUserJwt, Card card){
        if(cardRepository.findCardByNumber(card.getNumber()).isPresent()) {
            throw new ApiException("Card number is taken", HttpStatus.BAD_REQUEST);
        }

        UUID currentUserUUID = Account.getAccountUUIDFromJwt(currentUserJwt);
        Account currentUser = accountService.getAccountByUUID(currentUserUUID);

        card.setOwner(currentUser);
        card.hashPin(passwordEncoder);
        cardRepository.save(card);
    }

    private void checkAccess(Jwt currentUserJwt, String number){
        UUID currentUserUUID = Account.getAccountUUIDFromJwt(currentUserJwt);
        if(cardRepository.findAllByOwner(currentUserUUID).stream().noneMatch(card -> card.getNumber().equals(number))){
            throw new ApiException("Unauthorized access to the card", HttpStatus.UNAUTHORIZED);
        }
    }

    public void deleteCardByNumber(Jwt currentUserJwt, String number){
        checkAccess(currentUserJwt, number);

        cardRepository.deleteCardByNumber(number);
    }

    public void updatePinByNumber(Jwt currentUserJwt, String number, String pin){
        checkAccess(currentUserJwt, number);

        String pinHash = passwordEncoder.encode(pin);
        cardRepository.updateCardPinByNumber(number, pinHash);
    }

    public void updateCardBalanceByNumber(Jwt currentUserJwt, String number, int balance){
        UUID currentUserUUID = Account.getAccountUUIDFromJwt(currentUserJwt);
        Account currentUser = accountService.getAccountByUUID(currentUserUUID);
        if(currentUser.getRole() == Role.USER){
            throw new ApiException("Insufficient authority", HttpStatus.UNAUTHORIZED);
        }

        updateCardBalanceByNumberNoAuth(number, balance);
    }

    private void updateCardBalanceByNumberNoAuth(String number, int balance){
        if(balance < 0){
            throw new ApiException("Can't set negative balance", HttpStatus.BAD_REQUEST);
        }
        Optional<Card> optionalCard = cardRepository.findCardByNumber(number);
        if(optionalCard.isEmpty()){
            throw new ApiException("No such card", HttpStatus.BAD_REQUEST);
        }

        cardRepository.updateCardBalanceByNumber(number, balance);
    }

    @Transactional(rollbackFor=Exception.class)
    public void transfer(Payment payment) {
        if(payment.getSender().equals(payment.getRecipient())){
            throw new ApiException("Recipient and sender can't be the same", HttpStatus.BAD_REQUEST);
        }
        Card sender = cardRepository.findCardByNumber(payment.getSender()).orElseThrow(
                () -> new ApiException("Sender not found", HttpStatus.BAD_REQUEST)
        );
        if (!passwordEncoder.matches(payment.getPin(), sender.getPinHash())) {
            throw new ApiException("Bad pin", HttpStatus.BAD_REQUEST);
        }
        if(sender.getBalance() - payment.getAmount() < 0){
            throw new ApiException("Insufficient balance", HttpStatus.BAD_REQUEST);

        }
        Card recipient = cardRepository.findCardByNumber(payment.getRecipient()).orElseThrow(
                () -> new ApiException("Recipient not found", HttpStatus.BAD_REQUEST)
        );


        updateCardBalanceByNumberNoAuth(sender.getNumber(), sender.getBalance() - payment.getAmount());
        updateCardBalanceByNumberNoAuth(recipient.getNumber(), recipient.getBalance() + payment.getAmount());
    }
}
