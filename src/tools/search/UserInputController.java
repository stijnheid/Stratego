package tools.search;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.InvalidPositionException;
import Game.Team;
import Logic.Simulation;
import actions.SelectAction;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;

/**
 * A user input controller that translates mouse actions into Action objects
 * that can be submitted to the simulation component.
 */
public class UserInputController implements MouseListener {
    private final GameState state;
    private final Simulation simulation;
    private Team side;
    
    public UserInputController(GameState state, Simulation simulation) {
        this.state = state;
        this.simulation = simulation;
        this.side = null;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //System.out.
        System.out.println("MOUSE RELEASED");
        // Delegate the SELECT and MOVE Actions to the Simulation.
        JTable table = (JTable) e.getSource();
        int x = table.columnAtPoint(e.getPoint());
        int y = table.rowAtPoint(e.getPoint());
        BoardPosition target = new BoardPosition(x, y);
        GameBoard board = this.state.getGameBoard();
        try {
            GamePiece piece = board.getPiece(target);
            if(piece != null) {
                Team team = piece.getTeam();
                SelectAction selection = new SelectAction(team, target);
                simulation.processAction(selection);
                this.side = team;
            } else if(this.side != null) {
                SelectAction selection = new SelectAction(this.side, target);
                simulation.processAction(selection);
                this.side = null;
            } else {    
                System.out.println("Nothing selected.");
            }
        } catch (InvalidPositionException ex) {
            Logger.getLogger(UserInputController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Update UI after action.
        table.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    
}
