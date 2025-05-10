package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button4x4 = findViewById(R.id.button4x4);
        Button button8x8 = findViewById(R.id.button8x8);

        // Используем лямбда-выражения для компактности кода
        button4x4.setOnClickListener(v -> startGame(4, 3)); // 4x4 поле с 3 минами
        button8x8.setOnClickListener(v -> startGame(8, 10)); // 8x8 поле с 10 минами
    }

    private void startGame(int boardSize, int numMines) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("BOARD_SIZE", boardSize);
        intent.putExtra("NUM_MINES", numMines);
        startActivity(intent);
    }
}