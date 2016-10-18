package Game;

/**
 *
 */
public enum Team {
    RED,
    BLUE;
    
    public Team opposite() {
        if(this == RED) {
            return BLUE;
        } else {
            return RED;
        }
    }
    
    public String getSymbol() {
        if(this == RED) {
            return "r";
        } else {
            return "b";
        }
    }
}
