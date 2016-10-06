package tools.deeplearning;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.GlobalSettings;
import Game.InvalidPositionException;
import Game.Pieces;
import Game.Team;
import Logic.Simulation;
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
        try {
            //new BattleEngine().runTests(); //initialize();
            //new BattleEngine().initialize();
            //new BattleEngine().testTranscript();
            //new BattleEngine().testApplyUndoMove();
            //new BattleEngine().testUnequalAttack();
            //new BattleEngine().testAlphaBeta();
            //new BattleEngine().smallBattle();
            //new BattleEngine().test();
            //new BattleEngine().testEndState();
            
            
            new BattleEngine().debug();
        } catch (InvalidPositionException ex) {
            Logger.getLogger(BattleEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        battle(board, attacker, defender, 3000, 40); //1500);
        System.out.println("Battle ended");
    }
    
    private void smallBattle() {
        GameBoard board = new GameBoard(2, 5, Team.RED, Team.BLUE);
        try {
            // Setup the teams.
            board.setupPiece(0, 0, Pieces.SERGEANT, Team.RED);
            board.setupPiece(0, 1, Pieces.LIEUTENANT, Team.RED);
            board.setupPiece(1, 0, Pieces.FLAG, Team.RED);
            board.setupPiece(1, 1, Pieces.CAPTAIN, Team.RED);
            
            board.setupPiece(1, 4, Pieces.SERGEANT, Team.BLUE);
            board.setupPiece(1, 3, Pieces.LIEUTENANT, Team.BLUE);
            board.setupPiece(0, 4, Pieces.FLAG, Team.BLUE);
            board.setupPiece(0, 3, Pieces.CAPTAIN, Team.BLUE);
        } catch (InvalidPositionException ex) {
            Logger.getLogger(BattleEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        AIBot attacker = new DefaultPlayer(Team.RED);
        AIBot defender = new DefaultPlayer(Team.BLUE);
        
        // Execute the battle.
        int computationTime = 2000;
        // Do we also want to limit the search depth?
        battle(board, attacker, defender, computationTime, 40);
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
    
    private void startDeepLearning() {
        
    }
    
    private void test() {
        GameBoard board = new GameBoard(2, 5, Team.RED, Team.BLUE);
        
        try {
            // Setup the teams.
            //board.setupPiece(0, 0, Pieces.SERGEANT, Team.RED);
            //board.setupPiece(0, 1, Pieces.LIEUTENANT, Team.RED);
            //board.setupPiece(1, 0, Pieces.FLAG, Team.RED);
            board.setupPiece(0, 2, Pieces.CAPTAIN, Team.RED);
            
            board.setupPiece(1, 4, Pieces.SERGEANT, Team.BLUE);
            board.setupPiece(1, 3, Pieces.LIEUTENANT, Team.BLUE);
            board.setupPiece(0, 4, Pieces.FLAG, Team.BLUE);
            board.setupPiece(1, 0, Pieces.CAPTAIN, Team.BLUE);
        } catch (InvalidPositionException ex) {
            Logger.getLogger(BattleEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(board.transcript());
        
        List<GamePiece> captains = board.getPieces(Team.RED, Pieces.CAPTAIN);
        System.out.println("Captains: " + captains.size());
        
        // Check if this is an endstate.
        Team winner = board.isEndState();
        System.out.println("Winner: " + winner);
        
        // Check if we win when the flag is surrounded by bombs and no miners
        // are left.
        
    }
    
    private void testEndState() {      
        String setup = "r:F|r:B|   |r:3\n" +
                        "--- --- --- ---\n" +
                        "r:B|r:3|b:5|r:4\n" +
                        "--- --- --- ---\n" +
                        "   |   |   |   \n" +
                        "--- --- --- ---\n" +
                        "   |   |   |   \n" +
                        "--- --- --- ---\n" +
                        "b:B|   |b:3|b:4\n" +
                        "--- --- --- ---\n" +
                        "b:F|b:B|b:2|b:3";
        GameBoard board = GameBoard.loadBoard(setup, 4, 6);
        // Print board.
        System.out.println(board.transcript());
        
        // Test end state.
        Team winner = board.isEndState();
        System.out.println("Winner: " + winner);
    }
    
    public BattleTranscript battle(GameState state, AIBot attacker, AIBot defender, 
            long computationTime, Simulation simulation) {
        
        // Attacker starts the game.
        AIBot currentTurn = attacker;
        
        // Start the game.
        state.setRunning(true);
        int totalMoves = 0;
        System.out.println("Start Battle with computation time: " + computationTime);
        
        // Initialize the battle transcript.
        GameBoard board = state.getGameBoard();
        BattleTranscript transcript = new BattleTranscript(board, 
                board.getAttacker(), board.getDefender());
        transcript.startGame();
        
        Team winner;
        while((winner = board.isEndState()) == null) {
            // Put a temporary hard limit for the moves.
            if(totalMoves >= 20) { //100) {
                System.out.println("Game exceeded 100 moves, terminate.");
                break;
            }
            
            // Request a move from the AI bot.
            // It is important to clone the game state to prevent corruption
            // of the game state, because when search algorithms timeout they
            // have not been able to undo the already applied moves and thereby
            // the game state gets corrupted. However game state corruption
            // should not happen if search algorithm finish in time before the
            // computation time expires.
            MoveAction move = timedAIMove((GameState) state.clone(), 
                    currentTurn, computationTime);
            
            // Player did not provide a move, this should never happen.
            if(move == null) {
                throw new RuntimeException("BattleEngine.battle():"
                        + " player has no move: " + currentTurn.getTeam());
            }
            
            // Store the move in the battle transcript.
            transcript.addMove(move);
            
            // Print informative information about the move.
            System.out.println("move#" + totalMoves + " " + move.toString());
            
            // Submit the move to the simulation.
            simulation.processAction(move);
            
            // Show the board state after the move.
            System.out.println(board.transcript());
            
            // Swap turns.
            if(currentTurn == attacker) {
                currentTurn = defender;
            } else {
                currentTurn = attacker;
            }
            
            // Keep track of the number of applied moves (both teams)
            totalMoves++;
        }
        
        // End the game.
        state.setRunning(false);
        transcript.endGame();
        // Print informative information about the battle result.
        System.out.println("Game Ended in " + (state.getGameDuration() / 1000d) + " s.");
        System.out.println("Winner: " + winner);
        // Print the battle transcript.
        transcript.print();
        transcript.setWinner(winner);
        
        return transcript;
    }
    
    public void battle(GameBoard board, AIBot attacker, AIBot defender, 
            long computationTime, int maxIterations) {
        // Wrap around in a GameState
        GameState state = new GameState();
        state.setGameBoard(board);
        
        // Attacker starts the game.
        AIBot currentTurn = attacker;
        
        // Check if setups are valid.
        
        // Simulate the game.
        state.setRunning(true);
        int iterations = 0;
        System.out.println("Start Game with computation time: " + computationTime);
        BattleTranscript transcript = new BattleTranscript(board, 
                board.getAttacker(), board.getDefender());
        
        System.out.println("Attacking team: " + board.getAttacker());
        System.out.println("Defending team: " + board.getDefender());
        
        Team winner;
        while((winner = board.isEndState()) == null) {
            if(maxIterations != -1 && iterations >= maxIterations) {
                System.out.println("ENDED LONG MATCH");
                break;
            }
            
            //System.out.println("Invoke Move");
            MoveAction move = timedAIMove((GameState) state.clone(), 
                    currentTurn, 
                    computationTime);
            // Player unable to supply a move, should not happen.
            if(move == null) {
                throw new RuntimeException("Player has no move: " + currentTurn.getTeam());
            }
            
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
        }
        
        state.setRunning(false);
        System.out.println("Game Ended in " + (state.getGameDuration() / 1000d) + " s.");
        System.out.println("Winner: " + winner);
        transcript.print();
    }
    
    private boolean interrupted;
    
    private MoveAction timedAIMove(GameState state, final AIBot bot, long computationTime) {
        interrupted = false;
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                interrupted = true;
                bot.stop();
            }
        };
        
        timer.schedule(task, computationTime);
        MoveAction move = bot.nextMove(state);
        System.out.println("Interrupted: " + interrupted);
        task.cancel();
        return move;
    }
    
    // Calculate a random setup for the player.
    
    // Make random moves.
    
    public BattleTranscript battle() {
        return null;
    }
    
    public void debug() throws InvalidPositionException {
        SetupGenerator generator = new SetupGenerator();
        GameBoard board = generator.mirroredSetup();
        System.out.println("Initial Board");
        System.out.println(board.transcript());
        
        /**
        GamePiece redCaptain = new GamePiece(Pieces.CAPTAIN, Team.RED, new BoardPosition(0, 1));
        MoveAction move = new MoveAction(Team.RED, redCaptain, new BoardPosition(0, 1), new BoardPosition(0, 2));
        board.applyMove(move);
        System.out.println(board.transcript());
        System.out.println();
        
        GamePiece blueCaptain = new GamePiece(Pieces.CAPTAIN, Team.BLUE, new BoardPosition(0, 3));
        MoveAction attack = new MoveAction(Team.BLUE, blueCaptain, new BoardPosition(0, 3), new BoardPosition(0, 2));
        board.applyMove(attack);
        System.out.println(board.transcript());
        System.out.println();
        */
        
        GamePiece redCaptain = board.getPiece(new BoardPosition(0, 1));
        MoveAction move = new MoveAction(Team.RED, redCaptain, redCaptain.getPosition(), new BoardPosition(0,2));
        board.applyMove(move);
        System.out.println(board.transcript());
        System.out.println();
        
        GamePiece blueCaptain = board.getPiece(new BoardPosition(0,3));
        MoveAction attack = new MoveAction(Team.BLUE, blueCaptain, blueCaptain.getPosition(), new BoardPosition(0,2));
        board.applyMove(attack);
        System.out.println(board.transcript());
        System.out.println();
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
        BattleTranscript transcript = new BattleTranscript(board, 
                board.getAttacker(), 
                board.getDefender());
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
