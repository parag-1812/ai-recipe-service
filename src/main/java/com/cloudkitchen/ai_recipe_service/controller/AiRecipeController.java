package com.cloudkitchen.ai_recipe_service.controller;

import com.cloudkitchen.ai_recipe_service.dto.ParseDishRequest;
import com.cloudkitchen.ai_recipe_service.dto.ParsedDish;
import com.cloudkitchen.ai_recipe_service.exception.RateLimitExceededException;
import com.cloudkitchen.ai_recipe_service.service.AiRecipeService;
import com.cloudkitchen.ai_recipe_service.service.RateLimiter;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiRecipeController {

    private final AiRecipeService aiRecipeService;
    private final RateLimiter rateLimiter;

    public AiRecipeController(AiRecipeService aiRecipeService, RateLimiter rateLimiter) {
        this.aiRecipeService = aiRecipeService;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Turn a natural-language request like
     * "boneless chicken in tomato gravy, medium spicy, extra garlic, less oil"
     * into a structured dish the order-engine can price and route.
     */
    @PostMapping("/parse-dish")
    public ParsedDish parseDish(@Valid @RequestBody ParseDishRequest request, Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "anonymous";
        if (!rateLimiter.tryAcquire(user)) {
            throw new RateLimitExceededException("Too many requests — please wait a moment and try again.");
        }
        return aiRecipeService.parse(request.getText());
    }
}
