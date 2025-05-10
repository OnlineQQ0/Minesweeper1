package com.example.minesweeper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private int boardSize;
    private int numMines;
    private Cell[][] cells;
    private GridLayout gridLayout;
    private TextView textViewGameInfo;
    private Button buttonNewGame;
    private Button buttonBack;
    private boolean gameActive = true;
    private int cellsRevealed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Получаем параметры игры
        boardSize = getIntent().getIntExtra("BOARD_SIZE", 4);
        numMines = getIntent().getIntExtra("NUM_MINES", 3);

        gridLayout = findViewById(R.id.gridLayoutMines);
        textViewGameInfo = findViewById(R.id.textViewGameInfo);
        buttonNewGame = findViewById(R.id.buttonNewGame);
        buttonBack = findViewById(R.id.buttonBack);

        textViewGameInfo.setText("Мины: " + numMines);

        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupGame();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupGame();
    }

    private void setupGame() {
        // Очищаем поле
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(boardSize);
        gridLayout.setRowCount(boardSize);

        // Инициализируем ячейки
        cells = new Cell[boardSize][boardSize];
        gameActive = true;
        cellsRevealed = 0;

        // Создаем кнопки для поля
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                cells[row][col] = new Cell(row, col);

                Button button = new Button(this);
                button.setLayoutParams(new GridLayout.LayoutParams());
                button.setMinimumWidth(0);
                button.setMinimumHeight(0);

                // Устанавливаем размер кнопки в зависимости от размера поля
                int cellSize = boardSize == 4 ? 100 : 60;
                button.setWidth(cellSize);
                button.setHeight(cellSize);

                button.setPadding(0, 0, 0, 0);
                button.setBackgroundResource(R.drawable.cell_button);

                final int finalRow = row;
                final int finalCol = col;

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (gameActive) {
                            handleCellClick(finalRow, finalCol);
                        }
                    }
                });

                cells[row][col].setButton(button);
                gridLayout.addView(button);
            }
        }

        // Расставляем мины
        placeMines();
        calculateNumbers();
    }

    private void placeMines() {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < numMines) {
            int row = random.nextInt(boardSize);
            int col = random.nextInt(boardSize);

            if (!cells[row][col].isMine()) {
                cells[row][col].setMine(true);
                minesPlaced++;
            }
        }
    }

    private void calculateNumbers() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (cells[row][col].isMine()) continue;

                int count = 0;

                // Проверяем все 8 соседних клеток
                for (int r = Math.max(0, row - 1); r <= Math.min(boardSize - 1, row + 1); r++) {
                    for (int c = Math.max(0, col - 1); c <= Math.min(boardSize - 1, col + 1); c++) {
                        if (r == row && c == col) continue;
                        if (cells[r][c].isMine()) count++;
                    }
                }

                cells[row][col].setAdjacentMines(count);
            }
        }
    }

    private void handleCellClick(int row, int col) {
        if (cells[row][col].isRevealed()) return;

        cells[row][col].reveal();
        cellsRevealed++;

        if (cells[row][col].isMine()) {
            // Попали на мину - проигрыш
            gameOver(false);
            return;
        }

        Button button = cells[row][col].getButton();

        if (cells[row][col].getAdjacentMines() > 0) {
            button.setText(String.valueOf(cells[row][col].getAdjacentMines()));

            // Устанавливаем цвет числа в зависимости от количества мин рядом
            switch (cells[row][col].getAdjacentMines()) {
                case 1:
                    button.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                    break;
                case 2:
                    button.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    break;
                case 3:
                    button.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    break;
                default:
                    button.setTextColor(getResources().getColor(android.R.color.holo_purple));
                    break;
            }
        } else {
            // Если рядом нет мин, открываем соседние клетки
            button.setText("");
            revealAdjacentCells(row, col);
        }

        // Проверяем, выиграл ли игрок
        if (cellsRevealed == (boardSize * boardSize) - numMines) {
            gameOver(true);
        }
    }

    private void revealAdjacentCells(int row, int col) {
        for (int r = Math.max(0, row - 1); r <= Math.min(boardSize - 1, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(boardSize - 1, col + 1); c++) {
                if (r == row && c == col) continue;
                if (!cells[r][c].isRevealed() && !cells[r][c].isMine()) {
                    handleCellClick(r, c);
                }
            }
        }
    }

    private void gameOver(boolean win) {
        gameActive = false;

        // Показываем все мины
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (cells[row][col].isMine()) {
                    Button button = cells[row][col].getButton();
                    button.setText("💣");
                }
            }
        }

        // Показываем сообщение о победе или проигрыше
        new AlertDialog.Builder(this)
                .setTitle(win ? "Победа!" : "Проигрыш!")
                .setMessage(win ? "Поздравляем! Вы успешно нашли все мины!" : "Вы попали на мину. Попробуйте еще раз!")
                .setPositiveButton("Новая игра", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setupGame();
                    }
                })
                .setNegativeButton("В меню", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    // Класс для представления ячейки игрового поля
    private class Cell {
        private int row;
        private int col;
        private boolean isMine;
        private int adjacentMines;
        private boolean isRevealed;
        private Button button;

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
            this.isMine = false;
            this.adjacentMines = 0;
            this.isRevealed = false;
        }

        public boolean isMine() {
            return isMine;
        }

        public void setMine(boolean mine) {
            isMine = mine;
        }

        public int getAdjacentMines() {
            return adjacentMines;
        }

        public void setAdjacentMines(int adjacentMines) {
            this.adjacentMines = adjacentMines;
        }

        public boolean isRevealed() {
            return isRevealed;
        }

        public void reveal() {
            isRevealed = true;
            button.setEnabled(false);
            button.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }

        public Button getButton() {
            return button;
        }

        public void setButton(Button button) {
            this.button = button;
        }
    }
}