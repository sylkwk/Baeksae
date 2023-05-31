package com.cookandroid.baeksae;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TextSummarizer {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client;
    private TextView textView;


    public TextSummarizer(TextView textView) {
        client = new OkHttpClient();
        this.textView = textView;
    }

    public void summarizeText(String text) {
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        try {
            message.put("role", "user");
            message.put("content", "PARAPHRASE THIS" );
            messages.put(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addSummaryToTextView("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(jsonArray.length() - 1).getJSONObject("message").getString("content");
                        addSummaryToTextView(result.trim());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String responseBody = response.body().string();
                        addSummaryToTextView("Failed to load response due to " + responseBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void addSummaryToTextView(String summary) {
        textView.setText(summary);
    }
}


