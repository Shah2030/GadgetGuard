package com.example.gadgetguard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

public class ChatBotActivity extends AppCompatActivity {

    private static final String API_KEY = "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";
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    private EditText userInput;
    private Button sendButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private final List<ChatMessage> chatMessages = new ArrayList<>();

    OkHttpClient client = new OkHttpClient();
    MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        userInput = findViewById(R.id.userInput);
        sendButton = findViewById(R.id.sendBtn);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> {
            String message = userInput.getText().toString().trim();
            if (!message.isEmpty()) {
                addMessage(message, true);
                userInput.setText("");
                getAIResponse(message);
            }
        });
    }

    private void addMessage(String text, boolean isUser) {
        runOnUiThread(() -> {
            chatMessages.add(new ChatMessage(text, isUser));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
        });
    }

    private void getAIResponse(String userMessage) {
        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("model", "mistralai/mistral-7b-instruct");

                JSONArray messages = new JSONArray();
                messages.put(new JSONObject().put("role", "system").put("content", "You are a helpful study assistant."));
                messages.put(new JSONObject().put("role", "user").put("content", userMessage));
                body.put("messages", messages);

                Request request = new Request.Builder()
                        .url(API_URL)
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("HTTP-Referer", "https://example.com")
                        .addHeader("X-Title", "GadgetGuard Chatbot")
                        .post(RequestBody.create(body.toString(), JSON))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String reply = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addMessage(reply.trim(), false);
                    } else {
                        addMessage("Error: API call failed", false);
                    }
                }
            } catch (Exception e) {
                addMessage("Exception: " + e.getMessage(), false);
            }
        }).start();
    }
}
