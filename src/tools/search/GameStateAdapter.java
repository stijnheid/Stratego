package tools.search;

import static Game.Pieces.*;
import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.GlobalSettings;
import Game.InvalidPositionException;
import Game.Pieces;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 */
public class GameStateAdapter extends AbstractTableModel implements TableModelListener {
    
    private final GameState state;
    
    public GameStateAdapter() {
        this.state = new GameState();
        int width = GlobalSettings.WIDTH;
        int height = GlobalSettings.HEIGHT;
        GameBoard board = new GameBoard(width, height);
        this.state.setGameBoard(board);
    }
    
    public GameState getGameState() {
        return this.state;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        
    }    

    @Override
    public int getRowCount() {
        return this.state.getGameBoard().getHeight();
    }

    @Override
    public int getColumnCount() {
        return this.state.getGameBoard().getWidth();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            GameBoard board = this.state.getGameBoard();
            GamePiece piece = board.getPiece(new BoardPosition(rowIndex, 
                    columnIndex));
            String symbol = getPieceSymbol(piece);
            return symbol;
        } catch (InvalidPositionException ex) {
            Logger.getLogger(GameStateAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "X";
    }
    
    private String getPieceSymbol(GamePiece piece) {
        Pieces rank = piece.getRank();
        switch(rank) {
            case BOMB:
                return "B";
            case FLAG:
                return "F";
            case SPY:
                return "S";
            case SCOUT:
                return "1";
            case MINER:
                return "2";
            case SERGEANT:
                return "3";
            case LIEUTENANT:
                return "4";
            case CAPTAIN:
                return "5";
            case MAJOR:
                return "6";
            case COLONEL:
                return "7";
            case GENERAL:
                return "8";
            case MARSHALL:
                return "9";
            default:
                return "X";
        }
    }
}
