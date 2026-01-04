package com.dev.BiteNowAPI.service;

import com.dev.BiteNowAPI.repository.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final UserService userService;

    @Override
    public void addToCart(String foodId) {

        String userId = userService.findByUserId();

    }

}
