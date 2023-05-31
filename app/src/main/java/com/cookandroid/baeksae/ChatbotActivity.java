package com.cookandroid.baeksae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatbotActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        welcomeTextView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        //setup recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v) -> {
            String question = messageEditText.getText().toString().trim();
            addToChat(question, Message.SENT_BY_ME);
            messageEditText.setText("");
            callAPI(question);
            welcomeTextView.setVisibility(View.GONE);
        });

        // 이전 대화 로드
        loadPreviousChat();
    }

    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!messageList.isEmpty() && messageList.get(messageList.size() - 1).getSentBy().equals(sentBy)) {
                    // 이전 대화 내용과 같은 송신자에게서의 메시지인 경우
                    Message lastMessage = messageList.get(messageList.size() - 1);
                    lastMessage.setContent(lastMessage.getContent() + "\n" + message);
                    messageAdapter.notifyItemChanged(messageList.size() - 1);
                } else {
                    // 새로운 송신자에게서의 메시지인 경우
                    messageList.add(new Message(message, sentBy));
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                }
            }
        });
    }

    void addResponse(String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!messageList.isEmpty()) {
                    messageList.remove(messageList.size() - 1);
                }
                messageList.add(new Message(response, Message.SENT_BY_BOT));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void loadPreviousChat() {
        // 이전 대화 내용을 로드하는 로직
        // 파일이나 데이터베이스에서 이전 대화 내용을 읽어와서 `messageList`에 추가
        // 추가된 메시지들은 `messageAdapter.notifyDataSetChanged()`를 호출하여 RecyclerView에 표시
        // 예시로 이전 대화를 두 개의 메시지로 가정하고 추가하는 코드
/*        addToChat("안녕하세요!", Message.SENT_BY_BOT);
        addToChat("반갑습니다.", Message.SENT_BY_ME);*/
    }

    void callAPI(String question) {
        // okhttp
        messageList.add(new Message("입력중...", Message.SENT_BY_BOT));

        JSONArray messages = new JSONArray();

        // 모든 메시지 히스토리를 추가
        for (Message message : messageList) {
            JSONObject msg = new JSONObject();
            try {
                msg.put("role", message.getSentBy().equals(Message.SENT_BY_ME) ? "user" : "assistant");
                msg.put("content", message.getContent());
                messages.put(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                addResponse("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(jsonArray.length() - 1).getJSONObject("message").getString("content");
                        addResponse(result.trim());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String responseBody = response.body().string();
                        addResponse("Failed to load response due to " + responseBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
