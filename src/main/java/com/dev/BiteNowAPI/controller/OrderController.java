package com.dev.BiteNowAPI.controller;

import com.dev.BiteNowAPI.io.OrderRequest;
import com.dev.BiteNowAPI.io.OrderResponse;
import com.dev.BiteNowAPI.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public OrderResponse createOrderWithPayment(@RequestBody OrderRequest request) {
        try {
            return orderService.createOrderWithPayment(request);
        } catch (Exception e) {
            throw new RuntimeException("Order creation failed", e);
        }
    }

}
