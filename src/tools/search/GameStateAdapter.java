package tools.search;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.GlobalSettings;
import Game.InvalidPositionException;
import Game.Team;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * Custom Table Model that draws the current game state.
 */
public class GameStateAdapter extends AbstractTableModel implements TableModelListener {
    
    private final GameState state;
    
    public GameStateAdapter(GameState state) {
        this.state = state;
        int width = GlobalSettings.WIDTH;
        int height = GlobalSettings.HEIGHT;
        GameBoard board = new GameBoard(width, height, Team.RED, Team.BLUE);
        this.state.setGameBoard(board);
    }
    
    public GameState getGameState() {
        return this.state;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        System.out.println("Table Changed.");
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
            GamePiece piece = board.getPiece(new BoardPosition(columnIndex, 
                    rowIndex));

            // Empty cell.
            if(piece == null) {
                return "";
            }
           
            String symbol = piece.getRank().getPieceSymbol();
            return symbol;
        } catch (InvalidPositionException ex) {
            Logger.getLogger(GameStateAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "X";
    }
}
