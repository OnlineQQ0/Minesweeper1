package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button4x4 = findViewById(R.id.button4x4);
        Button button8x8 = findViewById(R.id.button8x8);

        button4x4.setOnClickListener(v -> startGame(4, 4));
        button8x8.setOnClickListener(v -> startGame(8, 10));
    }

    private void startGame(int boardSize, int mines) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("BOARD_SIZE", boardSize);
        intent.putExtra("MINES", mines);
        startActivity(intent);
    }
}