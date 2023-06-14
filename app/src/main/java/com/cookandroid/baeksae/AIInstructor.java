package com.cookandroid.baeksae;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class AIInstructor extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    ImageButton sttButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    loading progress = new loading(AIInstructor.this);

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private SpeechToTextHelper speechToTextHelper;

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
        sttButton = findViewById(R.id.button_stt);

        welcomeTextView.setText("원하는 주제나 배경에 대한 간단한 소설을 작성해 드릴 수 있어요!");

        // Setup recycler view
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

        // Initialize SpeechToTextHelper
        speechToTextHelper = new SpeechToTextHelper(this, messageEditText);
        speechToTextHelper.initializeViews(sttButton);
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
                progress.closeDialog();
            }
        });
    }

    void callAPI(String question) {

        JSONArray messages = new JSONArray();
        JSONObject firstMessage = new JSONObject();
        try {
            firstMessage.put("role", "system");
            firstMessage.put("content", "저는 당신이 이야기꾼 역할을 해주길 바랍니다. 여러분은 관객들을 매료시키고, 상상력이 풍부하며, " +
                    "매혹적인 재미있는 이야기들을 생각해 낼 것입니다. 그것은 사람들의 관심과 상상력을 사로잡을 수 있는 동화, " +
                    "교육적인 이야기 또는 다른 종류의 이야기일 수 있습니다. 대상 독자에 따라 스토리텔링 세션의 특정 주제 또는 주제를 선택할 수 있습니다. " +
                    "예를 들어, 어린이일 경우 동물에 대해 이야기할 수 있습니다. " +
                    "어른일 경우 역사를 기반으로 한 이야기가 더 흥미를 끌 수 있습니다.");
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
                .header("Authorization", "Bearer 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")
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Call SpeechToTextHelper's onActivityResult
        speechToTextHelper.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SpeechToTextHelper.REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = result.get(0);
            addToChat(spokenText, Message.SENT_BY_ME);
            callAPI(spokenText);
            welcomeTextView.setVisibility(View.GONE);
        }
    }
}
