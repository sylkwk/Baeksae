package com.cookandroid.baeksae;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity implements SpeechToTextHelper.SpeechToTextListener {

    private TextView textViewResponse;
    private Button buttonRequest;
    private SpeechToTextHelper speechToTextHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textViewResponse = findViewById(R.id.textView_response);
        buttonRequest = findViewById(R.id.button_request);

        // SpeechToTextHelper 인스턴스를 생성합니다.
        speechToTextHelper = new SpeechToTextHelper(this, this);

        // 요청 버튼에 클릭 리스너를 설정합니다.
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 음성 입력을 위해 듣기 시작합니다.
                speechToTextHelper.startListening();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // SpeechToTextHelper 인스턴스를 정리합니다.
        speechToTextHelper.destroy();
    }

    @Override
    public void onSpeechToTextResult(String result) {
        // TextView에 음성 인식 결과를 표시합니다.
        textViewResponse.setText(result);
    }

    @Override
    public void onSpeechToTextError(int errorCode) {
        // 음성 인식 중에 발생하는 오류를 처리합니다.
        // 예를 들어, 사용자에게 오류 메시지를 표시할 수 있습니다.
        textViewResponse.setText("음성 인식 오류: " + errorCode);
    }
}



