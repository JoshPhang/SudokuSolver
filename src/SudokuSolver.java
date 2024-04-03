import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import javax.swing.text.Position;
import java.awt.*;

public class SudokuSolver extends Application{
    public static Board puzzle = new Board();

    @Override
    public void start(Stage stage) throws Exception {
        puzzle.loadBoardFromFile("puzzle.txt");
        puzzle.printBoard();

        // Create master board
        TilePane bigPane = new TilePane();
        bigPane.setPrefColumns(3);
        bigPane.setHgap(5);
        bigPane.setVgap(5);
        bigPane.setAlignment(Pos.CENTER);
        bigPane.setBackground(Background.fill(Paint.valueOf("rgb(60, 60, 60)")));

        bigPane = updateBoard(puzzle, bigPane);

        stage.setTitle("Sudoku Solver");
        stage.setScene(new Scene(bigPane,600,600));
        stage.show();

        // Create solver threads
        Thread row_solver = new Thread(new SolverThreads(0));
        row_solver.start();

    }

    private TilePane updateBoard(Board board, TilePane bigPane) {
        bigPane.getChildren().clear();
        TilePane[] panes = new TilePane[9];

        for(int i = 0; i < 9; i++) {
            panes[i] = new TilePane();
            panes[i].setPrefColumns(3);
            panes[i].setBackground(Background.fill(Paint.valueOf("rgb(0, 0, 0)")));
            panes[i].setHgap(2);
            panes[i].setVgap(2);
            bigPane.getChildren().add(panes[i]);
        }

        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                // Hashing to each sub-board.
                // board 0: i=0,1,2, j=0,1,2
                // board 1: i=0,1,2, j=3,4,5
                // board 2: i=0,1,2, j=6,7,8
                // board 3: i=3,4,5, j=0,1,2
                // board 4: i=3,4,5, j=3,4,5
                // board 5: i=3,4,5, j=6,7,8
                // board 6: i=6,7,8, j=0,1,2
                // board 7: i=6,7,8, j=3,4,5
                // board 8: i=6,7,8, j=6,7,8
                int boardnum = 0;

                if(0 <= i && i <= 2 && 0 <= j && j <= 2) boardnum = 0;
                if(0 <= i && i <= 2 && 3 <= j && j <= 5) boardnum = 1;
                if(0 <= i && i <= 2 && 6 <= j && j <= 8) boardnum = 2;
                if(3 <= i && i <= 5 && 0 <= j && j <= 2) boardnum = 3;
                if(3 <= i && i <= 5 && 3 <= j && j <= 5) boardnum = 4;
                if(3 <= i && i <= 5 && 6 <= j && j <= 8) boardnum = 5;
                if(6 <= i && i <= 8 && 0 <= j && j <= 2) boardnum = 6;
                if(6 <= i && i <= 8 && 3 <= j && j <= 5) boardnum = 7;
                if(6 <= i && i <= 8 && 6 <= j && j <= 8) boardnum = 8;

                TextField textBox;

                if(board.getTile(i,j).getVal() == null) {
                    textBox = new TextField("");
                } else {
                    textBox = new TextField(board.getTile(i,j).getVal().toString());
                    textBox.setEditable(false);
                    textBox.setStyle("-fx-text-inner-color: rgb(0, 20, 100);");
                }
                textBox.setPrefSize(50,50);
                textBox.setAlignment(Pos.CENTER);
                textBox.setBackground(Background.fill(Paint.valueOf("rgb(100, 100, 100)")));

                panes[boardnum].getChildren().add(textBox);
            }
        }

        return bigPane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
