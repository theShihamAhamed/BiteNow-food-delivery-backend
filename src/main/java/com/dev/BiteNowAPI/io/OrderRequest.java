package com.dev.BiteNowAPI.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {

    private List<OrderItem> orderItems;
    private String phoneNumber;
    private String email;
    private String userAddress;
    private double amount;

}