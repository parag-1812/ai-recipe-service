package com.cloudkitchen.ai_recipe_service.controller;

import com.cloudkitchen.ai_recipe_service.dto.ParseDishRequest;
import com.cloudkitchen.ai_recipe_service.dto.ParsedDish;
import com.cloudkitchen.ai_recipe_service.service.AiRecipeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiRecipeController {

    private final AiRecipeService aiRecipeService;

    public AiRecipeController(AiRecipeService aiRecipeService) {
        this.aiRecipeService = aiRecipeService;
    }

    /**
     * Turn a natural-language request like
     * "boneless chicken in tomato gravy, medium spicy, extra garlic, less oil"
     * into a structured dish the order-engine can price and route.
     */
    @PostMapping("/parse-dish")
    public ParsedDish parseDish(@Valid @RequestBody ParseDishRequest request) {
        return aiRecipeService.parse(request.getText());
    }
}
