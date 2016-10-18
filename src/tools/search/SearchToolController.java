package tools.search;

import tools.search.ai.players.AIPlayer;
import tools.search.ai.AIBot;
import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.InvalidPositionException;
import Game.Pieces;
import Game.Team;
import java.util.Timer;
import java.util.TimerTask;
import actions.MoveAction;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;

/**
 *
 */
public class SearchToolController implements MouseListener {
    
    private final Timer timer = new Timer();
    private AIBot bot;
    private final GameState state;
    /**
    public static void main(String[] args) {
        new SearchToolController().initialize();
    }*/
    
    public SearchToolController(GameState state) {
        this.state = state;
    }
    
    public void initialize() {
        this.bot = new AIPlayer(Team.BLUE);
        //startGame();
    }
    
    private void startGame() {
        System.out.println("Invoke Get MOVE");
        MoveAction move = getMoveAIPlayer();
        System.out.println("Got Move");
        
    }
    
    public MoveAction getMoveAIPlayer() {
        System.out.println("Get MOVE");
        TimerTask timeTask = new TimerTask() {
            @Override
            public void run() {
                SearchToolController.this.bot.stop();
            }
        };
        
        long delay = 1000;
        long start = System.currentTimeMillis();
        timer.schedule(timeTask, delay);
        long end = System.currentTimeMillis();
        System.out.println("Scheduling: " + (end - start) + " ms.");
        
        // Ask the player to calculate a move.
        
        long startOne = System.currentTimeMillis();
        MoveAction move = this.bot.nextMove(this.state);
        long endOne = System.currentTimeMillis();
        System.out.println("Move Computation Time: " + (endOne - startOne) + " ms.");
        return move;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        try {
            JTable table = (JTable) e.getSource();
            int x = table.columnAtPoint(e.getPoint());
            int y = table.rowAtPoint(e.getPoint());
            BoardPosition target = new BoardPosition(x, y);
            
            GameBoard board = this.state.getGameBoard();
            List<GamePiece> highlightedPieces = board.getHihglightedPieces();
            if(!highlightedPieces.isEmpty()) {
                if(highlightedPieces.size() > 1) {
                    System.err.println("There is more than one piece "
                            + "highlighted at the same time.");
                }
                
                // A piece was highlighted and may be ready to move.
                // Check if the move is valid, that is a cell that is neighbouring
                // and on the board and the cell must be free or attackable.
                GamePiece selectedPiece = highlightedPieces.get(0);
                List<BoardPosition> moves = board.getValidMoves(selectedPiece);
                
                boolean movedPiece = false;
                for(BoardPosition move : moves) {
                    if(move.equals(target)) {
                        // Move piece to target if cell is empty.
                        if(board.isEmpty(target)) {
                            board.setPiece(target, selectedPiece);
                            movedPiece = true;
                        } else { // Must be an empty if the getValidMoves
                            // method did its work right.
                            GamePiece enemy = board.getPiece(target);
                            if(selectedPiece.isEnemy(enemy)) {
                                if(enemy.getRank() == Pieces.FLAG) {
                                    System.out.println("Captured the Flag.");
                                    // Call End Game.
                                }
                                
                                int result = selectedPiece.attack(enemy);
                                if(result == 1) {
                                    board.removePieceAt(target);
                                    board.setPiece(target, selectedPiece);
                                } else if(result == 0) {
                                    board.removePieceAt(selectedPiece.getPosition());
                                    board.removePieceAt(enemy.getPosition());
                                } else {
                                    //board.setPiece(move, enemy);
                                    board.removePieceAt(selectedPiece.getPosition());
                                }
                            } else { // Fatal Error, leading into corrupt state.
                                throw new RuntimeException("Move Target " + target.toString() + " contains a non-enemy piece.");
                            }
                            
                        }
                        
                        break;
                    }
                }
                
                // At this point the piecer has either been moved or the
                // target of the move was invalid in both cases the piece must
                // be unhighlighted.
                selectedPiece.toggleHighlight();
                
                // How to inform the player of an invalid move?
                
            } else {
                GamePiece piece = board.getPiece(target);
                // Select and highlight this piece for movement only if the
                // clicked cell contains an actual piece and if the piece is
                // non-static, that is allowed to move. The only non-moveable
                // pieces are the BOMB and FLAG.
                if(piece != null) {
                    if(!piece.isStatic()) {
                        piece.toggleHighlight();
                    }
                }
            }
            
            // Refersh UI.
            table.repaint();
        } catch (InvalidPositionException ex) {
            Logger.getLogger(SearchToolController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
