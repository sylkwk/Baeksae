package com.cookandroid.baeksae;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class AiSearchActivity extends AppCompatActivity {

    private FrameLayout processingScreen;
    private TextView textViewAiSearch;
    private SpeechToTextHelper speechToTextHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_search);

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        processingScreen = findViewById(R.id.processing_summary);
        processingScreen.setVisibility(View.GONE);

        textViewAiSearch = findViewById(R.id.textView_AiSearch);
        ImageButton buttonSTT = findViewById(R.id.button_stt);

        speechToTextHelper = new SpeechToTextHelper(this, textViewAiSearch);
        speechToTextHelper.initializeViews(buttonSTT);

        ImageButton summaryButton = findViewById(R.id.button_summary);
        summaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processingScreen.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        speechToTextHelper.onActivityResult(requestCode, resultCode, data);

/*        processingScreen = findViewById(R.id.processing_STT);
        processingScreen.setVisibility(View.GONE); // processing_summary 초기 상태를 숨김으로 설정

        ImageButton sttButton = findViewById(R.id.button_stt);
        sttButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processingScreen.setVisibility(View.VISIBLE); // processing_screen을 보이도록 설정
            }
        });*/
    }
}
