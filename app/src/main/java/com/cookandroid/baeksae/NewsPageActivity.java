package com.cookandroid.baeksae;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class NewsPageActivity extends AppCompatActivity {
    private TextToSpeech mTTS; // 텍스트를 음성으로 변환하기 위한 TextToSpeech 객체
    private TextView mTextView; // 기사를 표시하는 TextView
    private ImageButton mButtonRead; // 텍스트를 음성으로 변환하는 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_page);

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 현재 액티비티를 종료하고 닫음
            }
        });

        mButtonRead = findViewById(R.id.button_read);

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) { // TextToSpeech 엔진 초기화에 성공한 경우
                    int result = mTTS.setLanguage(Locale.KOREAN); // 한국어로 언어 설정

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS","언어가 지원되지 않습니다"); // 선택한 언어가 지원되지 않을 경우 오류 메시지 출력
                    } else {
                        mButtonRead.setEnabled(true); // TextToSpeech 엔진이 준비된 상태이므로 읽기 버튼 활성화
                    }
                } else {
                    Log.e("TTS","초기화에 실패했습니다"); // TextToSpeech 엔진 초기화 실패시 오류 메시지 출력
                }
            }
        });

        mTextView = findViewById(R.id.textView_Article);

        mButtonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak(); // 읽기 메서드 호출하여 텍스트를 음성으로 변환
            }
        });
    }

    private void speak() {
        String text = mTextView.getText().toString(); // TextView에서 텍스트 가져오기

        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null); // 텍스트를 음성으로 변환하여 읽음
    }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop(); // 음성 합성 정지
            mTTS.shutdown(); // TextToSpeech 엔진 종료
        }

        super.onDestroy();
    }
}