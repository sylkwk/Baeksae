package com.cookandroid.baeksae;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class NewsPageActivity extends AppCompatActivity {
    private TextToSpeechHelper mTTSHelper;
    private TextView mTextView;
    private ImageButton mButtonToggleTTS;

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