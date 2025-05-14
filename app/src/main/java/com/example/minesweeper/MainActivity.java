package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Button button4x4 = findViewById(R.id.button4x4);
        Button button8x8 = findViewById(R.id.button8x8);
        Button lightThemeButton = findViewById(R.id.lightThemeButton);
        Button darkThemeButton = findViewById(R.id.darkThemeButton);

        button4x4.setOnClickListener(v -> startGame(8, 8));
        button8x8.setOnClickListener(v -> startGame(12, 18));

        lightThemeButton.setOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        });

        darkThemeButton.setOnClickListener(v -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            recreate();
        });
    }

    private void startGame(int boardSize, int mines) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("BOARD_SIZE", boardSize);
        intent.putExtra("MINES", mines);
        startActivity(intent);
    }
}