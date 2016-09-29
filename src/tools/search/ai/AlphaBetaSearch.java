package tools.search.ai;

import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.InvalidPositionException;
import Game.Pieces;
import Game.Team;
import actions.MoveAction;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.deeplearning.BattleEngine;

/**
 * A class that implements the miniMax and alphaBetaPruning algorithm wrapped
 * around by iterative deepening. The class must be configured to use a custom
 * heuristic.
 */
public class AlphaBetaSearch {
    
    public static void main(String[] args) {
        new AlphaBetaSearch(null).iterativeDeepeningTest();
    }
    
    // Evaluation function to be used by miniMax or alphaBeta
    private HeuristicEvaluation evaluation;
    // Indicates if the algorithm was timed out.
    private boolean timeout;
    // Deepest depth reached by the most recently executed miniMax or alphaBeta
    // algorithm.
    private int deepestDepth;
    // # of explored nodes in the most recently executed iteration of miniMax
    // or alphaBeta, beaware that if the algorithm was interrupted, this value
    // expresses how many nodes it explored before it got interrupted and not
    // the total size of the tree with max depth d.
    private int exploredNodes;
    
    public AlphaBetaSearch(HeuristicEvaluation evaluation) {
        this.evaluation = evaluation;
        this.timeout = false;
        this.deepestDepth = 0;
    }
    
    public double search(GameNode node, int alpha, int beta) {
        GameState state = node.getState();
        GameBoard board = state.getGameBoard();
        List<MoveAction> moves = board.getMoves(Team.RED);
        MoveAction bestMove = null;
        double bestResult = 0;
        for(MoveAction move : moves) {
            // Apply move to state.
            
            // Compute heursitic value for this state.
            double value = this.evaluation.score(state);
            
            // Create node with new state.
            GameNode next = new GameNode(state);
            // Recursive alpha beta call
            double result = search(node, alpha, beta);
            if(result > bestResult) {
                bestResult = result;
                bestMove = move;
            }
            
            // Undo move.
        }
        
        // Store best move.
        node.setBestMove(bestMove);
        return bestResult;
    }
    
    public double searchIterative(GameNode node, int alpha, int beta) {
        return 0;
    }
    
    private void reset() {
        this.deepestDepth = 0;
        this.exploredNodes = 0;
    }
    
