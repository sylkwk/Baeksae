package com.cookandroid.baeksae;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AiSearchActivity extends AppCompatActivity {

    private FrameLayout processingScreen;
    private TextView textViewAiSearch;
    private ImageButton buttonSTT;
    private SpeechToTextHelper speechToTextHelper;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_search);

        processingScreen = findViewById(R.id.processing_summary);
        processingScreen.setVisibility(View.GONE);

        textViewAiSearch = findViewById(R.id.textView_AiSearch);
        buttonSTT = findViewById(R.id.button_stt);

        speechToTextHelper = new SpeechToTextHelper(this, textViewAiSearch);
        speechToTextHelper.initializeViews(buttonSTT);

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton summaryButton = findViewById(R.id.button_summary);
        summaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processingScreen.setVisibility(View.VISIBLE);
                startSpeechToText();
            }
        });

        client = new OkHttpClient();
    }

    private void startSpeechToText() {
        if (checkRecordAudioPermission()) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말씀해주세요...");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "음성 인식이 지원되지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            requestRecordAudioPermission();
        }
    }

    private boolean checkRecordAudioPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_CODE_SPEECH_INPUT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSpeechToText();
            } else {
                Toast.makeText(this, "음성 입력을 위해 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        speechToTextHelper.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String question = result.get(0);
            textViewAiSearch.setText(question);
            processingScreen.setVisibility(View.GONE);

            // 음성 인식 결과를 API로 전달하여 답변을 가져옵니다.
            callAPI(question);
        }
    }

    private void callAPI(String question) {
        // okhttp
        JSONArray messages = new JSONArray();

        // 모든 메시지 히스토리를 추가
        JSONObject userMessage = new JSONObject();
        try {
            userMessage.put("role", "user");
            userMessage.put("content", question);
            messages.put(userMessage);
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
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addResponse("Failed to load response due to " + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(jsonArray.length() - 1).getJSONObject("message").getString("content");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addResponse(result.trim());
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String responseBody = response.body().string();
                                addResponse("Failed to load response due to " + responseBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    private void addResponse(String response) {
        if (textViewAiSearch.getText().toString().isEmpty()) {
            textViewAiSearch.append(response);
        } else {
            textViewAiSearch.append("\n\n" + response);
        }
    }
}




/*        processingScreen = findViewById(R.id.processing_STT);
        processingScreen.setVisibility(View.GONE); // processing_summary 초기 상태를 숨김으로 설정

        ImageButton sttButton = findViewById(R.id.button_stt);
        sttButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processingScreen.setVisibility(View.VISIBLE); // processing_screen을 보이도록 설정
            }
        });*/

