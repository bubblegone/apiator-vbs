package com.apiator.vbs.card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/card")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public List<Card> getCurrentUserCards(@AuthenticationPrincipal Jwt currentUserJwt) {
        return cardService.getCurrentUserCards(currentUserJwt);
    }

    @PostMapping
    public void createNewCard(@AuthenticationPrincipal Jwt currentUserJwt, @RequestBody Card card) {
        cardService.createNewCard(currentUserJwt, card);
    }

    @DeleteMapping("/{number}")
    public void deleteCardByNumber(@AuthenticationPrincipal Jwt currentUserJwt, @PathVariable String number) {
        cardService.deleteCardByNumber(currentUserJwt, number);
    }

    @PutMapping("/{number}")
    public void updatePinByNumber(@AuthenticationPrincipal Jwt currentUserJwt, @PathVariable String number,
                                  @RequestBody String pin){
        cardService.updatePinByNumber(currentUserJwt, number, pin);
    }

    @PutMapping("/admin/setbalance/{number}")
    public void setBalanceByNumber(@AuthenticationPrincipal Jwt currentUserJwt,@PathVariable("number") String number,
                                   @RequestBody int balance){
        cardService.updateCardBalanceByNumber(currentUserJwt, number, balance);
    }

}
