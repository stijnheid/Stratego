package Game;

/**
 * Represents a position on the board.
 */
public class BoardPosition {
    
    private final int x;
    private final int y;
    
    public BoardPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BoardPosition other = (BoardPosition) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }    

    @Override
    public String toString() {
        return "BoardPosition{" + "x=" + x + ", y=" + y + '}';
    }
    
    @Override
    public Object clone() {
        BoardPosition clone = new BoardPosition(x, y);
        return clone;
    }
}
