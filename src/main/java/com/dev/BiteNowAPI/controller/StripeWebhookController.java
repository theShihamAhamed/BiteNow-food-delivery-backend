package com.dev.BiteNowAPI.controller;


import com.dev.BiteNowAPI.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/webhook")
public class StripeWebhookController {

    private final OrderService orderService;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        orderService.verifyPayment(payload, sigHeader);
        return ResponseEntity.ok("Webhook received");
    }

}
