package com.dev.BiteNowAPI.io;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItem {

    private String foodID;
    private int quantity;
    private double price;
    private String category;
    private String imageUrl;
    private String description;
    private String name;

}
