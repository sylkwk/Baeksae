package com.cookandroid.baeksae;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechToTextHelper {
    private Context mContext;
    private SpeechRecognizer mSpeechRecognizer;
    private boolean mIsListening;
    private SpeechToTextListener mListener;

    public SpeechToTextHelper(Context context, SpeechToTextListener listener) {
        mContext = context;
        mListener = listener;
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
    }

    public void startListening() {
        if (!mIsListening) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

            mSpeechRecognizer.startListening(intent);
            mIsListening = true;
        }
    }

    public void stopListening() {
        if (mIsListening) {
            mSpeechRecognizer.stopListening();
            mIsListening = false;
        }
    }

    public boolean isListening() {
        return mIsListening;
    }

    public void destroy() {
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
        }
    }

    private class SpeechRecognitionListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d("STT", "onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d("STT", "onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.d("STT", "onRmsChanged: " + rmsdB);
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d("STT", "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d("STT", "onEndOfSpeech");
        }

        @Override
        public void onError(int error) {
            Log.e("STT", "onError: " + error);
            mListener.onSpeechToTextError(error);
            mIsListening = false;
        }

        @Override
        public void onResults(Bundle results) {
            Log.d("STT", "onResults");
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty()) {
                String text = matches.get(0);
                mListener.onSpeechToTextResult(text);
            }
            mIsListening = false;
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d("STT", "onPartialResults");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d("STT", "onEvent");
        }
    }

    public interface SpeechToTextListener {
        void onSpeechToTextResult(String result);

        void onSpeechToTextError(int errorCode);
    }
}