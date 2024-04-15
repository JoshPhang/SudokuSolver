import java.util.ArrayList;
import java.util.Arrays;

public class SudokuTile {
    private final Integer val;
    private boolean empty;
    private ArrayList<Integer> possible_vals;

    public SudokuTile(Integer val) {
        this.val = val;
        if(val == null) {
            empty = true;
            possible_vals = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9));
        } else {
            empty = false;
            possible_vals = null;
        }
    }

    public Integer getVal() {
        return val;
    }

    public ArrayList<Integer> getPossible_vals() {
        return possible_vals;
    }

    public void intersectList(ArrayList<Integer> nums) {
        ArrayList<Integer> new_possibles = new ArrayList<>();
        for(Integer num : nums) {
            if(possible_vals.contains(num)) {
                new_possibles.add(num);
            }
        }
        possible_vals = new_possibles;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setFilled() {
        empty = false;
    }

}
