package com.cookandroid.baeksae;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {
    private TextView textViewA;
    private TextView textViewB;
    private TextSummarizer textSummarizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textViewA = findViewById(R.id.textViewA);
        textViewB = findViewById(R.id.textViewB);
        textSummarizer = new TextSummarizer(textViewB);

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
                String textToSummarize = textViewA.getText().toString();
                textSummarizer.summarizeText(textToSummarize);
            }
        });
    }
}