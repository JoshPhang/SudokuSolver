/**
 * CPSC 222 Final Project
 * Joshua Phang 230158207
 * SudokuSolver.java
 *
 * Note: JavaFX Runtime components are required for running this program.
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Objects;

public class SudokuSolver extends Application{
    // Setup UI for Sudoku board
    public static final Board puzzle = new Board();
    public static TilePane bigPane = new TilePane();

    // Setting for choosing puzzle number (1-5)
    private int PUZZLE_NUM = 1;
    private String PUZZLE_FILE = "src/puzzles/puzzle_" + PUZZLE_NUM + ".txt";

    @Override
    public void start(Stage stage) {
        // Create title of scene
        Text titleText = new Text("Sudoku Solver");
        titleText.setFont(Font.font("Stencil", 48));

        // Create status of puzzle
        Text solvedStatus = new Text("");
        solvedStatus.setFont(Font.font("Stencil", 24));

        // Create box to hold both the board and the buttons
        VBox mainBox = new VBox();
        mainBox.setPadding(new Insets(10));
        mainBox.setSpacing(20);
        mainBox.setBackground(Background.fill(Paint.valueOf("rgb(60, 60, 60)")));
        mainBox.setAlignment(Pos.CENTER);

        // Create button to start solver
        Button solveButton = new Button("Solve!");
        solveButton.setBackground(Background.fill(Paint.valueOf("rgb(120, 120, 120)")));
        solveButton.setBorder(Border.stroke(Paint.valueOf("black")));
        solveButton.setPrefHeight(40);
        solveButton.setPrefWidth(80);
        solveButton.setTextFill(Paint.valueOf("black"));

        // Create button to reset the board
        Button resetButton = new Button("Reset");
        resetButton.setBackground(Background.fill(Paint.valueOf("rgb(120, 120, 120)")));
        resetButton.setBorder(Border.stroke(Paint.valueOf("black")));
        resetButton.setPrefHeight(40);
        resetButton.setPrefWidth(80);
        resetButton.setTextFill(Paint.valueOf("black"));

        // Create button to close the board
        Button closeButton = new Button("Exit");
        closeButton.setBackground(Background.fill(Paint.valueOf("rgb(120, 120, 120)")));
        closeButton.setBorder(Border.stroke(Paint.valueOf("black")));
        closeButton.setPrefHeight(40);
        closeButton.setPrefWidth(80);
        closeButton.setTextFill(Paint.valueOf("black"));

        // UI for choosing puzzle
        Spinner puzzleSpinner = new Spinner(1,5,1,1);
        puzzleSpinner.setPrefHeight(40);
        puzzleSpinner.setPrefWidth(80);
        puzzleSpinner.setEditable(false);
        puzzleSpinner.setBackground(Background.fill(Paint.valueOf("rgb(120,120,120)")));

        // Create box to hold the buttons and spinner
        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(10));
        buttonBox.setSpacing(20);
        buttonBox.setBackground(Background.fill(Paint.valueOf("rgb(60, 60, 60)")));
        buttonBox.setAlignment(Pos.CENTER);

        buttonBox.getChildren().add(puzzleSpinner);
        buttonBox.getChildren().add(solveButton);
        buttonBox.getChildren().add(resetButton);
        buttonBox.getChildren().add(closeButton);

        // When solve button is clicked, start running threads
        solveButton.setOnAction(event -> {
            // Create solver threads
            Thread row_solver = new Thread(new SolverThreads(0));
            Thread col_solver = new Thread(new SolverThreads(1));
            Thread box_solver = new Thread(new SolverThreads(2));
            Thread check_solver = new Thread(new SolverThreads(3));

            row_solver.start();
            col_solver.start();
            box_solver.start();
            check_solver.start();

            // Check if board is solved whenever there is an update
            while(!puzzle.getSolved() && !puzzle.getBacktracking()) {
                if(Board.needs_update) {
                    updateBoard(puzzle, bigPane);
                }
                Board.needs_update = false;
                System.out.print(""); // Random sout here to prevent program from occasionally crashing idk why lol
            }

            updateBoard(puzzle, bigPane);

            if(puzzle.getSolved()) {
                solvedStatus.setText("Solved!");
                solvedStatus.setFill(Paint.valueOf("rgb(30,180,30)")); // Green
            } else {
                solvedStatus.setText("Backtracking needed.");
                solvedStatus.setFill(Paint.valueOf("rgb(180,30,30)")); // Red
            }
            // Stop threads when board is solved
            row_solver.interrupt();
            col_solver.interrupt();
            box_solver.interrupt();
            check_solver.interrupt();
        });

        // Reset board when reset button is clicked
        resetButton.setOnAction(event -> {
            PUZZLE_FILE = "puzzles/puzzle_" + PUZZLE_NUM + ".txt";
            puzzle.loadBoardFromFile(PUZZLE_FILE);
            puzzle.printBoard();

            solvedStatus.setText("");
            updateBoard(puzzle, bigPane);
            puzzle.setUnsolved();
            puzzle.setBacktracking(false);
        });

        // Close board when close button is clicked
        closeButton.setOnAction(event -> stage.close());

        // Add mouse hover effects for buttons
        solveButton.setOnMouseEntered(mouseEvent -> solveButton.setBackground(Background.fill(Paint.valueOf("rgb(80, 80, 80)"))));
        solveButton.setOnMouseExited(mouseEvent -> solveButton.setBackground(Background.fill(Paint.valueOf("rgb(120, 120, 120)"))));

        resetButton.setOnMouseEntered(mouseEvent -> resetButton.setBackground(Background.fill(Paint.valueOf("rgb(80, 80, 80)"))));
        resetButton.setOnMouseExited(mouseEvent -> resetButton.setBackground(Background.fill(Paint.valueOf("rgb(120, 120, 120)"))));

        closeButton.setOnMouseEntered(mouseEvent -> closeButton.setBackground(Background.fill(Paint.valueOf("rgb(80, 80, 80)"))));
        closeButton.setOnMouseExited(mouseEvent -> closeButton.setBackground(Background.fill(Paint.valueOf("rgb(120, 120, 120)"))));


        puzzleSpinner.setOnMouseClicked(event -> {
            PUZZLE_NUM = (int) puzzleSpinner.getValue();
            PUZZLE_FILE = "src/puzzles/puzzle_" + PUZZLE_NUM + ".txt";
            puzzle.loadBoardFromFile(PUZZLE_FILE);
            puzzle.printBoard();

            solvedStatus.setText("");
            updateBoard(puzzle, bigPane);
            puzzle.setUnsolved();
            puzzle.setBacktracking(false);
        });

        // Load the puzzle from file into the board
        puzzle.loadBoardFromFile(PUZZLE_FILE);
        puzzle.printBoard();

        // Create master board
        bigPane.setPrefColumns(3);
        bigPane.setHgap(5);
        bigPane.setVgap(5);
        bigPane.setAlignment(Pos.CENTER);
        bigPane.setBackground(Background.fill(Paint.valueOf("rgb(60, 60, 60)")));

        // Update the board UI when everything is set up
        updateBoard(puzzle, bigPane);

        mainBox.getChildren().add(titleText);
        mainBox.getChildren().add(bigPane);
        mainBox.getChildren().add(buttonBox);
        mainBox.getChildren().add(solvedStatus);

        // Create the scene and display it to the user
        stage.setTitle("Sudoku Solver");
        stage.setScene(new Scene(mainBox,600,700));
        stage.setResizable(false);
        stage.getIcons().add(new Image(Objects.requireNonNull(SudokuSolver.class.getResourceAsStream("images/icon.png"))));
        stage.show();
    }

    public static void updateBoard(Board board, TilePane bigPane) {
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
                }
                textBox.setStyle("-fx-text-inner-color: rgb(0, 20, 100);");
                textBox.setEditable(false);
                textBox.setPrefSize(50,50);
                textBox.setAlignment(Pos.CENTER);
                textBox.setBackground(Background.fill(Paint.valueOf("rgb(100, 100, 100)")));

                panes[boardnum].getChildren().add(textBox);
            }
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
