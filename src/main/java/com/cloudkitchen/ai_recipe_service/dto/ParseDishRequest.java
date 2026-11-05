package com.cloudkitchen.ai_recipe_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ParseDishRequest {

    @NotBlank(message = "Describe the dish you want")
    @Size(max = 2000, message = "Description is too long")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
