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
    private TextToSpeech mTTS;
    private TextView mTextView;
    private ImageButton mButtonToggleTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

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

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.KOREAN);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "언어가 지원되지 않습니다");
                    } else {
                        mButtonToggleTTS.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "초기화에 실패했습니다");
                }
            }
        });

        mTextView = findViewById(R.id.textView_Article);
    }

    private void toggleTTS() {
        if (mTTS.isSpeaking()) {
            stopTTS();
        } else {
            startTTS();
        }
    }

    private void startTTS() {
        String text = mTextView.getText().toString();

        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId");
    }

    private void stopTTS() {
        mTTS.stop();
    }

    @Override
    protected void onDestroy() {
        stopTTS();

        if (mTTS != null) {
            mTTS.shutdown();
        }

        super.onDestroy();
    }
}