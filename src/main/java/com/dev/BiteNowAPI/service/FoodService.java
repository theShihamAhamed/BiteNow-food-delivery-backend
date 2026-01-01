package com.dev.BiteNowAPI.service;

import com.dev.BiteNowAPI.io.FoodRequest;
import com.dev.BiteNowAPI.io.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {
    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest request, MultipartFile file);

}
