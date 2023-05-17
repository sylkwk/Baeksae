package com.cookandroid.baeksae;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView cardView2 = findViewById(R.id.button_toNewsCategory);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NewCategoryAcivity로 이동
                Intent intent = new Intent(MainActivity.this, NewsCategoryActivity.class);
                startActivity(intent);
            }
        });

        CardView cardView1 = findViewById(R.id.button_toAiSearch);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // AiSearchActivity로 이동
                Intent intent = new Intent(MainActivity.this, AiSearchActivity.class);
                startActivity(intent);
            }
        });
    }
}