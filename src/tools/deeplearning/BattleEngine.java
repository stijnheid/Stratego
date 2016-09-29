package tools.deeplearning;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.GlobalSettings;
import Game.InvalidPositionException;
import Game.Pieces;
import Game.Team;
import actions.MoveAction;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator.OfInt;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import tools.search.ai.AIBot;
import tools.search.ai.players.DefaultPlayer;
import tools.search.ai.SetupGenerator;

/**
 * 
 * 
 */
public class BattleEngine {
    
    public static void main(String[] args) {
        //new BattleEngine().runTests(); //initialize();
        new BattleEngine().initialize();
        //new BattleEngine().testTranscript();
        //new BattleEngine().testApplyUndoMove();
        //new BattleEngine().testUnequalAttack();
        //new BattleEngine().testAlphaBeta();
    }
    
    public void runTests() {
        testApplyUndoMove();
    }
    
    // Initialize battlefield with supplied setup.
    public void initialize() {
        // Create an empty GameBoard
        /**
        GameBoard board = new GameBoard(GlobalSettings.WIDTH, 
                GlobalSettings.HEIGHT, Team.RED, Team.BLUE);*/
        GameBoard board = new GameBoard(4, 6, Team.RED, Team.BLUE);
        loadDefensiveSetup(board);
        
        List<GamePiece> offensiveArmy = new ArrayList<>();
        generateOffensiveSetup(board, offensiveArmy);
        
        // Print board setup.
        System.out.println(board.transcript());
        
        AIBot attacker = new DefaultPlayer(Team.RED); //new RandomPlayer(Team.RED);
        AIBot defender = new DefaultPlayer(Team.BLUE);
        System.out.println("Start Battle");
        battle(board, attacker, defender, 10000); //1500);
    }
    
