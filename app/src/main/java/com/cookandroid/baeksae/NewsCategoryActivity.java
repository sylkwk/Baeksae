package com.cookandroid.baeksae;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.cardview.widget.CardView;

        import android.os.Bundle;
        import android.content.Intent;
        import android.view.View;
        import android.widget.Button;

public class NewsCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_category);

        CardView cardView2 = findViewById(R.id.button_toEntertainmentNews);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NewsPageAcivity로 이동
                Intent intent = new Intent(NewsCategoryActivity.this, NewsPageActivity.class);
                startActivity(intent);
            }
        });

        CardView cardView1 = findViewById(R.id.button_toEconomyNews);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // AiSearchActivity로 이동
                Intent intent = new Intent(NewsCategoryActivity.this, NewsPageActivity.class);
                startActivity(intent);
            }
        });

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}