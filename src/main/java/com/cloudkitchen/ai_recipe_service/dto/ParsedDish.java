package com.cloudkitchen.ai_recipe_service.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

/**
 * The structured recipe Claude produces from a customer's natural-language
 * request. The field descriptions double as the schema the model is
 * constrained to (via structured outputs), so keep the allowed values accurate.
 */
@JsonClassDescription("A structured representation of a customer's custom dish request for a cloud kitchen.")
public record ParsedDish(

        @JsonPropertyDescription("Overall classification. Must be exactly \"VEG\" or \"NON_VEG\". Use NON_VEG if any animal protein (chicken, mutton, fish, egg, prawn, etc.) is present; otherwise VEG.")
        String dishType,

        @JsonPropertyDescription("Primary protein, if any. One of: Chicken, Paneer, Egg, Mutton, Fish, Soya, Tofu. Use an empty string if the dish has no distinct protein.")
        String protein,

        @JsonPropertyDescription("All vegetables and ingredients the customer explicitly asked for, e.g. [\"Garlic\", \"Tomato\", \"Coriander\"]. Empty list if none specified.")
        List<String> ingredients,

        @JsonPropertyDescription("Cooking style. One of: Curry, Dry, Fried, Grilled, Tandoor, Roasted, Baked, Steamed, Smoked. Make a best guess from the request if it is not stated.")
        String cookingStyle,

        @JsonPropertyDescription("Spice level. One of: No Spice, Mild, Medium, Hot, Extra Hot. Default to Medium if not stated.")
        String spiceLevel,

        @JsonPropertyDescription("Oil preference. One of: No Oil, Less Oil, Medium Oil, Regular, Extra Butter, Ghee. Default to Medium Oil if not stated.")
        String oilLevel,

        @JsonPropertyDescription("Dietary tags that apply, e.g. [\"Jain\", \"Gluten Free\", \"High Protein\", \"Keto\", \"Vegan\", \"Homemade Style\", \"Restaurant Style\", \"Healthy\", \"Low Calories\"]. Empty list if none apply.")
        List<String> dietaryTags,

        @JsonPropertyDescription("Any free-text special instructions for the kitchen, e.g. \"boneless chicken\", \"thick gravy\", \"soft cooked vegetables\". Empty string if none.")
        String specialInstructions,

        @JsonPropertyDescription("A single friendly sentence summarizing the dish, for the customer to confirm before ordering.")
        String summary
) {
}
