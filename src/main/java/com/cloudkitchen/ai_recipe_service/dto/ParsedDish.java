package com.cloudkitchen.ai_recipe_service.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

/**
 * The structured recipe Claude/Groq produces from a customer's natural-language
 * request, now including an estimated nutrition breakdown and a few suggested
 * alternative dishes the customer might also enjoy.
 */
@JsonClassDescription("A structured representation of a customer's custom dish request for a cloud kitchen.")
public record ParsedDish(

        @JsonPropertyDescription("Overall classification. Must be exactly \"VEG\" or \"NON_VEG\".")
        String dishType,

        @JsonPropertyDescription("Primary protein, if any. One of: Chicken, Paneer, Egg, Mutton, Fish, Soya, Tofu. Empty string if none.")
        String protein,

        @JsonPropertyDescription("All vegetables and ingredients the customer explicitly asked for. Empty list if none.")
        List<String> ingredients,

        @JsonPropertyDescription("Cooking style. One of: Curry, Dry, Fried, Grilled, Tandoor, Roasted, Baked, Steamed, Smoked.")
        String cookingStyle,

        @JsonPropertyDescription("Spice level. One of: No Spice, Mild, Medium, Hot, Extra Hot. Default Medium.")
        String spiceLevel,

        @JsonPropertyDescription("Oil preference. One of: No Oil, Less Oil, Medium Oil, Regular, Extra Butter, Ghee. Default Medium Oil.")
        String oilLevel,

        @JsonPropertyDescription("Dietary tags that apply. Empty list if none.")
        List<String> dietaryTags,

        @JsonPropertyDescription("Free-text special instructions for the kitchen. Empty string if none.")
        String specialInstructions,

        @JsonPropertyDescription("A single friendly sentence summarizing the dish, for the customer to confirm.")
        String summary,

        @JsonPropertyDescription("Estimated nutrition for one serving of this dish.")
        Nutrition nutrition,

        @JsonPropertyDescription("2-3 short alternative or popular dish ideas the customer might also enjoy, each phrased as a short orderable description.")
        List<String> suggestions,

        @JsonPropertyDescription("Integer 0-100: how confident you are that you correctly understood the customer's request into this dish.")
        Integer confidence,

        @JsonPropertyDescription("Each ingredient (including the protein) classified by how essential it is to the dish, with a sensible substitute where one exists. Covers the same ingredients as the 'ingredients' list plus the protein.")
        List<IngredientSpec> ingredientSpecs
) {

    @JsonClassDescription("An ingredient with how essential it is to the dish and a possible substitute.")
    public record IngredientSpec(
            @JsonPropertyDescription("Ingredient name, e.g. \"Paneer\" or \"Spinach\".")
            String name,
            @JsonPropertyDescription("How essential: \"CORE\" (the dish isn't itself without it), \"SUBSTITUTABLE\" (central but a good swap exists), or \"OPTIONAL\" (a garnish/extra that can be skipped).")
            String role,
            @JsonPropertyDescription("A single sensible substitute ingredient name if role is SUBSTITUTABLE (e.g. \"Tofu\" for Paneer, \"Rice Flour\" for Wheat Flour), else empty string.")
            String substitute
    ) {
    }

    @JsonClassDescription("Estimated nutrition for a single serving.")
    public record Nutrition(
            @JsonPropertyDescription("Estimated calories (kcal) for one serving.")
            Integer calories,
            @JsonPropertyDescription("Estimated protein in grams for one serving.")
            Integer proteinGrams,
            @JsonPropertyDescription("Estimated carbohydrates in grams for one serving.")
            Integer carbsGrams,
            @JsonPropertyDescription("Estimated fat in grams for one serving.")
            Integer fatGrams
    ) {
    }
}
