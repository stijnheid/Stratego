package Logic;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.GlobalSettings;
import Game.InvalidPositionException;
import Game.Pieces;
import Game.Team;
import Renderer.Animation;
import Renderer.AnimationCallback;
import Renderer.AttackAnimation;
import Renderer.DeathAnimation;
import Renderer.DrawAnimation;
import Renderer.Skeleton;
import Renderer.Terrain;
import Renderer.Vector;
import Renderer.WalkAnimation;
import actions.Action;
import actions.MoveAction;
import actions.PlyAction;
import actions.SelectAction;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import tools.deeplearning.BattleEngine;
import tools.deeplearning.BattleTranscript;
import tools.search.Player;
import tools.search.ai.AIBot;

/**
 * Class which executes Actions performed by a controller, after which it
 * updates the GameState according to the game logic, and potentially requests
 * Animations to be played.
 * @author Maurits Ambags (0771400)
 */
public class Simulation implements AnimationCallback {
    
    /* The current GameState. May be modified by an Action but should be
       communicated to the main GameState at the end of that Ply.
    */
    private final GameState state;
    
    //private Team turn;
    private Player first;
    private Player second;
    private Player turn;
    private final int computationTime;
    //private Animation runningAnimation;
    private boolean animationBusy;
    private MoveAction pendingMove;
    private BattleTranscript transcript;
    private boolean aiBusy;
    private Terrain terrain;
    private Vector offset;
    
    private GamePiece subject;
    private BoardPosition subjectDestination;
    private boolean updateSkeleton;
    
    //private final Renderer renderer;
    //private final JComponent uiComponent;
    
    public Simulation(GameState state, Player first, Player second, Terrain terrain) {
        // Human vs Human matches are unsupported.
        if(first.isHuman() && second.isHuman()) {
            throw new IllegalArgumentException("Human vs Human not supported.");
        }
        // Both players must be in a different team.
        if(first.getTeam() == second.getTeam()) {
            throw new IllegalArgumentException("Players are on same team.");
        }
        
        this.state = state;
        //this.turn = Team.RED;
        this.first = first;
        this.second = second;
        this.turn = first;
        this.terrain = terrain;
        //this.uiComponent = null;
        this.computationTime = GlobalSettings.AI_COMPUTATION_TIME;
        //this.runningAnimation = null;
        this.animationBusy = false;
        this.pendingMove = null;
        this.offset = null;
    }
    
    /*
    public Simulation(GameState state, JComponent uiComponent) {
        this.state = state;
        this.turn = Team.RED; // Attacker starts.
        this.uiComponent = uiComponent;
    }*/
    
    /**
     * Method to process an Action performed by a controller, all actions
     * must be processed on the UI thread.

     * A Select Action may trigger a Ply to be made if there already is a cell
     * selected and the new Select is a valid move for the piece on the selected
     * cell. If this is not the case, only update the currentlySelected cell.
     * An UnSelect Action should only set the currentlySelected variable to null.
     * @param action the Action that should be processed.
     */
    public void processAction(Action action){
        if(!SwingUtilities.isEventDispatchThread()) {
            throw new RuntimeException("Action does not originate from a UI thread");
        }
        
        // Reject all actions if no game is running.
        if(this.transcript.gameOver()) {
            System.out.println("Rejected Action during game over " + action.getTeam());
            return;
        }
        
        // Do not accept any new action while an animation is running.
        if(this.animationBusy) {
            System.out.println("Rejected Action during animation from " + action.getTeam());
            return;
        }
        
        if(this.pendingMove != null) {
            System.out.println("Rejected Action during pending move from " + action.getTeam());
            return;
        }
        
        // Check if the action is from the team that has the current turn if not
        // an action from current team ignore it.
        if(this.turn.getTeam() != action.getTeam()) {
            System.out.println("Rejected Action from " + action.getTeam() + " (Player does not have current turn)");
            return;
        }
        
        System.out.println("Process " + action.getClass().getSimpleName() +
                " for " + this.turn.getTeam());
        
        if(action instanceof SelectAction) {
            System.out.println("Process selection.");
            processSelection((SelectAction) action);
        } else if(action instanceof MoveAction) {
            MoveAction move = (MoveAction) action;
            if(!move.representationOkay()) {
                System.out.println("Rejected malformed move action: " + move);
                return;
            }
            
            System.out.println("Process move.");
            processMove(move);
        } else if(action instanceof PlyAction) {
            System.out.println("Process ply.");
            PlyAction ply = (PlyAction) action;
            GamePiece piece = this.state.getGameBoard().getPiece(ply.getOrigin());
            System.out.println("Create Move from Ply");
            // TODO why is a null pointer not caught here?
            MoveAction move = new MoveAction(ply.getTeam(), piece, ply.getOrigin(), ply.getDestination());
            
            // TODO should check that the target is not the same piece.
            System.out.println("Get Target");
            GamePiece target = this.state.getGameBoard().getPiece(ply.getDestination());
            if(target != null) {
                move.setEnemy(target);
                move.setAttack();
            }
            
            // Deliver the converted ply action into a move action to this
            // same method, such that it is finally processed and influences
            // the game state.
            System.out.println("Deliver Ply to MoveAction: " + move);
            processAction(move);
        }
        
        // Update UI interface.
        /**
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                System.out.println("Redraw");
                if(Simulation.this.uiComponent != null) {
                    Simulation.this.uiComponent.repaint();
                }
            }
        });*/
    }
    
