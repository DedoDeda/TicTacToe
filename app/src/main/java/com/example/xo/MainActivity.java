package com.example.xo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /** The text that displays all the messages to the players. */
    private TextView messageTextView;
    /** The button to restart the game. */
    private Button restartButton;
    /** A cell matrix representing the board. */
    private final Cell[][] board;
    /** Is it X's turn now? */
    private boolean xTurn;
    /** The number of used cells. */
    private int numFilledCells;
    /** Are we waiting for restart (after win, draw...). */
    private boolean waitingForRestart;

    /** Constructs an empty default board. */
    public MainActivity() {
        board = new Cell[3][3];
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
        // Bind to the restart button click event.
        restartButton.setOnClickListener(v -> restartGame());
    }

    private void initCells() {
        // Initialize the board with the views.
        board[0][0] = new Cell(findViewById(R.id.imgview_cell_1));
        board[0][1] = new Cell(findViewById(R.id.imgview_cell_2));
        board[0][2] = new Cell(findViewById(R.id.imgview_cell_3));
        board[1][0] = new Cell(findViewById(R.id.imgview_cell_4));
        board[1][1] = new Cell(findViewById(R.id.imgview_cell_5));
        board[1][2] = new Cell(findViewById(R.id.imgview_cell_6));
        board[2][0] = new Cell(findViewById(R.id.imgview_cell_7));
        board[2][1] = new Cell(findViewById(R.id.imgview_cell_8));
        board[2][2] = new Cell(findViewById(R.id.imgview_cell_9));

        // Bind to each cell's click event.
        for (int rowIdx = 0; rowIdx < board.length; rowIdx++) {
            Cell[] row = board[rowIdx];
            final int finalY = rowIdx;
            for (int columnIdx = 0; columnIdx < row.length; columnIdx++) {
                Cell cell = row[columnIdx];
                final int finalX = columnIdx;
                cell.image.setOnClickListener(v -> {
                    // Do nothing if we're waiting for restart or if this cell is already taken.
                    if (waitingForRestart || cell.state != CellState.Empty) {
                        return;
                    }

                    // Mark the cell as taken and update the turn state message accordingly.
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
                    } else if (isBoardFull()) {
                        // If the board is full and we don't have a winner yet it's a draw.
                        endGameWithDraw();
                    }

                    xTurn = !xTurn;
                });
            }
        }
    }

    /** Are all the cells in the board taken? */
    private boolean isBoardFull() {
        return numFilledCells == 9;
    }

    /** Ends the game displaying a draw message. */
    private void endGameWithDraw() {
        // Lock the board until a restart.
        waitingForRestart = true;

        // Display the message.
        CharSequence drawText = getResources().getText(R.string.draw);
        messageTextView.setText(drawText);
        Toast.makeText(this, drawText, Toast.LENGTH_SHORT).show();
    }

    /** Ends the game declaring the winner. */
    private void endGameWithWinner() {
        // Declare the winner.
        CharSequence winText;
        if (xTurn) {
            winText = getResources().getText(R.string.x_won);
        } else {
            winText = getResources().getText(R.string.o_won);
        }
        messageTextView.setText(winText);
        Toast.makeText(this, winText, Toast.LENGTH_SHORT).show();

        // Lock the board until a restart.
        waitingForRestart = true;
    }

    /** Clears all the game's data. */
    private void restartGame() {
        for (Cell[] row : board) {
            for (Cell cell : row) {
                cell.clear();
            }
        }

        messageTextView.setText(getResources().getText(R.string.x_turn));
        xTurn = true;
        numFilledCells = 0;
        waitingForRestart = false;
    }


    /** Is there a winner (AKA a row, diagonal or column X or Y sequence)? */
    private boolean hasWinner(int changedRow, int changedColumn) {
        if (hasRowWin(changedRow)) {
            return true;
        }

        if (hasColumnWin(changedColumn)) {
            return true;
        }

        return hasDiagonalWin();
    }

    /** Is there a diagonal X or O sequence. */
    private boolean hasDiagonalWin() {
        if (board[0][0].state == board[1][1].state
                && board[1][1].state == board[2][2].state
                && board[2][2].state != CellState.Empty) {
            return true;
        }

        return board[0][2].state == board[1][1].state
                && board[1][1].state == board[2][0].state
                && board[2][0].state != CellState.Empty;
    }

    /** Is there a column X or O sequence. */
    private boolean hasColumnWin(int column) {
        return board[0][column].state == board[1][column].state
                && board[1][column].state == board[2][column].state
                && board[2][column].state != CellState.Empty;
    }

    /** Is there a row X or O sequence. */
    private boolean hasRowWin(int row) {
        return board[row][0].state == board[row][1].state
                && board[row][1].state == board[row][2].state
                && board[row][2].state != CellState.Empty;
    }

    /** A board's cell state. */
    private enum CellState {
        Empty, X, O,
    }

    /** A cell in the game's board. */
    private static class Cell {
        /** The cell's image. */
        public ImageView image;
        /** The cell's state. */
        public CellState state;

        /** Construct an empty cell given it's ImageView in the display. */
        public Cell(ImageView image) {
            this.image = image;
            this.state = CellState.Empty;
        }

        /** Marks the cell as X. */
        public void setX() {
            image.setImageResource(R.drawable.cell_x);
            state = CellState.X;
        }

        /** Marks the cell as Y. */
        public void setO() {
            image.setImageResource(R.drawable.cell_o);
            state = CellState.O;
        }

        /** Clears the cell's data. */
        public void clear() {
            image.setImageResource(R.drawable.cell_empty);
            state = CellState.Empty;
        }
    }
}