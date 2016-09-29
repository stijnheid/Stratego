package Logic;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.InvalidPositionException;
import Game.Pieces;
import Game.Team;
import actions.Action;
import actions.MoveAction;
import actions.SelectAction;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which executes Actions performed by a controller, after which it
 * updates the GameState according to the game logic, and potentially requests
 * Animations to be played.
 * @author Maurits Ambags (0771400)
 */
public class Simulation {
    
    /* The current GameState. May be modified by an Action but should be
       communicated to the main GameState at the end of that Ply.
    */
    private final GameState state;
    
    private Team turn;
    
    /* The currently selected cell (null if none selected).*/
    private int currentlySelected;
    
    //private final Renderer renderer;
    
    public Simulation(GameState state) {
        this.state = state;
        this.turn = Team.RED;
    }    
    
    /**
     * Method to execute a valid Ply.
     * This method should update the currentState and potentially call an 
     * Animation to be played. Finally, it should call setGameState() to update
     * the new GameState.
     * @param p 
     */
    //private void makePly(Ply p){
    //    
    //}
    
    /**
     * Method to process an Action performed by a controller.
     * If this action is of the form Ply, this method should call makeMove to
     * execute this Ply.
     * A Select Action may trigger a Ply to be made if there already is a cell
     * selected and the new Select is a valid move for the piece on the selected
     * cell. If this is not the case, only update the currentlySelected cell.
     * An UnSelect Action should only set the currentlySelected variable to null.
     * @param action the Action that should be processed.
     */
    public void processAction(Action action){
        // Check here if the action is from the team that has the current turn.
        if(this.turn != action.getTeam()) {
            System.out.println(action.getTeam() + " is not allowed to move.");
            return;
        }
        
        if(action instanceof SelectAction) {
            SelectAction selection = (SelectAction) action;
            BoardPosition target = selection.getTarget();
            
            GameBoard board = this.state.getGameBoard();
            List<GamePiece> highlighted = board.getHihglightedPieces();
            
            if(highlighted.isEmpty()) {
                System.out.println("SELECT PIECE");
                GamePiece piece;
                try {
                    piece = board.getPiece(target);
                    if(piece != null && !piece.isStatic()) {
                        piece.toggleHighlight();
                    }
                } catch (InvalidPositionException ex) {
                    Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                // A piece was highlighted and might be moved.
                if(highlighted.size() > 1) {
                    throw new RuntimeException("More than one piece selected at the same time.");
                }
                
                System.out.println("MOVE PIECE");
                GamePiece selectedPiece = highlighted.get(0);
                List<BoardPosition> positions = board.getValidMoves(selectedPiece);
                
                // The move is valid, apply it.
                if(positions.contains(target)) {
                    try {
                        if(board.isEmpty(target)) {
                            System.out.println("MOVE TO FREE SPOT");
                            board.setPiece(target, selectedPiece);
                            // Switch turn and show a move animation.
                            switchTurn();
                        } else { // Attack the enemy piece.
                            GamePiece enemy = board.getPiece(target);
                            if(!selectedPiece.isEnemy(enemy)) {
                                throw new RuntimeException("Non-enemy piece at target " + target.toString());
                            }
                            
                            // Is the FLAG captured?
                            if(enemy.getRank() == Pieces.FLAG) {
                                System.out.println("CAPTURED THE FLAG");
                                // End Game.
                                // Show a Victory Animation.
                                endGame();
                            }
                            
                            // Show an ATTACK Animation.
                            
                            int result = selectedPiece.attack(enemy);
                            System.out.println("ATTACK PIECE --> " + result);
                            if(result == 1) {
                                board.killPiece(enemy);
                                board.setPiece(target, selectedPiece);
                            } else if(result == -1) {
                                // The piece that is attacked, wins.
                                board.killPiece(selectedPiece);
                            } else { // Tie
                                // Both pieces die.
                                board.killPiece(selectedPiece);
                                board.killPiece(enemy);
                            }
                            
                            // Switch turn.
                            switchTurn();
                        }
                        
                        
                    } catch (InvalidPositionException ex) {
                        Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    System.out.println("INVALID MOVE/UNSELECT");
                }
                
                // Unselect.
                selectedPiece.toggleHighlight();
            }
        } else if(action instanceof MoveAction) {
            // These type of actions are supplied by the AI algorithm.
            System.out.println("MoveAction: AI?");
            MoveAction move = (MoveAction) action;
            GameBoard board = this.state.getGameBoard();
            // This function takes care of applying the move and incrementing
            // the move counter if the move is made by the attacker.
            board.applyMove(move);
        }
    }
    
    private void endGame() {
        
    }
    
    private void switchTurn() {
        if(this.turn == Team.RED) {
            this.turn = Team.BLUE;
        } else {
            this.turn = Team.RED;
        }
    }
    
    // Implement State Machine Logic that Controls the Game Flow.
}
