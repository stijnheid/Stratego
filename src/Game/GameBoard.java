package Game;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class GameBoard {
    
    private final int width;
    private final int height;
    private final GamePiece[][] board;
    // Can also keep a list of pieces rather than a multidimensional array.
    
    public GameBoard(int w, int h) {
        this.width = w;
        this.height = h;
        this.board = new GamePiece[h][w];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }   
    
    private boolean isValidPosition(BoardPosition position) {
        return (position.getX() >= 0 && position.getX() < this.width 
                && position.getY() >= 0 && position.getY() < this.height);
    }
    
    public GamePiece getPiece(BoardPosition position) throws InvalidPositionException {
        if(!isValidPosition(position)) {
            throw new InvalidPositionException("Invalid position: (" 
                    + position.getX() + "," + position.getY() + ")");
        }
        
        return this.board[position.getX()][position.getY()];
    }
    
    /**
    public GamePiece getPiece(int x, int y) {
        return this.board[x][y];
    }*/
    
    public void setPiece(BoardPosition position, GamePiece piece) throws InvalidPositionException {
        if(!isValidPosition(position)) {
            throw new InvalidPositionException("Invalid position: (" 
                    + position.getX() + "," + position.getY() + ")");
        }        
        
        BoardPosition old = piece.getPosition();
        // Remove piece from old position.
        this.board[old.getX()][old.getY()] = null;
        // Attach new position to piece.
        piece.setPosition(position);
        // Set piece to new position.
        this.board[position.getX()][position.getY()] = piece;
    }
    
    /**
    public void setPiece(int x, int y, GamePiece piece) {
        //this.board[piece.getX(), piece.getY()] = null;
        //piece.setX(x);
        //piece.setY(y);
        this.board[x][y] = piece;
    }*/
    
    // Returns true if the move result in an attack.
    public boolean movePiece(GamePiece piece) {
        return false;
    }
    
    public List<GamePiece> getTeam(Team team) {
        List<GamePiece> army = new ArrayList<>();
        
        for(int c=0; c<this.height; c++) {
            for(int r=0; r<this.width; r++) {
                GamePiece piece = this.board[c][r];
                if(piece != null && piece.getTeam() == team) {
                    army.add(piece);
                }
            }
        }
        return army;
    }
}