    private void processMove(MoveAction move) {
        // These type of actions are supplied by the AI bots.
        System.out.println("MoveAction from " + move.getTeam());
        System.out.println("Move: " + move);
        // Check if the move representation is valid.
        if(!move.representationOkay()) {
            throw new RuntimeException("Invalid move: " + move);
        }

        // Decide which animation must be played. Regular Move or an Attack.
        if(move.isAttack()) {
            Animation attackAnimation;
            Animation deathAnimation;
            
            GamePiece attacker = move.getPiece();
            // Not safe can currently point to a cloned piece that has a different
            // position, due to an interrupted search.
            //GamePiece enemy = move.getEnemy();
            
            GamePiece enemy = this.state.getGameBoard().getPiece(move.getDestination());
            System.out.println("Enemy: " + enemy.toString());
            
            // Create an attack animation.
            int result = attacker.attack(enemy);
            System.out.println("ATTACK PIECE --> " + result);
            if(result == 1) {
                System.out.println("Positive Attack Animation");
                // Attacker kills enemy.
                attackAnimation = new AttackAnimation(this.terrain, attacker, move.getDestination(), this);
                attackAnimation.execute();
                
                deathAnimation = new DeathAnimation(this.terrain, enemy, move.getOrigin(), this);
                deathAnimation.execute();

                this.updateSkeleton = true;
                this.subject = attacker;
                this.subjectDestination = move.getDestination();
            } else if(result == -1) {
                System.out.println("Negative Attack Animation");
                // The piece that is being attacked, wins.
                attackAnimation = new AttackAnimation(this.terrain, enemy, move.getOrigin(), this);
                attackAnimation.execute();
                
                deathAnimation = new DeathAnimation(this.terrain, attacker, move.getDestination(), this);
                deathAnimation.execute();
                
                this.updateSkeleton = true;
                this.subject = enemy;
                // If an enemy is being attacked and wins it should remain put
                // on its original position so not go to origin, but destination.
                this.subjectDestination = move.getDestination();
            } else { // Tie
                System.out.println("Tie Animation");
                // Both pieces die.
                attackAnimation = new DrawAnimation(this.terrain, attacker, move.getDestination(), this, true);
                attackAnimation.execute();
                
                Animation secondAnimation = new DrawAnimation(this.terrain, enemy, move.getOrigin(), this, false);
                secondAnimation.execute();
                
                DeathAnimation firstDeath = new DeathAnimation(this.terrain, attacker, move.getDestination(), this);
                firstDeath.execute();
                
                DeathAnimation secondDeath = new DeathAnimation(this.terrain, enemy, move.getOrigin(), this);
                secondDeath.execute();
            }
        } else {
            System.out.println("Walk Animation");
            // Create a regular move animation.
            WalkAnimation walk = new WalkAnimation(this.terrain, move.getPiece(), move.getDestination(), this);
            walk.execute();
        }
        
        // Store the running animation and pending move to be applied after
        // the animation is finished.
        //this.runningAnimation = animation;
        this.pendingMove = move;
        this.animationBusy = true;
        
        // Run animation.
        /**
        if(animation != null) {
            animation.execute();
        } else {
            // No animation, trigger turn switch directly. (For testing)
            applyMove();
        }*/      
    }
    
    /**
     * Must manually update the offset vector in the skeleton attached to the
     * GamePiece object in case of an attack animation. This is not necessary in
     * case of a walk animation there it is done automagically.
     * 
     * @param piece
     * @param position 
     */
    private void updateSkeletonPosition(GamePiece piece, BoardPosition position) {
        System.out.println("Update Skeleton Position");
        Skeleton skeleton = piece.getSkeleton();
        int x = position.getX();
        int y = position.getY();
        Vector offset = new Vector(-2.5 + x, 2.5 - y, 0);
        
        // If it is the attacker we need to rotate 180 degrees.
        if(piece.getTeam() == this.state.getGameBoard().getAttacker()) {
            offset.rotate(180);
        }
        
        // Assign new offset to skeleton.
        skeleton.offset = offset;
    }
    
    private void processSelection(SelectAction selection) {
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
                            endGame(selection.getTeam());
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
    }

    @Override
    public void animationEnded() {
        System.out.println("ANIMATION ENDED");
        if(!this.animationBusy) {
            throw new RuntimeException("No running animation.");
        }
        if(this.pendingMove == null) {
            throw new RuntimeException("No pending move.");
        }
        
        applyMove();
        
        // Reset animation variables.
        this.animationBusy = false;
        //this.pendingMove = null;
    }
    
