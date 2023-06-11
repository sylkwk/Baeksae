package com.cookandroid.baeksae;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
import java.io.IOException;


public class NewsPageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    ImageButton sumbutton;

    private WebView webView;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    loading progress = new loading(NewsPageActivity.this);

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
        sumbutton = findViewById(R.id.button_summary);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        String startPageUrl = "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1=103";
        webView.loadUrl(startPageUrl);

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
    }

    void addResponse(String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewsPageActivity.this);
                builder.setMessage(response)
                        .setPositiveButton("OK", null)
                        .create()
                        .show();
            }
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
                .header("Authorization","Bearer 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")
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
