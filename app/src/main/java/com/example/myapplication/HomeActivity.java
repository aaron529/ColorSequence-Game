package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    Integer topScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);
        findViewById(R.id.homeStartbtn).setOnClickListener((View view) -> {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        });
        findViewById(R.id.historyBtn).setOnClickListener((View view) -> {
            Intent i = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivity(i);
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("UserPref", MODE_PRIVATE);
        topScore = sharedPreferences.getInt("topscore", 0);
        ((TextView) findViewById(R.id.highscore)).setText("Highscore: " + topScore);
    }
}