    private void applyMove() {
        System.out.println("APPLY MOVE " + this.pendingMove.toString());
        GameBoard board = this.state.getGameBoard();

        // Store move in battle transcript. (Store before applying.)
        this.transcript.addMove(this.pendingMove);

        System.out.println("BoardState Before:\n" + board.transcript());
        
        
        // If an offset is set, apply it.
        /**
        if(this.offset != null) {
            this.pendingMove.getPiece().getSkeleton().offset = this.offset;
            this.offset = null;
        }  */
        
        // If this is an attack than the offset vector of the skeletons must
        // be updated.
        if(this.pendingMove.isAttack() && this.updateSkeleton) {
            if(this.subject == null || this.subjectDestination == null) {
                throw new RuntimeException("Offset helper variables not set.");
            }
            
            updateSkeletonPosition(this.subject, this.subjectDestination);
            // Reset helper variables.
            this.updateSkeleton = false;
            this.subject = null;
            this.subjectDestination = null;
        }
        
        // This function takes care of applying the move and incrementing
        // the move counter if the move is made by the attacker.
        board.applyMove(this.pendingMove);
        
        System.out.println("BoardState After:\n" + board.transcript());
        
        // Check if the board state is still valid after applying the move.
        // This is not done inside applyMove at all times, since that would only
        // incur extra cost for the search algorithms when they apply moves.
        if(!board.isSetupValid()) {
            throw new RuntimeException("Board state invalid after applying move: " + this.pendingMove);
        }
        
        // Check if the game has ended after this move.
        Team winner = board.isEndState();
        // Game ended.
        if(winner != null) {
            endGame(winner);
        } else {
            // Switch turns.
            switchTurn();            
        }
        
        // Reset pending move.
        this.pendingMove = null;
    }
    
    public void startGame() {
        // Check if the board setup is valid.
        GameBoard board = this.state.getGameBoard();
        if(!board.isSetupValid()) {
            throw new RuntimeException("Invalid board setup.");
        }
        
        if(board.isEndState() != null) {
            throw new RuntimeException("Board already is in an end state.");
        }
        
        // Print inital setup.
        System.out.println("Initial Setup:\n" + board.transcript());
        
        System.out.println("Simulation start game.");
        
        this.transcript = new BattleTranscript(board, first.getTeam(), second.getTeam());
        this.transcript.setComputationTime(this.computationTime);
        
        System.out.println("First player: " + this.first.getClass().getSimpleName());
        System.out.println("Second player: " + this.second.getClass().getSimpleName());
        Player red;
        Player blue;
        if(this.first.getTeam() == Team.RED) {
            red = this.first;
            blue = this.second;
        } else {
            red = this.second;
            blue = this.first;
        }
        
        this.transcript.setRedPlayer(red);
        this.transcript.setBluePlayer(blue);
        this.transcript.startGame();
        
        // Setup a user input controller and supply it with a reference to this
        // Simulation object such that it may deliver actions.
        if(this.first.isHuman()) {
            
        } else if(this.second.isHuman()) {
            
        } // else AI vs AI match.
        
        // Run an AI task if the first player is an AI bot.
        if(!this.turn.isHuman()) {
            runAITask();
        }        
    }
    
    private void endGame(Team winner) {
        // Game ended.
        System.out.println("Match winner: " + winner);
        this.transcript.endGame();
        System.out.println("Duration: " + this.transcript.getGameDuration() + " ms.");
        
        // Print battle transcript.
        this.transcript.print();
    }
    
    /**
    private void switchTurn() {
        if(this.turn == Team.RED) {
            this.turn = Team.BLUE;
        } else {
            this.turn = Team.RED;
        }
    }*/
    
    private void switchTurn() {
        if(this.turn == this.first) {
            this.turn = this.second;
            System.out.println("Current Turn: " + this.second.getTeam());
        } else {
            this.turn = this.first;
            System.out.println("Current Turn: " + this.first.getTeam());
        }
        
        // If the player that has the current turn is an AI player then
        // schedule an AINextMoveTask.
        if(!this.turn.isHuman()) {
            runAITask();
        }
    }
    
    private void runAITask() {
        System.out.println("Run AI Task");
        AIBot bot = (AIBot) this.turn;
        AINextMoveTask task = new AINextMoveTask(bot, this.computationTime, this.state);
        // Run task. Task will asynchronously deliver a MoveAction to processAction()
        task.execute();
    }
    
    private class AINextMoveTask extends SwingWorker<MoveAction, MoveAction> {

        private final AIBot bot;
        private final int computationTime;
        private final GameState gameState;

        public AINextMoveTask(AIBot bot, int computationTime, GameState gameState) {
            this.bot = bot;
            this.computationTime = computationTime;
            this.gameState = gameState;
        }
        
        @Override
        protected MoveAction doInBackground() throws Exception {
            // Request the next move from AI.
            // GameState here is accessed from the background thread.
            System.out.println("AI compute move for " + this.bot.getClass().getName());
            MoveAction move = BattleEngine.timedAIMove(this.gameState, this.bot, this.computationTime);
            return move;
        }

        @Override
        protected void done() {
            // Deliver the move action to the processAction method on the UI
            // thread.
            try {
                //super.done();
                MoveAction move = this.get();
                // Supply the move action asynchronously to the processAction method.
                processAction(move);
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    // Implement State Machine Logic that Controls the Game Flow.
}
