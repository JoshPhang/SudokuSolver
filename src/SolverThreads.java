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
            synchronized (SudokuSolver.puzzle) {
                switch (id) {
                    case 0: {
                        for (int i = 0; i < 9; i++) {                                    // Check rows line by line for seen numbers
                            ArrayList<Integer> possible_in_row = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
                            for (int j = 0; j < 9; j++) {
                                SudokuTile curr_tile = SudokuSolver.puzzle.getTile(i, j);
                                if (!curr_tile.isEmpty()) {
                                    if (isIn(curr_tile.getVal(), possible_in_row)) {                // Remove possible values for the row
                                        possible_in_row.remove(Integer.valueOf(curr_tile.getVal()));
                                    }
                                }
                            }
                            // Update each tile in the row with the possible values
                            for (int j = 0; j < 9; j++) {
                                if (SudokuSolver.puzzle.getTile(i, j).isEmpty()) {
                                    SudokuSolver.puzzle.getTile(i, j).intersectList(possible_in_row);
    //                                System.out.println("possible values in tile " + i + "," + j + " is ");
    //                                for (Integer num : SudokuSolver.puzzle.getTile(i, j).getPossible_vals()) {
    //                                    System.out.print(num + " ");
    //                                }
                                }

                                // Fill in tile if there is only one possible value
                                if (SudokuSolver.puzzle.getTile(i, j).isEmpty() && SudokuSolver.puzzle.getTile(i, j).getPossible_vals().size() == 1) {
                                    int val = SudokuSolver.puzzle.getTile(i, j).getPossible_vals().get(0);
                                    SudokuSolver.puzzle.setTile(i, j, val);
                                    SudokuSolver.puzzle.getTile(i, j).setFilled();
                                    SudokuSolver.puzzle.needs_update = true;
                                    System.out.print("Filling in " + val + " into " + i + "," + j + " -- c0");
                                    if (SudokuSolver.puzzle.getTile(i, j).isCorrect(SudokuSolver.solution.getTile(i, j))) {
                                        System.out.println(" -- correct");
                                    } else {
                                        System.out.println(" -- wrong");
                                    }
                                }
                            }
                        }
                    }
                    case 1: {
                        for (int j = 0; j < 9; j++) {                                    // Check col line by line for seen numbers
                            ArrayList<Integer> possible_in_col = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
                            for (int i = 0; i < 9; i++) {
                                SudokuTile curr_tile = SudokuSolver.puzzle.getTile(i, j);
                                if (!curr_tile.isEmpty()) {
                                    if (isIn(curr_tile.getVal(), possible_in_col)) {                // Remove possible values for the col with tile if it is filled
                                        possible_in_col.remove(Integer.valueOf(curr_tile.getVal()));
                                    }
                                }
                            }
                            // Update each tile in the row with the possible values
                            for (int i = 0; i < 9; i++) {
                                if (SudokuSolver.puzzle.getTile(i, j).isEmpty()) {
                                    SudokuSolver.puzzle.getTile(i, j).intersectList(possible_in_col);
    //                                System.out.println("possible values in tile " + i + "," + j + " is ");
    //                                for (Integer num : SudokuSolver.puzzle.getTile(i, j).getPossible_vals()) {
    //                                    System.out.print(num + " ");
    //                                }
                                }

                                // Fill in tile if there is only one possible value
                                if (SudokuSolver.puzzle.getTile(i, j).isEmpty() && SudokuSolver.puzzle.getTile(i, j).getPossible_vals().size() == 1) {
                                    int val = SudokuSolver.puzzle.getTile(i, j).getPossible_vals().get(0);
                                    SudokuSolver.puzzle.setTile(i, j, val);
                                    SudokuSolver.puzzle.getTile(i, j).setFilled();
                                    SudokuSolver.puzzle.needs_update = true;
                                    System.out.print("Filling in " + val + " into " + i + "," + j + " -- c1");
                                    if (SudokuSolver.puzzle.getTile(i, j).isCorrect(SudokuSolver.solution.getTile(i, j))) {
                                        System.out.println(" -- correct");
                                    } else {
                                        System.out.println(" -- wrong");
                                    }
                                }
                            }
                        }
                    }
                    case 2: {
                        // Sub-box checks
                        checkBox(1);
                        checkBox(2);
                        checkBox(3);
                        checkBox(4);
                        checkBox(5);
                        checkBox(6);
                        checkBox(7);
                        checkBox(8);
                        checkBox(9);
                    }
                    case 3: {
                        // Individually check each tile compared to other tiles in its row/col/box
                        // to see if it has the only possible number

                        // #1 check row
                        int[] num_nums = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
                        for (int i = 0; i < 9; i++) {
                            for (int j = 0; j < 9; j++) {
                                if (SudokuSolver.puzzle.getTile(i, j).isEmpty()) {
                                    ArrayList<Integer> nums = SudokuSolver.puzzle.getTile(i, j).getPossible_vals();
                                    for (int num : nums) {
                                        num_nums[num - 1]++;
                                    }
                                } else {
                                    num_nums[SudokuSolver.puzzle.getTile(i,j).getVal()-1] = 2;
                                }
                            }
                            // get a value if there is a 1 in num_nums
                            Integer val = null;
                            for (int n = 0; n < num_nums.length; n++) {
                                if (num_nums[n] == 1) {
                                    val = n + 1;
                                }
                            }
                            // Check where to put the value in the only possible spot
                            if (val != null) {
                                for (int j = 0; j < 9; j++) {
                                    if (SudokuSolver.puzzle.getTile(i, j).isEmpty()) {
                                        ArrayList<Integer> nums = SudokuSolver.puzzle.getTile(i, j).getPossible_vals();
                                        if (nums.contains(Integer.valueOf(val))) {
                                            SudokuSolver.puzzle.setTile(i, j, val);
                                            SudokuSolver.puzzle.getTile(i, j).setFilled();
                                            SudokuSolver.puzzle.needs_update = true;
                                            System.out.print("Filling in " + val + " into " + i + "," + j + " -- c3.1");
                                            if (SudokuSolver.puzzle.getTile(i, j).isCorrect(SudokuSolver.solution.getTile(i, j))) {
                                                System.out.println(" -- correct");
                                            } else {
                                                System.out.println(" -- wrong");
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // #2 Check col
                        num_nums = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
                        for (int j = 0; j < 9; j++) {
                            for (int i = 0; i < 9; i++) {
                                if (SudokuSolver.puzzle.getTile(i, j).isEmpty()) {
                                    ArrayList<Integer> nums = SudokuSolver.puzzle.getTile(i, j).getPossible_vals();
                                    for (int num : nums) {
                                        num_nums[num - 1]++;
                                    }
                                } else {
                                    num_nums[SudokuSolver.puzzle.getTile(i,j).getVal()-1] = 2;
                                }
                            }
                            // get a value if there is a 1 in num_nums
                            Integer val = null;
                            for (int n = 0; n < num_nums.length; n++) {
                                if (num_nums[n] == 1) {
                                    val = n + 1;
                                }
                            }
                            // Check where to put the value in the only possible spot
                            if (val != null) {
                                for (int i = 0; i < 9; i++) {
                                    if (SudokuSolver.puzzle.getTile(i, j).isEmpty()) {
                                        ArrayList<Integer> nums = SudokuSolver.puzzle.getTile(i, j).getPossible_vals();
                                        if (nums.contains(Integer.valueOf(val))) {
                                            SudokuSolver.puzzle.setTile(i, j, val);
                                            SudokuSolver.puzzle.getTile(i, j).setFilled();
                                            SudokuSolver.puzzle.needs_update = true;
                                            System.out.print("Filling in " + val + " into " + i + "," + j + " -- c3.2");
                                            if (SudokuSolver.puzzle.getTile(i, j).isCorrect(SudokuSolver.solution.getTile(i, j))) {
                                                System.out.println(" -- correct");
                                            } else {
                                                System.out.println(" -- wrong");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            SudokuSolver.puzzle.checkSolved(SudokuSolver.solution);
        }
        System.out.println("Solved!");
    }

    public boolean isIn(Integer val, ArrayList<Integer> nums) {
        for(Integer num : nums) {
            if(val == num) {
                return true;
            }
        }
        return false;
    }

    private void checkBox(int box) {
        int i_beg, i_end, j_beg, j_end;
        switch (box) {
            case 1:
                i_beg = 0;
                i_end = 3;
                j_beg = 0;
                j_end = 3;
                break;
            case 2:
                i_beg = 0;
                i_end = 3;
                j_beg = 3;
                j_end = 6;
                break;
            case 3:
                i_beg = 0;
                i_end = 3;
                j_beg = 6;
                j_end = 9;
                break;
            case 4:
                i_beg = 3;
                i_end = 6;
                j_beg = 0;
                j_end = 3;
                break;
            case 5:
                i_beg = 3;
                i_end = 6;
                j_beg = 3;
                j_end = 6;
                break;
            case 6:
                i_beg = 3;
                i_end = 6;
                j_beg = 6;
                j_end = 9;
                break;
            case 7:
                i_beg = 6;
                i_end = 9;
                j_beg = 0;
                j_end = 3;
                break;
            case 8:
                i_beg = 6;
                i_end = 9;
                j_beg = 3;
                j_end = 6;
                break;
            case 9:
                i_beg = 6;
                i_end = 9;
                j_beg = 6;
                j_end = 9;
                break;
            default:
                i_beg = 0;
                i_end = 0;
                j_beg = 0;
                j_end = 0;
        }
        // Sub-box checks

        // #1: Get possible values remaining in box
        ArrayList<Integer> possible_in_box = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for(int i = i_beg; i < i_end; i++) {
            for(int j = j_beg; j < j_end; j++) {
                SudokuTile curr_tile = SudokuSolver.puzzle.getTile(i,j);
                if (!curr_tile.isEmpty()) {
                    if (isIn(curr_tile.getVal(), possible_in_box)) {                // Remove possible values for the box with tile if it is filled
                        possible_in_box.remove(Integer.valueOf(curr_tile.getVal()));
                    }
                }
            }
        }

        // #3 Check box
        int[] num_nums = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        for(int i = i_beg; i < i_end; i++) {
            for (int j = j_beg; j < j_end; j++) {
                if (SudokuSolver.puzzle.getTile(i, j).isEmpty()) {
                    ArrayList<Integer> nums = SudokuSolver.puzzle.getTile(i, j).getPossible_vals();
                    for (int num : nums) {
                        num_nums[num - 1]++;
                    }
                } else {
                    num_nums[SudokuSolver.puzzle.getTile(i,j).getVal()-1] = 2;
                }
            }
        }
        Integer val = null;
        for(int n = 0; n < num_nums.length; n++) {
            if(num_nums[n] == 1) {
                val = n+1;
            }
        }
        if(val != null) {
            // get a value if there is a 1 in num_nums
            // Check where to put the value in the only possible spot
            for(int i = i_beg; i < i_end; i++) {
                for(int j = j_beg; j < j_end; j++) {
                    if(SudokuSolver.puzzle.getTile(i,j).isEmpty()) {
                        ArrayList<Integer> nums = SudokuSolver.puzzle.getTile(i,j).getPossible_vals();
                        if(nums.contains(Integer.valueOf(val))) {
                            SudokuSolver.puzzle.setTile(i,j,val);
                            SudokuSolver.puzzle.getTile(i,j).setFilled();
                            SudokuSolver.puzzle.needs_update = true;
                            System.out.print("Filling in " + val + " into " + i + "," + j + " -- c2.1");
                            if(SudokuSolver.puzzle.getTile(i,j).isCorrect(SudokuSolver.solution.getTile(i,j))) {
                                System.out.println(" -- correct");
                            } else {
                                System.out.println(" -- wrong");
                            }
                        }
                    }
                }
            }
        }

        // Update each tile in the box with the possible values
        for (int i = i_beg; i < i_end; i++) {
            for(int j = j_beg; j < j_end; j++) {
                if (SudokuSolver.puzzle.getTile(i, j).isEmpty()) {
                    SudokuSolver.puzzle.getTile(i, j).intersectList(possible_in_box);
                }

                // Fill in tile if there is only one possible value
                if (SudokuSolver.puzzle.getTile(i, j).isEmpty() && SudokuSolver.puzzle.getTile(i, j).getPossible_vals().size() == 1) {
                    val = SudokuSolver.puzzle.getTile(i, j).getPossible_vals().get(0);
                    SudokuSolver.puzzle.setTile(i, j, val);
                    SudokuSolver.puzzle.getTile(i, j).setFilled();
                    SudokuSolver.puzzle.needs_update = true;
                    System.out.print("Filling in " + val + " into " + i + "," + j + " -- c2.2");
                    if(SudokuSolver.puzzle.getTile(i,j).isCorrect(SudokuSolver.solution.getTile(i,j))) {
                        System.out.println(" -- correct");
                    } else {
                        System.out.println(" -- wrong");
                    }
                }
            }
        }
    }
}
