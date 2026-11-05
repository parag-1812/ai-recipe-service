package com.cloudkitchen.ai_recipe_service.service;

import com.cloudkitchen.ai_recipe_service.dto.ParsedDish;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Turns a customer's free-text food request into a structured {@link ParsedDish}.
 * <p>
 * Uses Groq's free, OpenAI-compatible chat API in JSON mode. The provider is
 * swappable: anything that speaks the OpenAI chat-completions shape (Groq,
 * OpenAI, a local Ollama, or Anthropic later) only needs a different base URL,
 * key, and model.
 */
@Service
public class AiRecipeService {

    private static final String SYSTEM_PROMPT = """
            You are a culinary parser for a cloud-kitchen ordering platform.
            Convert the customer's natural-language food request into a single
            structured dish and respond with ONLY a JSON object (no markdown, no
            prose) with exactly these keys:

              "dishType"            -> "VEG" or "NON_VEG" (NON_VEG if any animal protein is present)
              "protein"            -> one of Chicken, Paneer, Egg, Mutton, Fish, Soya, Tofu, or "" if none
              "ingredients"        -> array of vegetables/ingredients requested, e.g. ["Garlic","Tomato"]
              "cookingStyle"       -> one of Curry, Dry, Fried, Grilled, Tandoor, Roasted, Baked, Steamed, Smoked
              "spiceLevel"         -> one of No Spice, Mild, Medium, Hot, Extra Hot (default Medium)
              "oilLevel"           -> one of No Oil, Less Oil, Medium Oil, Regular, Extra Butter, Ghee (default Medium Oil)
              "dietaryTags"        -> array from Jain, Gluten Free, High Protein, Keto, Vegan, Homemade Style, Restaurant Style, Healthy, Low Calories ([] if none)
              "specialInstructions"-> free text like "boneless" or "thick gravy", or "" if none
              "summary"            -> one friendly sentence summarizing the dish
              "nutrition"          -> object { "calories": int, "proteinGrams": int, "carbsGrams": int, "fatGrams": int } estimated for ONE serving
              "suggestions"        -> array of 2-3 short alternative or popular dish ideas the customer might also enjoy, each a short orderable description (e.g. "Paneer tikka, grilled, mild")

            Infer sensible values where the customer is silent. Only include
            ingredients and dietary tags the request actually implies. Make a
            reasonable nutrition estimate for a typical single serving.
            """;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper;
    private final String apiKey;
    private final String baseUrl;
    private final String model;

    public AiRecipeService(
            ObjectMapper mapper,
            @Value("${groq.api-key:}") String apiKey,
            @Value("${groq.base-url:https://api.groq.com/openai/v1}") String baseUrl,
            @Value("${groq.model:llama-3.3-70b-versatile}") String model) {
        this.mapper = mapper;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
    }

    public ParsedDish parse(String text) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "GROQ_API_KEY is not set. Get a free key at https://console.groq.com and set it in the environment.");
        }

        try {
            Map<String, Object> body = Map.of(
                    "model", model,
                    "temperature", 0.2,
                    "max_tokens", 1024,
                    "response_format", Map.of("type", "json_object"),
                    "messages", List.of(
                            Map.of("role", "system", "content", SYSTEM_PROMPT),
                            Map.of("role", "user", "content", text)));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() / 100 != 2) {
                throw new IllegalStateException("Groq API error " + response.statusCode() + ": " + response.body());
            }

            JsonNode root = mapper.readTree(response.body());
            String content = root.path("choices").path(0).path("message").path("content").asText();
            if (content.isBlank()) {
                throw new IllegalStateException("Groq returned an empty response");
            }
            return mapper.readValue(content, ParsedDish.class);

        } catch (IOException e) {
            throw new IllegalStateException("Failed to reach Groq: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while calling Groq", e);
        }
    }
}
