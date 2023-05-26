package com.cookandroid.baeksae;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

public class AiSearchActivity extends AppCompatActivity {

    private FrameLayout processingScreen;

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
        processingScreen.setVisibility(View.GONE); // processing_summary 초기 상태를 숨김으로 설정

        ImageButton summaryButton = findViewById(R.id.button_summary);
        summaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processingScreen.setVisibility(View.VISIBLE); // processing_screen을 보이도록 설정
            }
        });

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
