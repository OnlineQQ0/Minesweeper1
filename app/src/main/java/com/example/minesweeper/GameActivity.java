package com.example.minesweeper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private GridLayout gridLayoutMines;
    private TextView textViewGameInfo;
    private TextView stopwatchText;
    private Button buttonNewGame;
    private Button buttonBack;
    private int boardSize;
    private int minesCount;
    private Cell[][] cells;
    private boolean isFirstClick = true;
    private Handler handler;
    private Runnable stopwatchRunnable;
    private long startTime;
    private boolean isStopwatchRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridLayoutMines = findViewById(R.id.gridLayoutMines);
        textViewGameInfo = findViewById(R.id.textViewGameInfo);
        stopwatchText = findViewById(R.id.stopwatchText);
        buttonNewGame = findViewById(R.id.buttonNewGame);
        buttonBack = findViewById(R.id.buttonBack);

        boardSize = getIntent().getIntExtra("BOARD_SIZE", 4);
        minesCount = getIntent().getIntExtra("MINES", 4);

        isStopwatchRunning = false;
        startTime = 0;
        stopwatchText.setText("Time: 0s");
        handler = new Handler(Looper.getMainLooper());

        initGame();

        buttonNewGame.setOnClickListener(v -> {
            isFirstClick = true;
            stopStopwatch();
            stopwatchText.setText("Time: 0s");
            initGame();
        });

        buttonBack.setOnClickListener(v -> finish());
    }

    private void initGame() {
        cells = new Cell[boardSize][boardSize];
        gridLayoutMines.removeAllViews();
        gridLayoutMines.setRowCount(boardSize);
        gridLayoutMines.setColumnCount(boardSize);

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                cells[i][j] = new Cell(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 100;
                params.height = 100;
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                cells[i][j].setLayoutParams(params);
                cells[i][j].setText("");
                cells[i][j].setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                cells[i][j].setTextColor(getResources().getColor(android.R.color.black));
                final int row = i;
                final int col = j;
                cells[i][j].setOnClickListener(v -> handleCellClick(row, col));
                gridLayoutMines.addView(cells[i][j]);
            }
        }

        textViewGameInfo.setText("Mines: " + minesCount);
    }

    private void handleCellClick(int row, int col) {
        if (isFirstClick) {
            isFirstClick = false;
            placeMines(row, col);
            calculateNumbers();
            openInitialArea(row, col);
            startStopwatch();
        } else {
            openCell(row, col);
            checkGameStatus();
        }
    }

    private void placeMines(int firstRow, int firstCol) {
        Random random = new Random();
        int placedMines = 0;
        while (placedMines < minesCount) {
            int r = random.nextInt(boardSize);
            int c = random.nextInt(boardSize);
            boolean isSafeZone = boardSize == 4 ?
                    (Math.abs(r - firstRow) <= 1 && Math.abs(c - firstCol) <= 1) :
                    (Math.abs(r - firstRow) <= 2 && Math.abs(c - firstCol) <= 2);
            if (!isSafeZone && !cells[r][c].isMine()) {
                cells[r][c].setMine(true);
                placedMines++;
            }
        }
    }

    private void calculateNumbers() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (!cells[i][j].isMine()) {
                    int count = 0;
                    for (int di = -1; di <= 1; di++) {
                        for (int dj = -1; dj <= 1; dj++) {
                            int ni = i + di;
                            int nj = j + dj;
                            if (ni >= 0 && ni < boardSize && nj >= 0 && nj < boardSize && cells[ni][nj].isMine()) {
                                count++;
                            }
                        }
                    }
                    cells[i][j].setNumber(count);
                }
            }
        }
    }

    private void openInitialArea(int row, int col) {
        int range = boardSize == 4 ? 1 : 2;
        for (int di = -range; di <= range; di++) {
            for (int dj = -range; dj <= range; dj++) {
                int ni = row + di;
                int nj = col + dj;
                if (ni >= 0 && ni < boardSize && nj >= 0 && nj < boardSize) {
                    openCell(ni, nj);
                }
            }
        }
    }

    private void openCell(int row, int col) {
        if (row < 0 || row >= boardSize || col < 0 || col >= boardSize || cells[row][col].isOpen()) {
            return;
        }
        cells[row][col].setOpen(true);
        if (cells[row][col].isMine()) {
            revealMines();
            stopStopwatch();
        } else {
            int number = cells[row][col].getNumber();
            cells[row][col].setText(number > 0 ? String.valueOf(number) : "");
            cells[row][col].setBackgroundColor(getResources().getColor(android.R.color.white));
            switch (number) {
                case 1:
                    cells[row][col].setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                    break;
                case 2:
                    cells[row][col].setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    break;
                case 3:
                    cells[row][col].setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    break;
                case 4:
                    cells[row][col].setTextColor(getResources().getColor(android.R.color.holo_purple));
                    break;
                default:
                    cells[row][col].setTextColor(getResources().getColor(android.R.color.black));
                    break;
            }
            if (number == 0) {
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        openCell(row + di, col + dj);
                    }
                }
            }
        }
    }

    private void checkGameStatus() {
        int closedNonMines = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (!cells[i][j].isOpen() && !cells[i][j].isMine()) {
                    closedNonMines++;
                }
            }
        }
        if (closedNonMines == 0) {
            revealMines();
            stopStopwatch();
        }
    }

    private void revealMines() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (cells[i][j].isMine()) {
                    cells[i][j].setText("X");
                    cells[i][j].setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                }
            }
        }
    }

    private void startStopwatch() {
        isStopwatchRunning = true;
        startTime = System.currentTimeMillis();
        stopwatchRunnable = new Runnable() {
            @Override
            public void run() {
                if (isStopwatchRunning) {
                    long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                    stopwatchText.setText("Time: " + elapsedSeconds + "s");
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(stopwatchRunnable);
    }

    private void stopStopwatch() {
        isStopwatchRunning = false;
        if (stopwatchRunnable != null) {
            handler.removeCallbacks(stopwatchRunnable);
        }
    }

    private static class Cell extends AppCompatButton {
        private boolean isMine;
        private boolean isOpen;
        private int number;

        public Cell(Context context) {
            super(context);
            isMine = false;
            isOpen = false;
            number = 0;
        }

        public boolean isMine() {
            return isMine;
        }

        public void setMine(boolean mine) {
            isMine = mine;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}