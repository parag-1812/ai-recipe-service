# ai-recipe-service

The AI layer of the Cloud Kitchen platform. Turns a customer's natural-language
food request into a structured, kitchen-ready dish.

```
POST /ai/parse-dish        (requires a customer JWT with role USER)
{ "text": "boneless chicken in tomato gravy, medium spicy, extra garlic, less oil" }

-> { "dishType": "NON_VEG", "protein": "Chicken",
     "ingredients": ["Tomato","Garlic"], "cookingStyle": "Curry",
     "spiceLevel": "Medium", "oilLevel": "Less Oil",
     "dietaryTags": [], "specialInstructions": "boneless",
     "summary": "Boneless chicken in a medium-spicy tomato gravy ..." }
```

## Stack

- Java 21, Spring Boot 4, Spring Security (stateless — no database)
- Verifies JWTs with the shared RS256 **public** key (cannot mint tokens)
- Calls an **OpenAI-compatible** chat API in JSON mode via the JDK `HttpClient`
  (no vendor SDK). Default provider is **Groq** (free tier).

## Run

Needs a free API key from <https://console.groq.com>:

```bash
# Linux/macOS
export GROQ_API_KEY=gsk_...
# Windows PowerShell
$env:GROQ_API_KEY = "gsk_..."

./mvnw spring-boot:run      # starts on http://localhost:8082
```

Configuration (env overrides, see `application.properties`):

| Variable | Default |
|---|---|
| `GROQ_API_KEY` | _(required)_ |
| `GROQ_BASE_URL` | `https://api.groq.com/openai/v1` |
| `GROQ_MODEL` | `openai/gpt-oss-120b` |
| `JWT_PUBLIC_KEY` | `classpath:keys/jwt-public.pem` |

Swap `GROQ_BASE_URL`/`GROQ_MODEL` to point at OpenAI, a local Ollama, etc.

> The API key is read from the environment and is **never** committed. The local
> `.groq-key` helper file is git-ignored.
