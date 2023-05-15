package com.apiator.vbs.payment;

import com.apiator.vbs.card.CardService;
import com.apiator.vbs.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {
    private final CardService cardService;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(CardService cardService, PaymentRepository paymentRepository) {
        this.cardService = cardService;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(rollbackFor=Exception.class)
    public UUID pay(Payment payment) {
        cardService.transfer(payment);
        paymentRepository.save(payment);
        return payment.getId();
    }

    public Payment getPaymentById(UUID id){
        return paymentRepository.getPaymentById(id).orElseThrow(
                () -> new ApiException("No payment record found", HttpStatus.NOT_FOUND)
        );
    }
}
