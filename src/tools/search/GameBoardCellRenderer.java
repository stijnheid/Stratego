package tools.search;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.InvalidPositionException;
import Game.Team;
import java.awt.Color;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * TableCellRenderer used to draw the current game state.
 */
public class GameBoardCellRenderer extends JLabel implements TableCellRenderer {

    //private final boolean isBordered;
    private final GameState state;
    
    public GameBoardCellRenderer(GameState state, boolean isBordered) {
        this.state = state;
        //this.isBordered = isBordered;
        setOpaque(true); //MUST do this for background to show up.
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object color,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        
        //Color newColor = (Color) color;
        // Fetch the piece at this position.
        GameBoard board = this.state.getGameBoard();
        try {
            GamePiece piece = board.getPiece(new BoardPosition(column, row));
            Color backgroundColor;
            if(piece == null) { // Empty Cell.
                backgroundColor = Color.WHITE;
                
                // Need to explicitly set the JLabel to empty since
                // else the JTable Renderer will re-use the previous JLabel, so
                // pieces are copied to following cells, while they not exist.
                setText("");
                setToolTipText("Empty Cell");
            } else { 
                
                if(piece.isHightlighted()) {
                    backgroundColor = Color.YELLOW;
                } else if(piece.getTeam() == Team.RED) {
                    backgroundColor = Color.RED;
                } else {
                    backgroundColor = Color.BLUE;
                }
                
                setText(piece.getRank().getPieceSymbol());
                setToolTipText(piece.getRank().name());
            }
            
            setBackground(backgroundColor);
            
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        } catch (InvalidPositionException ex) {
            Logger.getLogger(GameBoardCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return this;
    }
}
