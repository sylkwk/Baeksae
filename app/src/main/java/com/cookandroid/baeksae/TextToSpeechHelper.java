package com.cookandroid.baeksae;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechHelper {
    public static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private TextToSpeech mTTS;
    private boolean mIsSpeaking;

    public TextToSpeechHelper(Context context) {
        mTTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.KOREAN);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "언어가 지원되지 않습니다");
                    }
                } else {
                    Log.e("TTS", "초기화에 실패했습니다");
                }
            }
        });
    }

    public void speak(String text) {
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId");
        mIsSpeaking = true;
    }

    public void stop() {
        mTTS.stop();
        mIsSpeaking = false;
    }

    public boolean isSpeaking() {
        return mIsSpeaking;
    }

    public void shutdown() {
        mTTS.shutdown();
    }
}
