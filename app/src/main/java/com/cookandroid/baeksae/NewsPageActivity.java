package com.cookandroid.baeksae;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsPageActivity extends AppCompatActivity {

    private WebView webView;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private loading progress;
    private TextToSpeechHelper mTTSHelper;

    // tts 실행여부 추적변수
    boolean ttsPlaying = false;

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

        webView = findViewById(R.id.webview2);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        String startPageUrl = "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=103";
        webView.loadUrl(startPageUrl);

        progress = new loading(NewsPageActivity.this);
        mTTSHelper = new TextToSpeechHelper(this);

        //뒤로가기
        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener((v) -> {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        });

        //앞으로가기
        ImageButton forwardButton = findViewById(R.id.button_next);
        forwardButton.setOnClickListener((v) -> {
            if (webView.canGoForward()) {
                webView.goForward();
            }
        });

        //요약버튼
        ImageButton sumbutton = findViewById(R.id.button_summary);
        sumbutton.setOnClickListener((v) -> {
            String currentUrl = webView.getUrl();

            new Thread(() -> {
                try {
                    callAPI(currentUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        });

        //읽기버튼
        ImageButton readbutton = findViewById(R.id.button_read);
        readbutton.setOnClickListener((v) -> {
            String currentUrl = webView.getUrl();

            new Thread(() -> {
                try {
                    if (ttsPlaying) {
                        mTTSHelper.stop();
                        ttsPlaying = false;
                    } else {
                        String textToRead = extractArticle(currentUrl);
                        mTTSHelper.speak(textToRead);
                        ttsPlaying = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    @Override
    protected void onDestroy() {
        mTTSHelper.shutdown();
        super.onDestroy();
    }

    String extractArticle(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element newsctArticleElement = doc.select("div.newsct_article").first();
        return newsctArticleElement.text();
    }

    void addResponse(String response) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(NewsPageActivity.this);
            builder.setMessage(response)
                    .setPositiveButton("닫기", null)
                    .create()
                    .show();
        });
    }

    void callAPI(String url) throws IOException {
        System.out.println("url: " + url);
        JSONArray messages = new JSONArray();

        JSONObject firstMessage = new JSONObject();
        Document doc = Jsoup.connect(url).get();
        String pageTitle = doc.title();
        Element newsctArticleElement = doc.select("div.newsct_article").first();
        String article = newsctArticleElement.text();

        System.out.println("title: " + pageTitle);
        System.out.println("paragraphs: " + article);
        try {
            firstMessage.put("role", "system");
            firstMessage.put("content", "이 기사를 요약해");
            messages.put(firstMessage);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject msg = new JSONObject();
        try {
            msg.put("role", "user");
            msg.put("content", "기사 제목 : " + pageTitle + "\n\n" + "기사 내용 : " + article + "이 내용을 한국어로 요약해줘");
            messages.put(msg);
        } catch (JSONException e) {
            e.printStackTrace();
            progress.closeDialog();
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
                        progress.closeDialog();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
