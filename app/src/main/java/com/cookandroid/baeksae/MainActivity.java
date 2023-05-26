package com.cookandroid.baeksae;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView cardView1 = findViewById(R.id.button_toNewsCategory);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NewCategoryAcivity로 이동
                Intent intent = new Intent(MainActivity.this, NewsCategoryActivity.class);
                startActivity(intent);
            }
        });

        CardView cardView2 = findViewById(R.id.button_toAiSearch);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // AiSearchActivity로 이동
                Intent intent = new Intent(MainActivity.this, AiSearchActivity.class);
                startActivity(intent);
            }
        });

        CardView cardView3 = findViewById(R.id.button_toEasyAi);
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // AiSearchActivity로 이동
                Intent intent = new Intent(MainActivity.this, EasyAiCategoryActivity.class);
                startActivity(intent);
            }
        });
    }
}