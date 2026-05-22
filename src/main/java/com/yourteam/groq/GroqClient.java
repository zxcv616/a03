package com.yourteam.groq;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;

/**
 * Handles all HTTP communication with the Groq chat completions API.
 * Sends a full conversation history with a system context and returns
 * the model's plain-text reply.
 *
 * @author Matthew Wiecking
 * @version 1.0
 */
public class GroqClient {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.1-8b-instant";

    private final String apiKey;
    private final OkHttpClient httpClient;

    /**
     * @param apiKey the Groq API key for this session
     */
    public GroqClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
    }

    /**
     * Sends the full conversation history to Groq and returns the model's reply.
     *
     * @param systemContext system-level context describing the app state
     * @param history       the conversation so far as a list of role/content message objects
     * @return the model's response text
     * @throws IOException if the request fails
     */
    public String chat(String systemContext, List<JSONObject> history) throws IOException {
        JSONArray messages = new JSONArray();

        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemContext);
        messages.put(systemMsg);

        for (JSONObject msg : history) {
            messages.put(msg);
        }

        JSONObject body = new JSONObject();
        body.put("model", MODEL);
        body.put("messages", messages);

        RequestBody requestBody = RequestBody.create(
            body.toString(),
            MediaType.get("application/json")
        );

        Request request = new Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String raw = response.body().string();
            JSONObject json = new JSONObject(raw);
            return json.getJSONArray("choices")
                       .getJSONObject(0)
                       .getJSONObject("message")
                       .getString("content");
        }
    }
}
