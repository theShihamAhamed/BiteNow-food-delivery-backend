package com.dev.BiteNowAPI.service;

import com.dev.BiteNowAPI.io.CartResponse;

public interface CartService {

    CartResponse addToCart(String foodId);
}
