package com.dev.BiteNowAPI.service;

import com.dev.BiteNowAPI.io.CartRequest;
import com.dev.BiteNowAPI.io.CartResponse;

public interface CartService {

    CartResponse addToCart(String foodId);

    CartResponse getCart();

    void clearCart();

    CartResponse removeFromCart(CartRequest cartRequest);
}
