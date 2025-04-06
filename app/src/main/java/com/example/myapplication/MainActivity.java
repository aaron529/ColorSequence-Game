package com.example.myapplication;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    Button startBtn, btnGreen, btnRed, btnYellow, btnBlue;
    TextView heading, score;
    Handler handler = new Handler();
    ArrayList<Integer> colors = new ArrayList<>();
    int currentMove = 0;
    boolean enabled = false;
    SharedPreferences.Editor editor;
    Set<String> history;
    Integer topScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("UserPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        history = new HashSet<>(sharedPreferences.getStringSet("history", new HashSet<>()));
        topScore = sharedPreferences.getInt("topscore", 0);
        startBtn = findViewById(R.id.startGameBtn);
        btnGreen = findViewById(R.id.btnGreen);
        btnRed = findViewById(R.id.btnRed);
        btnYellow = findViewById(R.id.btnYellow);
        btnBlue = findViewById(R.id.btnBlue);
        heading = findViewById(R.id.heading);
        score = findViewById(R.id.score);

        startBtn.setOnClickListener(view -> {
            startBtn.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Starting Game...", Toast.LENGTH_SHORT).show();
            resetGame();
            nextMove();
        });

        btnGreen.setOnClickListener(view -> handleClick(0));
        btnRed.setOnClickListener(view -> handleClick(1));
        btnYellow.setOnClickListener(view -> handleClick(2));
        btnBlue.setOnClickListener(view -> handleClick(3));
    }

    private void handleClick(int color) {
        if (!enabled) return;
        if (currentMove < colors.size() && colors.get(currentMove) == color) {
            allowMove();
        } else {
            denyMove();
        }
    }

    private void allowMove() {
        currentMove++;
        if (currentMove >= colors.size()) {
            currentMove = 0;
            nextMove();
        }
    }

    private void denyMove() {
        int score = colors.size() - 1;
        if (startBtn.isEnabled()) return;
        Toast.makeText(getApplicationContext(), "You LOST...", Toast.LENGTH_SHORT).show();
        String formatted = new SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale.getDefault()).format(new Date());
        history.add(formatted + " - Score: " + score);
        editor.putStringSet("history", new HashSet<>(history));
        if (topScore < score) topScore = score;
        editor.putInt("topscore", topScore);
        editor.commit();
        resetGame();
        heading.setText(R.string.player_lost);
        startBtn.setEnabled(true);
    }

    private void resetGame() {
        currentMove = 0;
        colors.clear();
    }

    private void nextMove() {
        score.setText("Score: " + colors.size());
        addRandomColor();
        showColor();
    }

    private void addRandomColor() {
        int randomNum = (int) (Math.random() * 4);
        colors.add(randomNum);
    }

    private void showColor() {
        heading.setText(R.string.watch);
        ArrayList<Integer> copy = new ArrayList<>(colors);
        Thread thread = new Thread(() -> {
            handler.post(() -> enabled = false);
            try {
                int delay = Math.max(300, 1000 - (colors.size() * 50));
                Thread.sleep(delay);
                for (Integer color : copy) {
                    Thread.sleep(delay);
                    highlightColor(color);
                    Thread.sleep(delay);
                    resetColor(color);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            handler.post(() -> {
                enabled = true;
                heading.setText(R.string.player_turn);

            });
        });
        thread.start();
    }

    private void highlightColor(int color) {
        handler.post(() -> {
            switch (color) {
                case 0:
                    btnGreen.setForeground(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.green_glow)));
                    break;
                case 1:
                    btnRed.setForeground(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.red_glow)));
                    break;
                case 2:
                    btnYellow.setForeground(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.yellow_glow)));
                    break;
                case 3:
                    btnBlue.setForeground(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.blue_glow)));
                    break;
            }
        });
    }

    private void resetColor(int color) {
        handler.post(() -> {
            switch (color) {
                case 0:
                    btnGreen.setForeground(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.green)));
                    break;
                case 1:
                    btnRed.setForeground(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.red)));
                    break;
                case 2:
                    btnYellow.setForeground(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.yellow)));
                    break;
                case 3:
                    btnBlue.setForeground(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.blue)));
                    break;
            }
        });
    }
}
