package com.example.xo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView messageTextView;
    private Button restartButton;
    private final Cell[][] cells;
    private boolean xTurn;
    private int numFilledCells;
    private boolean waitingForRestart;

    public MainActivity() {
        cells = new Cell[3][3];
        xTurn = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageTextView = findViewById(R.id.txtview_message);
        initRestartButton();
        initCells();
    }

    private void initRestartButton() {
        restartButton = findViewById(R.id.btn_restart);

        restartButton.setOnClickListener(v -> restartGame());
    }

    private void initCells() {
        cells[0][0] = new Cell(findViewById(R.id.imgview_cell_1));
        cells[0][1] = new Cell(findViewById(R.id.imgview_cell_2));
        cells[0][2] = new Cell(findViewById(R.id.imgview_cell_3));
        cells[1][0] = new Cell(findViewById(R.id.imgview_cell_4));
        cells[1][1] = new Cell(findViewById(R.id.imgview_cell_5));
        cells[1][2] = new Cell(findViewById(R.id.imgview_cell_6));
        cells[2][0] = new Cell(findViewById(R.id.imgview_cell_7));
        cells[2][1] = new Cell(findViewById(R.id.imgview_cell_8));
        cells[2][2] = new Cell(findViewById(R.id.imgview_cell_9));

        for (int rowIdx = 0; rowIdx < cells.length; rowIdx++) {
            Cell[] row = cells[rowIdx];
            final int finalY = rowIdx;
            for (int columnIdx = 0; columnIdx < row.length; columnIdx++) {
                Cell cell = row[columnIdx];
                final int finalX = columnIdx;
                cell.image.setOnClickListener(v -> {
                    if (waitingForRestart || cell.state != CellState.Empty) {
                        return;
                    }

                    if (xTurn) {
                        cell.setX();
                        messageTextView.setText(getResources().getText(R.string.o_turn));
                    } else {
                        cell.setO();
                        messageTextView.setText(getResources().getText(R.string.x_turn));
                    }

                    numFilledCells++;

                    if (hasWinner(finalY, finalX)) {
                        endGameWithWinner();
                    } else if (numFilledCells == 9) {
                        endGameWithoutWinner();
                    }

                    xTurn = !xTurn;
                });
            }
        }
    }

    private void endGameWithoutWinner() {
        waitingForRestart = true;

        CharSequence drawText = getResources().getText(R.string.draw);
        messageTextView.setText(drawText);
        Toast.makeText(this, drawText, Toast.LENGTH_SHORT).show();
    }

    private void endGameWithWinner() {
        CharSequence winText;
        if (xTurn) {
            winText = getResources().getText(R.string.x_won);
        } else {
            winText = getResources().getText(R.string.o_won);
        }

        messageTextView.setText(winText);
        Toast.makeText(this, winText, Toast.LENGTH_SHORT).show();

        waitingForRestart = true;
    }

    private void restartGame() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.clear();
            }
        }

        messageTextView.setText(getResources().getText(R.string.x_turn));
        xTurn = true;
        numFilledCells = 0;
        waitingForRestart = false;
    }


    private boolean hasWinner(int changedRow, int changedColumn) {
        if (hasRowWin(changedRow)) {
            return true;
        }

        if (hasColumnWin(changedColumn)) {
            return true;
        }

        return hasDiagonalWin();
    }

    private boolean hasDiagonalWin() {
        if (cells[0][0].state == cells[1][1].state
                && cells[1][1].state == cells[2][2].state
                && cells[2][2].state != CellState.Empty) {
            return true;
        }

        return cells[0][2].state == cells[1][1].state
                && cells[1][1].state == cells[2][0].state
                && cells[2][0].state != CellState.Empty;
    }

    private boolean hasColumnWin(int column) {
        return cells[0][column].state == cells[1][column].state
                && cells[1][column].state == cells[2][column].state
                && cells[2][column].state != CellState.Empty;
    }

    private boolean hasRowWin(int row) {
        return cells[row][0].state == cells[row][1].state
                && cells[row][1].state == cells[row][2].state
                && cells[row][2].state != CellState.Empty;
    }

    private enum CellState {
        Empty, X, O,
    }

    private static class Cell {
        public ImageView image;
        public CellState state;

        public Cell(ImageView image) {
            this.image = image;
            this.state = CellState.Empty;
        }

        public void setX() {
            image.setImageResource(R.drawable.cell_x);
            state = CellState.X;
        }

        public void setO() {
            image.setImageResource(R.drawable.cell_o);
            state = CellState.O;
        }

        public void clear() {
            image.setImageResource(R.drawable.cell_empty);
            state = CellState.Empty;
        }
    }
}