package com.cookandroid.baeksae;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.os.Bundle;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.FrameLayout;
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

public class EasychefActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    loading progress = new loading(EasychefActivity.this);

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
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

        welcomeTextView.setText("원하는 메뉴나 재료를 입력해주세요!");


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
            welcomeTextView.setVisibility(View.GONE);
            progress.showDialog();
            callAPI(question);
        });

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
        progress.closeDialog();
    }

    void callAPI(String question) {

        JSONArray messages = new JSONArray();
        JSONObject firstMessage = new JSONObject();
        try {
            firstMessage.put("role", "system");
            firstMessage.put("content", "저는 영양학적으로 유익하지만 시간이 많이 소비되지 않는 음식을 포함한 맛있는 요리법을 제안할 수 있는 사람이 필요합니다. " +
                    "따라서 비용 효율성과 같은 다른 요소들 중에서 바쁜 사람들에게 적합하기 때문에 전체적인 요리는 건강하면서도 경제적입니다!");
            messages.put(firstMessage);
        } catch (JSONException e) {
            e.printStackTrace();
            progress.closeDialog();
        }


        // 모든 메시지 히스토리를 추가
        for (Message message : messageList) {
            JSONObject msg = new JSONObject();
            try {
                msg.put("role", message.getSentBy().equals(Message.SENT_BY_ME) ? "user" : "assistant");
                msg.put("content", message.getContent());
                messages.put(msg);
            } catch (JSONException e) {
                e.printStackTrace();
                progress.closeDialog();
            }
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("messages", messages);
        } catch (JSONException e) {
            e.printStackTrace();
            progress.closeDialog();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer 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")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());
                progress.closeDialog();
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
                        progress.closeDialog();
                    }
                } else {
                    try {
                        String responseBody = response.body().string();
                        addResponse("Failed to load response due to " + responseBody);
                        progress.closeDialog();
                    } catch (IOException e) {
                        e.printStackTrace();
                        progress.closeDialog();
                    }
                }
            }
        });
    }
}