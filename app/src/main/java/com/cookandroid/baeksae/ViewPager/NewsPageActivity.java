package com.cookandroid.baeksae.ViewPager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cookandroid.baeksae.R;
import com.cookandroid.baeksae.TextSummarizer;
import com.cookandroid.baeksae.TextToSpeechHelper;

public class NewsPageActivity extends AppCompatActivity {
    private TextToSpeechHelper mTTSHelper;
    private TextView mTextView;
    private ImageButton mButtonToggleTTS;

    private TextSummarizer textSummarizer;
    private ImageButton summaryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

        mTTSHelper = new TextToSpeechHelper(this);

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mButtonToggleTTS = findViewById(R.id.button_read);
        mButtonToggleTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTTS();
            }
        });

        mTextView = findViewById(R.id.textView_Article);

        // TextSummarizer 초기화
        textSummarizer = new TextSummarizer(mTextView);

        // 요약 버튼 초기화 및 클릭 이벤트 처리
        summaryButton = findViewById(R.id.button_summary);
        summaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToSummarize = mTextView.getText().toString();
                textSummarizer.summarizeText(textToSummarize);
            }
        });
    }

    private void toggleTTS() {
        if (mTTSHelper != null) {
            if (mTTSHelper.isSpeaking()) {
                stopTTS();
            } else {
                startTTS();
            }
        }
    }

    private void startTTS() {
        String text = mTextView.getText().toString();
        mTTSHelper.speak(text);
    }

    private void stopTTS() {
        mTTSHelper.stop();
    }

    @Override
    protected void onDestroy() {
        stopTTS();

        if (mTTSHelper != null) {
            mTTSHelper.shutdown();
        }

        super.onDestroy();
    }
}

//News API : 001f39ffc4a04b3ab713cd153b46ccf7