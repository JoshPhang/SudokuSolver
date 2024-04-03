import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class SolverThreads implements Runnable {
    // id=0: row checker
    // id=1: col checker
    private int id;

    public SolverThreads(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while(!SudokuSolver.puzzle.isSolved()) {
            switch(id) {
                case 0:
                    for(int i = 0; i < 9; i++) {                                    // Check rows line by line for seen numbers
                        ArrayList<Integer> possible_in_row = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9));
                        for(int j = 0; j < 9; j++) {
                            SudokuTile curr_tile = SudokuSolver.puzzle.getTile(i,j);
                            if(!curr_tile.isEmpty()) {
                                if(isIn(curr_tile.getVal(),possible_in_row)) {                // Remove possible values for the row
                                    possible_in_row.remove(Integer.valueOf(curr_tile.getVal()));
                                }
                            } else {
                                if(curr_tile.valInList(curr_tile.getPossible_vals())) {                // Remove possible value for each tile that exists
                                    curr_tile.removePossible(curr_tile.getVal());
                                }
                            }
                        }
                        // Update each tile in the row with the possible values
                        for(int j = 0; j < 9; j++) {
                            if(SudokuSolver.puzzle.getTile(i,j).isEmpty()) {
                                SudokuSolver.puzzle.getTile(i, j).intersectList(possible_in_row);
                                System.out.println("possible values in tile " + i + "," + j + " is ");
                                for (Integer num : SudokuSolver.puzzle.getTile(i, j).getPossible_vals()) {
                                    System.out.print(num + " ");
                                }
                            }
                            System.out.println();
                        }

                        // Fill in tile if there is
                    }

            }
        }
    }

    public boolean isIn(Integer val, ArrayList<Integer> nums) {
        for(Integer num : nums) {
            if(val == num) {
                return true;
            }
        }
        return false;
    }
}