    public void iterativeDeepeningTest() {
        GameState state = new GameState();
        GameBoard board = new GameBoard(4, 6, Team.RED, Team.BLUE);        
        
        // Fixed Setup.
        try {
            // Attacker
            board.setupPiece(0, 0, Pieces.FLAG, Team.RED);
            board.setupPiece(0, 1, Pieces.BOMB, Team.RED);
            board.setupPiece(1, 0, Pieces.BOMB, Team.RED);
            board.setupPiece(2, 0, Pieces.MINER, Team.RED);
            board.setupPiece(3, 0, Pieces.SERGEANT, Team.RED);
            board.setupPiece(1, 1, Pieces.CAPTAIN, Team.RED);
            board.setupPiece(2, 1, Pieces.SERGEANT, Team.RED);
            board.setupPiece(3, 1, Pieces.LIEUTENANT, Team.RED);
            
            // Defender
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
        
        // Assign the board to the state.
        state.setGameBoard(board);
        
        // Set the correct Heuristic.
        setHeuristic(new HeuristicEvaluation() {
            @Override
            public double score(GameState state) {
                int score = 0;
                List<GamePiece> pieces = board.getTeam(board.getAttacker());
                for(GamePiece piece : pieces) {
                    score+= piece.getRank().ordinal();
                }
                return score;
            }
        });
        
        // Print board.
        System.out.println(board.transcript());
        
        GameNode node = new GameNode(state);
        node.setBestMove(null);
        
        System.out.println("Start Timer.");
        long computationTime = 5000; //10000; //1500;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeout();
            }
        }, computationTime);
        
        System.out.println("Test Iterative Deepening");
        long start = System.currentTimeMillis();
        //MoveAction move = iterativeDeepeningMinimax(node, true);
        int range = 20;
        //MoveAction move = iterativeDeepeningMinimax(node, 1, range, true);
        MoveAction move = iterativeDeepeningAlphaBeta(node, 1, range, true);
        timer.cancel();
        
        long end = System.currentTimeMillis();
        System.out.println("Iterative Deepening ended in " + (end - start) + " ms.");
        System.out.println("Reached Depth: " + getDeepestDepth());
        System.out.println("Explored Nodes: " + getExploredCount());
        
        if(move == null) {
            System.out.println("MOVE == null");
        } else {
            System.out.println(move.toString());
        }
    }
    
    public MoveAction iterativeDeepeningAlphaBeta(GameNode node, 
            int initialDepth, int range, boolean isAttacker) {
        // Reset counters.
        reset();
   
        // Initial depth must be at least 1, if it is 0, you remain in the
        // current state and do not get a best move.
        if(initialDepth < 1) {
            throw new IllegalArgumentException("Initial depth must be > 0");
        }
        // Range can be the special value "-1" or else range >= 0.
        if(range < -1) {
            throw new IllegalArgumentException("Range must be >= 0");
        }
        
        //int maxDepth = 2; //10;
        int maxDepth = initialDepth;
        
        // Initialize best value with the worst case value for the specific
        // player either minimizing or maximizing.
        double bestValue;
        if(isAttacker) {
            // Worst case value for the maximizing player.
            bestValue = Double.NEGATIVE_INFINITY;
        } else {
            // Worst case value for the minimizing player.
            bestValue = Double.POSITIVE_INFINITY;
        }

        // Apply iterative deepening.
        MoveAction bestMove = null;
        try {
            while(!this.timeout) {
                // Range equal to -1 means that the algorithm should use
                // an infinite range and go as deep as it can in the supplied
                // computation time frame. If range is set to any number
                // greater or equal to zero then the algorithm will have as
                // maximum depth = initialDepth + range.
                if(range != -1 && maxDepth - initialDepth > range) {
                    break;
                }
                
                // Reset best move. TODO is this useful?
                node.setBestMove(null);
                // Run minimax.
                System.out.println("Run alphaBeta with max depth: " + maxDepth);
                
                // Reset explored nodes count.
                this.exploredNodes = 0;
                boolean maxPlayer = false;
                if(isAttacker) {
                    maxPlayer = true;
                }
                
                double alpha = Double.NEGATIVE_INFINITY;
                double beta = Double.POSITIVE_INFINITY;
                
                // TODO REVISE THE ALPHA BETA PRUNING!
                double value = alphaBetaMiniMax(node, maxDepth, alpha, beta, maxPlayer);
                
                System.out.println("Nodes visited in iteration: " + this.exploredNodes);
                System.out.println("Retrieved Value: " + value);
                
                // Update the currently deepest reached depth in the search tree.
                this.deepestDepth = maxDepth;
                
                if((isAttacker && (value > bestValue)) 
                        || (!isAttacker && (bestValue > value))) {
                    
                    // FOR DEBUGGING PURPOSES
                    if(value > bestValue) {
                        System.out.println(value + " > " + bestValue);
                    } else {
                        System.out.println(bestValue + " > " + value);
                    }
                    bestValue = value;
                    bestMove = node.getBestMove();
                }
                
                // Increment max depth to search one layer deeper in the next
                // iteration.
                maxDepth++;
            }
        } catch (TimeoutException ex) {
        }
        // Reset timeout.
        this.timeout = false;
        // Return best move.
        return bestMove;
    }
    
    public MoveAction iterativeDeepeningMinimax(GameNode node, boolean isAttacker) {
        int initialDepth = 2; //10;
        int range = -1;
        return iterativeDeepeningMinimax(node, initialDepth, range, isAttacker);
    }
    
    public MoveAction iterativeDeepeningMinimax(GameNode node, 
            int initialDepth, 
            int range, 
            boolean isMaxPlayer) {
        // Reset counters.
        reset();
        
        // Initial depth must be at least 1, if it is 0, you remain in the
        // current state and do not get a best move.
        if(initialDepth < 1) {
            initialDepth = 1;
        }
        // Range can take the special value -1 or range >= 0
        if(range < -1) {
            throw new IllegalArgumentException("Range must be >= 0");
        }
        
        //int maxDepth = 2; //10;
        int maxDepth = initialDepth;
        double bestValue;
        // Initialize bestValue
        if(isMaxPlayer) {
            // Worst case for the maximizing player.
            bestValue = Double.NEGATIVE_INFINITY;
        } else {
            // Worst case for the minimizing player.
            bestValue = Double.POSITIVE_INFINITY;
        }
        
        MoveAction bestMove = null;
        // Apply iterative deepening.
        try {
            while(!this.timeout) {
                if(range != -1 && maxDepth - initialDepth > range) {
                    break;
                }
                
                // Reset best move.
                node.setBestMove(null);
                // Run minimax.
                System.out.println("Run miniMax with max depth: " + maxDepth);
                
                // Reset explored nodes count.
                this.exploredNodes = 0;
                System.out.println("isMaxPlayer: " + isMaxPlayer);
                
                double value = miniMax(node, maxDepth, isMaxPlayer);
                System.out.println("Explored in Iteration: " + this.exploredNodes);
                System.out.println("Value: " + value);
                this.deepestDepth = maxDepth;
                
                // If this is the minimizing player you want to store the
                // smallest possible value.
                // If this is the maximizing player you wan to store the
                // greatest value.
                if(isMaxPlayer && value > bestValue || !isMaxPlayer && bestValue > value) {
                    bestValue = value;
                    bestMove = node.getBestMove();
                }

                // Increment max depth.
                maxDepth++;
            }
        } catch (TimeoutException ex) {
        }
        
        // Reset timeout.
        this.timeout = false;
        // Return best move.
        return bestMove;
    }
    
    public double miniMax(GameNode node, int depth, boolean maxPlayer) 
            throws TimeoutException {
        // Keep track of the # of explored nodes.
        this.exploredNodes++;
        
        // Algorithm must immediately terminate if the timeout is signalled.
        if(this.timeout) {
            throw new TimeoutException();
        }
        
        GameState state = node.getState();
        GameBoard board = state.getGameBoard();
        //System.out.println("BoardState at " + depth);
        //System.out.println(board.transcript());        
        
        Team winner = board.isEndState();
        // If winner != null, means we reached an end state.
        if(winner != null) {
            System.out.println("END STATE REACHED");
            // What score should we give to an end state?
            if(maxPlayer) {
                if(winner == Team.RED) {
                    return Double.POSITIVE_INFINITY;
                } else {
                    return Double.NEGATIVE_INFINITY;
                }
            } else {
                // Minimizing Player.
                if(winner == Team.BLUE) {
                    return Double.NEGATIVE_INFINITY;
                } else {
                    return Double.POSITIVE_INFINITY;
                }
            }
        }
        
        // Reached maximum depth.
        if(depth == 0) {
            // Calculate the heuristic value.
            return this.evaluation.score(state);
        }
        
        double bestValue;
        MoveAction bestMove = null;
        if(maxPlayer) { // Maximizing player.
            bestValue = Double.NEGATIVE_INFINITY; // Worst case for maximizing player.
            List<MoveAction> moves = board.getMoves(Team.RED);
            for(MoveAction move : moves) {
                // Apply move.
                board.applyMove(move);
                
                // Recursive call.
                // TODO, probably do not need to create a new GameNode
                GameNode next = new GameNode(state);
                
                // Recursive call with DFS implementation.
                double value = miniMax(next, depth - 1, false);
                //System.out.println("MaxPlayer value: " + value);
                // Try to maximize the value over all nodes.
                if(value > bestValue) {
                    //System.out.println(value + " > " + bestValue);
                    bestValue = value;
                    bestMove = move;
                } else {
                    //System.out.println("NOT " + value + " > " + bestValue);
                }
                
                //System.out.println("UNDO MOVE");
                
                // Undo move.
                board.undoMove(move);
            }
        } else {
            // Minimizing player.
            bestValue = Double.POSITIVE_INFINITY; // Worst case for minimizing player.
            List<MoveAction> moves = board.getMoves(Team.BLUE);
            for(MoveAction move : moves) {
                // Apply move.
                board.applyMove(move);
                
                GameNode next = new GameNode(state);
                
                // Recursive call with DFS implementation.
                double value = miniMax(next, depth - 1, true);
                //System.out.println("MinPlayer value: " + value);
                // Try to minimize the value over all nodes.
                if(bestValue > value) {
                    //System.out.println(bestValue + " > " + value);
                    bestValue = value;
                    bestMove = move;
                } else {
                    //System.out.println("NOT " + bestValue + " > " + value);
                }
                
                //System.out.println("UNDO MOVE");
                
                // Undo move.
                board.undoMove(move);
            }
        }
        
        // Store the best move in the node.
        // TODO Actually only necessary at the initial call. Not useful
        // for lower depths.
        node.setBestMove(bestMove);
        //System.out.println(depth + " bestValue = " + bestValue);
        return bestValue;
    }
    
    public double miniMaxShort(GameNode node, int depth, boolean isMaxPlayer) throws TimeoutException {
        // Keep track of the # of explored nodes.
        this.exploredNodes++;
        
        if(this.timeout) {
            throw new TimeoutException();
        }
        
        GameBoard board = node.getState().getGameBoard();
        // Did we reach an endstate.
        Team winner = board.isEndState();
        // If winner != null, means we reached an end state.
        if(winner != null) {
            System.out.println("END STATE REACHED");
            // What score should we give to an end state?
            if(isMaxPlayer) {
                if(winner == Team.RED) {
                    return Double.POSITIVE_INFINITY;
                } else {
                    return Double.NEGATIVE_INFINITY;
                }
            } else {
                // Minimizing Player.
                if(winner == Team.BLUE) {
                    return Double.NEGATIVE_INFINITY;
                } else {
                    return Double.POSITIVE_INFINITY;
                }
            }
        }        
        
        // Reached maximum search depth.
        // What happens if we reach 0, while it is still an end state? Should
        // we first check for endstate?
        // With what board dimensinos can we utilize exhaustive search.
        if(depth == 0) {
            return this.evaluation.score(node.getState());
        }
        
        double bestValue = Double.POSITIVE_INFINITY; // Default for the minimzing player.
        MoveAction bestMove = null;
        if(isMaxPlayer) {
            bestValue = Double.NEGATIVE_INFINITY;
        }
        
        List<MoveAction> moves;
        if(isMaxPlayer) {
            moves = board.getMoves(Team.RED);
        } else {
            moves = board.getMoves(Team.BLUE);
        }
        
        // Apply all the moves in DFS fashion.
        for(MoveAction move : moves) {
            // Apply move.
            board.applyMove(move);

            // Recursive call.
            //GameNode next = new GameNode(state);
            // Only the board is modified and the change is transparent to
            // the node object, so we can re-use the same node object.
            // Beware that setBestMove at each treenode now modifies the same
            // node object, this is fine since the last setBestMove call is
            // invoked at the root node and thus gives us the best next move.
            double value = miniMax(node, depth - 1, !isMaxPlayer);
            if(isMaxPlayer) {
                if(value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
            } else {
                if(bestValue > value) {
                    bestValue = value;
                    bestMove = move;
                }
            }
            // Undo move.
            board.undoMove(move);
        }
        
        // Store best move in the node.
        node.setBestMove(bestMove);
        return bestValue;
    }
    
    // Re-use results.
    // Implement the Iterative version.
    public double alphaBetaMiniMax(GameNode node, 
            int depth, 
            double alpha, 
            double beta, 
            boolean isMaxPlayer)
            throws TimeoutException {
        // Keep track of the # of explored nodes.
        this.exploredNodes++;
        
        // Algorithm must immediately terminate if the timeout is signalled.
        if(this.timeout) {
            throw new TimeoutException();
        }
        
        GameState state = node.getState();
        GameBoard board = state.getGameBoard();
        //System.out.println("BoardState at " + depth);
        //System.out.println(board.transcript());        

        // Endstate reached?
        Team winner = board.isEndState();
        // If winner != null, means we reached an end state.
        if(winner != null) {
            //System.out.println("END STATE REACHED");
            // What score should we give to an end state?
            if(isMaxPlayer) {
                if(winner == Team.RED) {
                    return Double.POSITIVE_INFINITY;
                } else {
                    return Double.NEGATIVE_INFINITY;
                }
            } else {
                // Minimizing Player.
                if(winner == Team.BLUE) {
                    return Double.NEGATIVE_INFINITY;
                } else {
                    return Double.POSITIVE_INFINITY;
                }
            }
        }
        
        // Reached maximum depth.
        if(depth == 0) {
            // Calculate the heuristic value.
            return this.evaluation.score(state);
        }
        
        double value;
        double bestValue;
        MoveAction bestMove = null;
        if(isMaxPlayer) { // Maximizing player.
            value = Double.NEGATIVE_INFINITY;
            bestValue = Double.NEGATIVE_INFINITY;
            List<MoveAction> moves = board.getMoves(Team.RED);
            for(MoveAction move : moves) {
                // Apply move.
                board.applyMove(move);
                
                // Recursive call.
                //GameNode next = new GameNode(state);
                value = Math.max(value, alphaBetaMiniMax(node, depth - 1, alpha, beta, false));
                if(value > bestValue) {
                    bestValue = value;
                    bestMove = move;
                }
                
                alpha = Math.max(alpha, value);
                // Beta-cutoff.
                if(beta <= alpha) {
                    break;
                }
                
                // Undo move.
                board.undoMove(move);
            }
        } else {
            // Minimizing player.
            value = Double.POSITIVE_INFINITY;
            bestValue = Double.POSITIVE_INFINITY;
            List<MoveAction> moves = board.getMoves(Team.BLUE);
            for(MoveAction move : moves) {
                // Apply move.
                board.applyMove(move);
                
                //GameNode next = new GameNode(state);
                value = Math.min(value, alphaBetaMiniMax(node, depth - 1, alpha, beta, true));
                // Keep the smallest value.
                if(bestValue > value) {
                    bestValue = value;
                    bestMove = move;
                }
                
                beta = Math.min(beta, value);
                // Alpha-cutoff.
                if(beta <= alpha) {
                    break;
                }
                
                // Undo move.
                board.undoMove(move);
            }
        }
        // Store the best move in the node.
        node.setBestMove(bestMove);
        return bestValue;
    }
    
    /**
     * Interrupts the currently running algorithm and forces it
     * to return a move as fast as possible.
     */
    public void timeout() {
        timeout = true;
    }
    
    public int getDeepestDepth() {
        return this.deepestDepth;
    }
    
    public int getExploredCount() {
        return this.exploredNodes;
    }
    
    public void setHeuristic(HeuristicEvaluation evaluation) {
        this.evaluation = evaluation;
    }
}
