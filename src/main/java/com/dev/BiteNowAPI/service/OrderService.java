package com.dev.BiteNowAPI.service;

import com.dev.BiteNowAPI.io.OrderRequest;
import com.dev.BiteNowAPI.io.OrderResponse;

public interface   OrderService {

    OrderResponse createOrderWithPayment(OrderRequest request);

    void verifyPayment(String payload, String sigHeader);
}
