package com.cookandroid.baeksae;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class EasyAiCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_ai_category);

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //뒤로가기 (EasyAiCategoryActivity->MainActivity)

        CardView cardView1 = findViewById(R.id.button_toEasyDoctorActivity);
        CardView cardView2 = findViewById(R.id.button_TravelPlanner);
        CardView cardView3 = findViewById(R.id.button_AIInstructor);
        CardView cardView4 = findViewById(R.id.button_toeasychef);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EasyAiCategoryActivity.this, EasyDoctorActivity.class);
                startActivity(intent);
            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EasyAiCategoryActivity.this, TravelPlannerActivity.class);
                startActivity(intent);
            }
        });
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NewCategoryAcivity로 이동
                Intent intent = new Intent(EasyAiCategoryActivity.this, AIInstructor.class);
                startActivity(intent);
            }
        });
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NewCategoryAcivity로 이동
                Intent intent = new Intent(EasyAiCategoryActivity.this, EasychefActivity.class);
                startActivity(intent);
            }
        });



    }
}