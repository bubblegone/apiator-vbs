package com.apiator.vbs.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping()
    public UUID pay(@RequestBody Payment payment){
        return paymentService.pay(payment);
    }

    @GetMapping()
    public Payment getPaymentById(@RequestParam UUID paymentId){
        return paymentService.getPaymentById(paymentId);
    }
}
