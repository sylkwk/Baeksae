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

        processingScreen = findViewById(R.id.processing_screen_layout);
        processingScreen.setVisibility(View.GONE); // processing_screen 초기 상태를 숨김으로 설정

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton sttButton = findViewById(R.id.button_summary);
        sttButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processingScreen.setVisibility(View.VISIBLE); // processing_screen을 보이도록 설정
            }
        });
    }
}
