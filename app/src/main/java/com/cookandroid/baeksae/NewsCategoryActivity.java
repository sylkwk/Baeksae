package com.cookandroid.baeksae;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class NewsCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_category);

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        CardView cardView1 = findViewById(R.id.button_toEconomyNews);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NewsPageActivity로 이동
                Intent intent = new Intent(NewsCategoryActivity.this, NewsPageActivity.class);
                startActivity(intent);
            }
        });

        CardView cardView2 = findViewById(R.id.button_toPoliticNews);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NewsPageAcivity로 이동
                Intent intent = new Intent(NewsCategoryActivity.this, NewsPageActivity.class);
                startActivity(intent);
            }
        });

        CardView cardView3 = findViewById(R.id.button_toInternationalNews);
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NewsPageActivity로 이동
                Intent intent = new Intent(NewsCategoryActivity.this, NewsPageActivity.class);
                startActivity(intent);
            }
        });

        CardView cardView4 = findViewById(R.id.button_toEntertainmentNews);
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TestActivity로 이동
                Intent intent = new Intent(NewsCategoryActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

    }
}



