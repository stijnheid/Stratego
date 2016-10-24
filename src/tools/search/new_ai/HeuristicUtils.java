package tools.search.new_ai;

import Game.BoardPosition;
import java.util.HashMap;

/**
 *
 */
public class HeuristicUtils {
    
    public static HashMap<BoardPosition, Integer> loadMap(String values) {
        HashMap<BoardPosition, Integer> map = new HashMap<>();
        
        String[] lines = values.split("\n");
        for(int row=0; row<lines.length; row++) {
            String line = lines[row];
            
            String[] vals = line.split("\\|");
            for(int column=0; column<vals.length; column++) {
                int value = Integer.parseInt(vals[column]);
                BoardPosition pos = new BoardPosition(column, row);
                map.put(pos, value);
            }
        }
        
        return map;
    }    
}
