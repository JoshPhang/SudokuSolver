import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Board {
    private final SudokuTile[][] board;
    public static boolean needs_update;
    public static boolean solved;

    public Board() {
        board = new SudokuTile[9][9];
        needs_update = false;
        solved = false;
    }

    public void setTile(int x, int y, int val) {
        board[x][y] = new SudokuTile(val);
    }

    public void loadBoardFromFile(String file_name) {
        File file = new File(file_name);
        Scanner fileReader;
        try {
            fileReader = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        for(int i = 0; i < 9; i++) {
            // Get a row of the board
            String line = fileReader.nextLine();

            // Separate row into tiles
            String[] row = line.split(",");

            for(int j = 0; j < 9; j++) {
                if(row[j].equals(" ")) {
                    board[i][j] = new SudokuTile(null);
                } else {
                    board[i][j] = new SudokuTile(Integer.parseInt(row[j]));
                    board[i][j].setFilled();
                }
            }
        }
    }

    public void printBoard() {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                if(board[i][j].getVal() == null) {
                    System.out.print(" ,");
                } else {
                    System.out.print(board[i][j].getVal() + ",");
                }
            }
            System.out.println();
        }
    }

    public SudokuTile getTile(int i, int j) {
        return board[i][j];
    }

    public boolean getSolved() {
        return solved;
    }

    public void checkSolved() {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                if(getTile(i,j).isEmpty()) {
                    return;
                }
            }
        }
        solved = true;
    }

    public void setUnsolved() {
        solved = false;
    }
}