    // Load the defensive setup for the computer, this is one of the setups
    // used in the neural networks.
    private void loadDefensiveSetup(GameBoard board) {
        try {
            board.setupPiece(0, 5, Pieces.FLAG, Team.BLUE);
            board.setupPiece(0, 4, Pieces.BOMB, Team.BLUE);
            board.setupPiece(1, 5, Pieces.BOMB, Team.BLUE);
            board.setupPiece(2, 5, Pieces.MINER, Team.BLUE);
            board.setupPiece(3, 5, Pieces.SERGEANT, Team.BLUE);
            board.setupPiece(1, 4, Pieces.CAPTAIN, Team.BLUE);
            board.setupPiece(2, 4, Pieces.SERGEANT, Team.BLUE);
            board.setupPiece(3, 4, Pieces.LIEUTENANT, Team.BLUE);
        } catch (InvalidPositionException ex) {
            Logger.getLogger(BattleEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Choose a random position and pick the piece from the list.
     * 
     * @param board
     * @param offensiveArmy 
     */
    private void generateOffensiveSetup(GameBoard board, List<GamePiece> offensiveArmy) {
        SecureRandom random = new SecureRandom();
        int streamSize = GlobalSettings.OFFENSIVE_ARMY_SIZE;
        int start = 0;
        int bound = 12;
        IntStream stream = random.ints(streamSize, start, bound);
        
        // We should be provided with a list of army pieces that we are allowed
        // to use.
        
        // Put the free positions in an array and then randomly pick one and
        // remove it and assign the piece to it.
        
        OfInt iterator = stream.iterator();
        while(iterator.hasNext()) {
            int x = iterator.next();
            
        }
        
        // Fixed Setup.
        try {
            board.setupPiece(0, 0, Pieces.FLAG, Team.RED);
            board.setupPiece(0, 1, Pieces.BOMB, Team.RED);
            board.setupPiece(1, 0, Pieces.BOMB, Team.RED);
            board.setupPiece(2, 0, Pieces.MINER, Team.RED);
            board.setupPiece(3, 0, Pieces.SERGEANT, Team.RED);
            board.setupPiece(1, 1, Pieces.CAPTAIN, Team.RED);
            board.setupPiece(2, 1, Pieces.SERGEANT, Team.RED);
            board.setupPiece(3, 1, Pieces.LIEUTENANT, Team.RED);            
        } catch (InvalidPositionException ex) {
            Logger.getLogger(BattleEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void simulate(GameBoard board) {
        // Make a copy of the initial setup.
        
        // Simulate the Battle.
        AIBot red = null;
        AIBot blue = null;
        
        
        
        // if a player is a human than wait for the input listeners to supply an
        // action to the simulation. if a player is an AI than actively invoke
        // the nextMove function and enforce the timeout.
        
        // Determine winner and supply start setup and winner to learning
        // algorithm.
        
    }
    
    private void battle(GameBoard board, AIBot attacker, AIBot defender, 
            long computationTime) {
        // Wrap around in a GameState
        GameState state = new GameState();
        state.setGameBoard(board);
        
        // Attacker starts the game.
        AIBot currentTurn = attacker;
        
        // Check if setups are valid.
        
        // Simulate the game.
        state.setRunning(true);
        int iterations = 0;
        System.out.println("Start Game.");
        BattleTranscript transcript = new BattleTranscript(board);
        int i = 0;
        while(board.isEndState() == null) {
            if(i >= 30) {
                break;
            }
            
            //System.out.println("Invoke Move");
            MoveAction move = timedAIMove((GameState) state.clone(), 
                    currentTurn, 
                    computationTime);
            // Store move in transcript.
            transcript.addMove(move);
            
            //System.out.println("Received Move");
            Team team = move.getPiece().getTeam();
            BoardPosition destination = move.getDestination();
            System.out.println(iterations + ": " + team.name() + " moved " 
                    + move.getPiece().getRank().name() + " to (" 
                    + destination.getX() + "," + destination.getY() + ")");
            
            // Apply move.
            state.getGameBoard().applyMove(move);
            
            System.out.println("BoardState:\n" + state.getGameBoard().transcript());
            
            // Swap turn.
            if(currentTurn == attacker) {
                currentTurn = defender;
            } else {
                currentTurn = attacker;
            }
            
            System.out.println("Next Move");
            iterations++;
            i++;
        }
        
        System.out.println("Game Ended.");
        transcript.print();
    }
    
    private MoveAction timedAIMove(GameState state, final AIBot bot, long computationTime) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                bot.stop();
            }
        };
        
        timer.schedule(task, computationTime);
        MoveAction move = bot.nextMove(state);
        task.cancel();
        return move;
    }
    
    // Calculate a random setup for the player.
    
    // Make random moves.
    
    public BattleResult battle() {
        return null;
    }
    
    public void testUnequalAttack() {
        SetupGenerator generator = new SetupGenerator();
        GameBoard board = generator.generateSetup();
        System.out.println("Initial Board");
        System.out.println(board.transcript());
        System.out.println();
        
        GamePiece lieutenant = new GamePiece(Pieces.LIEUTENANT, Team.RED, new BoardPosition(3,1));
        MoveAction move = new MoveAction(Team.RED, lieutenant, new BoardPosition(3,1), new BoardPosition(3,2));
        board.applyMove(move);
        System.out.println(board.transcript());
        System.out.println();
        
        MoveAction nextMove = new MoveAction(Team.RED, lieutenant, new BoardPosition(3,2), new BoardPosition(2,2));
        board.applyMove(nextMove);
        System.out.println(board.transcript());
        System.out.println();
        
        // Move Blue sergeant to (2,3)
        GamePiece blueSergeant = new GamePiece(Pieces.SERGEANT, Team.BLUE, new BoardPosition(2,4));
        MoveAction blueMove = new MoveAction(Team.BLUE, blueSergeant, new BoardPosition(2,4), new BoardPosition(2,3));
        board.applyMove(blueMove);
        System.out.println(board.transcript());
        System.out.println();
        
        /**
        // Red attack.
        System.out.println("Red Attack");
        MoveAction redAttack = new MoveAction(Team.RED, lieutenant, new BoardPosition(2,2), new BoardPosition(2,3));
        board.applyMove(redAttack);
        System.out.println(board.transcript());
        System.out.println();
        
        System.out.println("MoveCount: " + board.getMoveCount());
        
        // Undo red attack.
        System.out.println("Undo Red Attack");
        board.undoMove(redAttack);
        System.out.println(board.transcript());
        System.out.println();
        
        System.out.println("MoveCount: " + board.getMoveCount());
        */
        
        System.out.println("Blue Attack");
        MoveAction blueAttack = new MoveAction(Team.BLUE, blueSergeant, new BoardPosition(2,3), new BoardPosition(2,2));
        board.applyMove(blueAttack);
        System.out.println(board.transcript());
        System.out.println();
        
        System.out.println("MoveCount: " + board.getMoveCount());
        
        // Undo blue attack.
        System.out.println("Undo Blue Attack");
        board.undoMove(blueAttack);
        System.out.println(board.transcript());
        System.out.println();
        
        System.out.println("MoveCount: " + board.getMoveCount());
        
    }
    
    public void testApplyUndoMove() {
        SetupGenerator generator = new SetupGenerator();
        GameBoard board = generator.generateSetup();
        System.out.println("Initial Board");
        System.out.println(board.transcript());
        System.out.println();
        
        try {
            // Move both captains to the front.
            GamePiece redCaptain = board.getPiece(new BoardPosition(1,1));
            MoveAction move = new MoveAction(Team.RED, redCaptain, new BoardPosition(1,1), new BoardPosition(1,2));
            board.applyMove(move);
            System.out.println(board.transcript());
            System.out.println();
            
            System.out.println("Move Red Captain back.");
            board.undoMove(move);
            System.out.println(board.transcript());
            System.out.println();
            
            System.out.println("Reapply red captain forward move.");
            board.applyMove(move);
            System.out.println(board.transcript());
            System.out.println();
            
            System.out.println("Move blue forward.");
            GamePiece blueCaptain = board.getPiece(new BoardPosition(1,4));
            MoveAction moveBlue = new MoveAction(Team.BLUE, blueCaptain, new BoardPosition(1,4), new BoardPosition(1,3));
            board.applyMove(moveBlue);
            System.out.println(board.transcript());
            System.out.println();
            
            // Test attack between redCaptain and blueCaptain should result in a
            // draw.
            // Red captain attacks blue captain.
            /**MoveAction attack = new MoveAction(Team.RED, redCaptain, new BoardPosition(1,2), new BoardPosition(1,3));
            board.applyMove(attack);
            System.out.println(board.transcript());
            System.out.println();*/
            // Blue captain attacks red captain.
            MoveAction attack = new MoveAction(Team.RED, redCaptain, new BoardPosition(1,2), new BoardPosition(1,3));
            //MoveAction attack = new MoveAction(Team.BLUE, blueCaptain, new BoardPosition(1,3), new BoardPosition(1,2));
            System.out.println("ATTACK");
            board.applyMove(attack);
            System.out.println(board.transcript());
            System.out.println();
            
            // Test undo of attack for both cases.
            System.out.println("UNDO MOVE");
            board.undoMove(attack);
            System.out.println(board.transcript());
            System.out.println();
            
        } catch (InvalidPositionException ex) {
            Logger.getLogger(BattleEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void testTranscript() {
        SetupGenerator generator = new SetupGenerator();
        GameBoard board = generator.generateSetup();
        System.out.println("Initial Board");
        System.out.println(board.transcript());
        System.out.println();
        
        // Create transcript.
        BattleTranscript transcript = new BattleTranscript(board);
        transcript.addMove(new MoveAction(
                Team.RED, new GamePiece(
                        Pieces.SERGEANT, Team.RED, new BoardPosition(2,1)), 
                new BoardPosition(2,1), new BoardPosition(2,2)));
        
        transcript.addMove(new MoveAction(
                Team.BLUE, new GamePiece(
                        Pieces.CAPTAIN, Team.BLUE, new BoardPosition(2,1)), 
                new BoardPosition(1,4), new BoardPosition(1,2)));
        
        transcript.addMove(new MoveAction(
                Team.RED, new GamePiece(
                        Pieces.SERGEANT, Team.RED, new BoardPosition(2,1)), 
                new BoardPosition(2,2), new BoardPosition(3,2)));
        /**
        transcript.addMove(new MoveAction(
                Team.BLUE, new GamePiece(
                        Pieces.CAPTAIN, Team.BLUE), 
                new BoardPosition(), new BoardPosition(2,3)));        
                */
        
        // Print transcript.
        transcript.print();
    }
    
    private void testAlphaBeta() {
        SetupGenerator generator = new SetupGenerator();
        GameBoard board = generator.generateSetup();
        
        try {
            board.removePieceAt(new BoardPosition(3,1));
            board.setupPiece(3, 2, Pieces.LIEUTENANT, Team.RED);
        } catch (InvalidPositionException ex) {
            Logger.getLogger(BattleEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Initial Board Setup:");
        System.out.println(board.transcript());
        
        GameState state = new GameState();
        state.setGameBoard(board);
        
        AIBot bot = new DefaultPlayer(Team.BLUE);
        MoveAction move = timedAIMove((GameState) state.clone(), 
                bot, 
                1500);
        
        System.out.println("Board Before Applying Move:");
        System.out.println(board.transcript());
        
        // Apply move.
        board.applyMove(move);
        System.out.println("Applied move:");
        System.out.println(move.toString());
        
        System.out.println("Board After Applying Move:");
        System.out.println(board.transcript());
        
        System.out.println("Attacker Moves: " + state.getGameBoard().getMoveCount());
    }
}
