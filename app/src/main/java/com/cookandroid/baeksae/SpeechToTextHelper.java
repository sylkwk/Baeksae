package com.cookandroid.baeksae;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Locale;

public class SpeechToTextHelper {
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private TextView textViewResponse;
    private ImageButton buttonSTT;
    private AppCompatActivity activity;

    public SpeechToTextHelper(AppCompatActivity activity, TextView textView) {
        this.activity = activity;
        this.textViewResponse = textView;
    }

    public void initializeViews(ImageButton buttonSTT) {
        this.buttonSTT = buttonSTT;
        this.buttonSTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });
    }

    private void startSpeechToText() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_CODE_SPEECH_INPUT);
        } else {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.KOREAN);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말씀해주세요...");

            try {
                activity.startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "음성 인식이 지원되지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String recognizedText = result.get(0);
            textViewResponse.setText(recognizedText);
        }
    }
}