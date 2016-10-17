package tools.search.ai;

import Game.GameBoard;
import Game.GameState;
import Game.Team;
import actions.MoveAction;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import tools.search.ai.players.Attacker;

/**
 *
 */
public class TreeSearch {
    
    // Evaluation function to be used by search algorithm to evaluate board
    // state.
    private HeuristicEvaluation evaluation;
    
    // Flag that indicates the algorithm must be interrupted.
    private boolean timeout;
    
    private int nodeCount;
    
    private boolean initialPlayer;
    
    private int cutoffs;
    
    private boolean cutoffOn = false;
    
    private boolean moveOrderOn = true;
    
    private int alphaBestMovesPops = 0;
    
    private Stack<MoveAction> iterationBestMoves;
    private Stack<MoveAction> alphaBetaBestMoves;
    private int iterationBest = 0;
    
    //private Stack<MoveAction> alphaBetaPVPath;
    private MoveAction[] alphaBetaPVPath;
    private double[] alphaBetaPVScore;
    private int currentMaxDepth = 0;
    
    public TreeSearch(HeuristicEvaluation evaluation) {
        this.timeout = false;
        this.evaluation = evaluation;
        this.cutoffs = 0;
    }
    
    /**
     * Interrupts the currently running algorithm and forces it
     * to return a move as fast as possible.
     */
    public void timeout() {
        this.timeout = true;
    }
    
    public void setHeuristic(HeuristicEvaluation evaluation) {
        this.evaluation = evaluation;
    }
    
    private class SearchResult {
        
        private Stack<Integer> exploredNodes;
        private MoveAction bestMove;
        private Stack<Long> iterationTimes;
        private Stack<MoveAction> principalVariationPath;
        
        public SearchResult() {
            this.exploredNodes = new Stack<>();
        }

        public Stack<Integer> getExploredNodes() {
            return exploredNodes;
        }

        public void setExploredNodes(Stack<Integer> exploredNodes) {
            this.exploredNodes = exploredNodes;
        }

        public MoveAction getBestMove() {
            return bestMove;
        }

        public void setBestMove(MoveAction bestMove) {
            this.bestMove = bestMove;
        }

        public Stack<Long> getIterationTimes() {
            return iterationTimes;
        }

        public void setIterationTimes(Stack<Long> iterationTimes) {
            this.iterationTimes = iterationTimes;
        }

        public Stack<MoveAction> getPrincipalVariationPath() {
            return principalVariationPath;
        }

        public void setPrincipalVariationPath(Stack<MoveAction> principalVariationPath) {
            this.principalVariationPath = principalVariationPath;
        }
    }
    
    public SearchResult iterativeDeepeningAlphaBeta(
            GameNode node, 
            int initialDepth, 
            int range, 
            boolean isMaxPlayer) {
        // Reset counters.
        //reset();
        
        // Reset timeout.
        this.timeout = false;
        
        // Initial depth must be at least 1, if it is 0, you remain in the
        // current state, since no moves are applied and therefore a best move
        // will not be set.
        if(initialDepth < 1) {
            throw new IllegalArgumentException("Initial depth must be >= 1");
        }
        
        // Range can take the special value -1 or range >= 0
        if(range < -1) {
            throw new IllegalArgumentException("Range must be >= 0 "
                    + "or the special value -1 for an infinite range.");
        }
        
        // Initialize current depth.
        int currentDepth = initialDepth;
        
        // Store the player that resides at the root, needed to give the correct
        // value for a terminal node, since its value must be from the
        // perspective of the root player.
        this.initialPlayer = isMaxPlayer;
        
        MoveAction bestMove = null;
        System.out.println("isMaxPlayer: " + isMaxPlayer);
        SearchResult result = new SearchResult();
        Stack<Integer> exploredNodes = new Stack<>();
        Stack<Long> iterationTimes = new Stack<>();
        this.iterationBestMoves = new Stack<>();
        this.alphaBetaBestMoves = new Stack<>();
        this.moveOrderOn = false;
        //this.alphaBetaPVPath = new Stack<>();
        
        Stack<MoveAction> principalVariationPath = new Stack<>();
        //principalVariationPath.ensureCapacity(20);
        //principalVariationPath.setSize(100);
        
        // Apply iterative deepening.
        try {
            while(!this.timeout) {
                // Check if range has been exceeded.
                if(range != -1 && currentDepth - initialDepth > range) {
                    System.out.println("Exceeded range.");
                    break;
                }
                
                // Reset best move.
                node.setBestMove(null);
                // Run alpha beta.
                System.out.println("Run IDAlphaBeta with max depth: " + currentDepth);
                
                // Reset counters.
                //this.exploredNodes = 0;
                this.cutoffs = 0;
                this.nodeCount = 0;
                this.alphaBestMovesPops = 0;
                this.currentMaxDepth = currentDepth;
                // Initialize the principal path array.
                this.alphaBetaPVPath = new MoveAction[this.currentMaxDepth];
                this.alphaBetaPVScore = new double[this.alphaBetaPVPath.length];
                // Initialize score array.
                for(int j=0; j<this.alphaBetaPVScore.length; j+=2) {
                    if(isMaxPlayer) {
                        this.alphaBetaPVScore[j] = Double.NEGATIVE_INFINITY;
                        if(j + 1 < this.alphaBetaPVScore.length) {
                            this.alphaBetaPVScore[j+1] = Double.POSITIVE_INFINITY;
                        }
                    } else {
                        this.alphaBetaPVScore[j] = Double.POSITIVE_INFINITY;
                        if(j + 1 < this.alphaBetaPVScore.length) {
                            this.alphaBetaPVScore[j+1] = Double.NEGATIVE_INFINITY;
                        }
                    }
                }
                
                principalVariationPath.clear();
                
                long start = System.currentTimeMillis();
                double value = alphaBeta(
                        node, 
                        currentDepth, 
                        Double.NEGATIVE_INFINITY, 
                        Double.POSITIVE_INFINITY, 
                        isMaxPlayer);
                long end = System.currentTimeMillis();
                
                System.out.println("Produced Cutoffs: " + this.cutoffs);
                // Deep copy the list.
                System.out.println("PV Path Size: " + this.alphaBetaBestMoves.size());
                //Collections.copy(this.alphaBetaBestMoves, principalVariationPath);
                Iterator<MoveAction> iterator = this.alphaBetaBestMoves.listIterator();
                while(iterator.hasNext()) {
                    principalVariationPath.insertElementAt(iterator.next(), 0);
                }
                
                System.out.println("PV Copied Path Size: " + principalVariationPath.size());
                //System.out.println("Alphabeta PV Path Size: " + this.alphaBetaPVPath.size());
                
                // The initial iteration it must be filled and the data must
                // be used to enhance the next iteration.
                if(currentDepth > initialDepth + 1) {
                    //this.moveOrderOn = true;
                    //this.alphaBetaBestMoves.clear();
                }
                
                iterationTimes.push((end - start));
                
                // Store node count.
                exploredNodes.push(this.nodeCount);
                
                // Update best move.
                bestMove = node.getBestMove();
                this.iterationBestMoves.push(bestMove);
                // Check if bestMove is okay.
                // TODO remove this check if this function is stable.
                if(!bestMove.isOkay()) {
                    throw new RuntimeException("Corrupt MoveAction: " + 
                            bestMove.toString());
                }
                
                // If the worst possible value occurs for a player, it means
                // that the game is inevitably lost when the opponent continues
                // perfect play. The returned move will be none and
                // continueing iterative deepening is now pointless.
                // Can it occur that our current bestMove is none when this
                // occurs, what do we do then?
                if(isMaxPlayer && value == Double.NEGATIVE_INFINITY ||
                        !isMaxPlayer && value == Double.POSITIVE_INFINITY) {
                    System.out.println("Inevitable Loss: " + value);
                    if(bestMove == null) {
                        throw new RuntimeException("bestMove was never set.");
                    }
                    break;
                }
                
                // If the best possible value occurs for a player, it means that
                // the player can force a win.
                if(isMaxPlayer && value == Double.POSITIVE_INFINITY ||
                        !isMaxPlayer && value == Double.NEGATIVE_INFINITY) {
                    System.out.println("Inevitable Win: " + value);
                    if(bestMove == null) {
                        throw new RuntimeException("bestMove was never set.");
                    }
                    break;
                }
                
                System.out.println("Explored in Iteration: " + this.nodeCount);
                System.out.println("Value: " + value);
                System.out.println("Best Move for max depth: " + currentDepth + " is " + bestMove.toString());
                System.out.println();

                // Increment maximum depth.
                currentDepth++;
            }
        } catch (TimeoutException ex) {
            System.out.println("TIMED OUT");
        }
        
        // Reset timeout.
        this.timeout = false;

        result.setBestMove(bestMove);
        result.setExploredNodes(exploredNodes);
        result.setIterationTimes(iterationTimes);
        result.setPrincipalVariationPath(principalVariationPath);
        
        // Print PV Path.
        System.out.println("Correct PV Path:");
        int k = 0;
        for(MoveAction move : this.alphaBetaPVPath) {
            System.out.println(k + ": " + move.toString());
            k++;
        }
        
        System.out.println("AlphaBestMove Pops: " + this.alphaBestMovesPops);
        
        // Return best move.
        return result;
    }
    
    public double alphaBeta(
            GameNode node, 
            int depth, 
            double alpha, 
            double beta, 
            boolean maxPlayer) 
            throws TimeoutException {
//        System.out.println("AlphaBeta depth: " + depth + ", alpha: " 
//                + alpha + ", beta: " + beta + ", isMax: " + maxPlayer);
        
        // Keep track of the # of visited nodes in the current search tree.
        this.nodeCount++;
        
        // Algorithm must immediately terminate if the timeout is signalled.
        if(this.timeout) {
            throw new TimeoutException();
        }
        
        GameState state = node.getState();
        GameBoard board = state.getGameBoard();
        //System.out.println("BoardState at " + depth);
        //System.out.println(board.transcript());        
        
        // Reached a terminal node. (Game ended)
        Team winner = board.isEndState();
        // If winner != null, means we reached an end state.
        if(winner != null) {
            System.out.println("ENDSTATE REACHED");
            // The end state should be evaluated with respect to the perspective
            // of the player at the root. The maximizing player is assumed to be
            // the RED team and the minimizing player is assumed to be the BLUE
            // team.
            if(this.initialPlayer) {
                // Initial call was by the maxiziming player (RED).
                if(winner == Team.RED) {
                    return Double.POSITIVE_INFINITY;
                } else {
                    return Double.NEGATIVE_INFINITY;
                }
            } else { 
                // Initial call was by the minimizing player (BLUE).
                if(winner == Team.BLUE) {
                    return Double.NEGATIVE_INFINITY;
                } else {
                    return Double.POSITIVE_INFINITY;
                }
            }
        }
        
        // Reached maximum depth.
        if(depth == 0) {
            // Calculate the heuristic value for this board state.
            return this.evaluation.score(state);
        }
        
        MoveAction previousBestMove = null;
        if(!this.alphaBetaBestMoves.isEmpty()) {
            previousBestMove = this.alphaBetaBestMoves.pop(); // The best move at the current depth in the previous iteration.
            this.alphaBestMovesPops++;
        }
        
        double bestValue;
        MoveAction bestMove = null;
        if(maxPlayer) { // Maximizing player. (Team RED)
            bestValue = Double.NEGATIVE_INFINITY; // Worst case for maximizing player.
            //bestValue = alpha;
            List<MoveAction> moves = board.getMoves(Team.RED);
            // Sort the moves.
            if(this.moveOrderOn) {
                System.out.println("Order Moves (Max) at " + depth);
                orderMoves(moves, previousBestMove);
            }
            
            int i = 0;
            for(MoveAction move : moves) {
                //System.out.println("APPLY" + depth + ":::" + move.toString());
                // Apply move.
                board.applyMove(move);
                //System.out.println("MaxPlayer Applied move: " + move.toString());
                
                if(i == 0) {
                    //System.out.println("Max Init Push: " + move.toString());
                    //this.alphaBetaPVPath.push(move);
                    //this.alphaBetaPVPath[this.currentMaxDepth - depth] = move;
                    //this.alphaBetaPVScore[this.currentMaxDepth - depth] = Double.NEGATIVE_INFINITY;
                }
                
                // Recursive call.
                // TODO, probably do not need to create a new GameNode
                // How much time does this slow down the search?
                GameNode next = new GameNode(state);
                
                // Recursive call with DFS implementation.
                double value = alphaBeta(next, depth - 1, alpha, beta, false);
                //System.out.println("MaxPlayer Depth: " + depth + ", value: " + value);
                
                //System.out.println(move.toString());
                //System.out.println("MaxPlayer value: " + value);
                // Try to maximize the value over all nodes.
                
                if(value >= bestValue) {
                    //System.out.println("Max Pop: " + this.alphaBetaPVPath.pop().toString());
                    //System.out.println("Max Push: " + move.toString());
                    //this.alphaBetaPVPath.push(move);
                    /**
                    if(value >= this.alphaBetaPVScore[this.currentMaxDepth - depth]) {
                        System.out.println("Max Set: " + move.toString() + " " + value + " >= " + this.alphaBetaPVScore[this.currentMaxDepth - depth]);
                        this.alphaBetaPVPath[this.currentMaxDepth - depth] = move;
                        this.alphaBetaPVScore[this.currentMaxDepth - depth] = value;
                    }*/
                    //System.out.println(value + " > " + bestValue);
                    bestValue = value;
                    bestMove = move;
                    alpha = value;
                } else {
                    //System.out.println("NOT " + value + " > " + bestValue);
                }
                
                // Undo move. (Important to do this before the cutoff)
                //System.out.println("UNDO " + depth + ":::" + move.toString());
                board.undoMove(move);
                //System.out.println("MAX UNDO MOVE");
                
                i++;
                
                // Cutoff.
                // Should not be <=! TODO Why?
                if(this.cutoffOn && beta < alpha) {
                    // Count the number of cutoffs.
                    this.cutoffs++;
                    break;
                }
            }
        } else {
            // Minimizing player. (Team BLUE)
            bestValue = Double.POSITIVE_INFINITY; // Worst case for minimizing player.
            //bestValue = beta;
            List<MoveAction> moves = board.getMoves(Team.BLUE);
            // Sort the moves.
            if(this.moveOrderOn) {
                System.out.println("Order Moves (Min) at " + depth);
                orderMoves(moves, previousBestMove);            
            }
            
            int i = 0;
            for(MoveAction move : moves) {
                //System.out.println("APPLY" + depth + ":::" + move.toString());
                // Apply move.
                board.applyMove(move);
                //System.out.println("MinPlayer Applied move: " + move.toString());
                
                if(i == 0) {
                    System.out.println("Min Init Push: " + move.toString());
                    //this.alphaBetaPVPath[this.currentMaxDepth - depth] = move;
                    //this.alphaBetaPVScore[this.currentMaxDepth - depth] = Double.POSITIVE_INFINITY;
                }
                
                GameNode next = new GameNode(state);
                
                // Recursive call with DFS implementation.
                double value = alphaBeta(next, depth - 1, alpha, beta, true);
                //System.out.println("MinPlayer Depth: " + depth + ", value: " + value);
                //System.out.println(move.toString());
                //System.out.println("MinPlayer value: " + value);
                // Try to minimize the value over all nodes.

                if(bestValue >= value) {
                    //System.out.println("Min Pop: " + this.alphaBetaPVPath.pop().toString());
                    //System.out.println("Min Push: " + move.toString());
                    //this.alphaBetaPVPath.push(move);
                    
                    /**
                    if(this.alphaBetaPVScore[this.currentMaxDepth - depth] >= value) {
                        System.out.println("Min Set: " + move.toString() + " " + this.alphaBetaPVScore[this.currentMaxDepth - depth] + " >= " + value);
                        this.alphaBetaPVPath[this.currentMaxDepth - depth] = move;
                        this.alphaBetaPVScore[this.currentMaxDepth - depth] = value;
                    }*/
                    //System.out.println(bestValue + " > " + value);
                    bestValue = value;
                    bestMove = move;
                    beta = value;
                } else {
                    //System.out.println("NOT " + bestValue + " > " + value);
                }
                
                // Undo move. (Important to do this before the cutoff.
                //System.out.println("UNDO " + depth + ":::" + move.toString());
                board.undoMove(move);
                //System.out.println("MIN UNDO MOVE");
                
                i++;
                
                // Cutoff. Current best value for the maximizing is better
                // than the best value for the minimizing player. Therefore the
                // maximizing player will never allow the minimizing player
                // to choose this move and so there is no reason in exploring
                // this branch.
                if(cutoffOn && alpha > beta) {
                    // Count the number of cutoffs.
                    this.cutoffs++;
                    break;
                }
            }
        }
        
        // Store the best move in the node.
        // TODO Actually only necessary at the initial call. Not useful
        // for lower depths.
        node.setBestMove(bestMove);
        
        // Store best move on stack.
        this.alphaBetaBestMoves.push(bestMove);
        System.out.println("BestMove at depth: " + depth + " -> " + bestMove.toString());
        
        //System.out.println(depth + " bestValue = " + bestValue);
        //System.out.println("Best move: " + bestMove.toString());
        return bestValue;
    }
    
    private void orderMoves(List<MoveAction> moves, MoveAction best) {
        if(moves.isEmpty()) {
            throw new IllegalArgumentException("Provided moves list is empty.");
        }
        
        if(best == null) {
            throw new IllegalArgumentException("orderMoves(): best == null");
        }
        
        int index = moves.lastIndexOf(best);
        if(index == -1) {
            System.out.println("Moves:");
            for(MoveAction move : moves) {
                System.out.println(move.toString());
            }
            throw new IllegalArgumentException("Provided bestMove:" + 
                    best.toString() + " is not contained in given moves list.");
        }
        
        // Swap best move with first element, such that it is processed first.
        MoveAction first = moves.get(0);
        moves.set(0, best);
        moves.set(index, first);
    }
    
    public static void main(String[] args) {
        new TreeSearch(null).test();
    }
    
    private void test() {
        System.out.println("Run Test");
        HeuristicEvaluation heuristic = new Attacker.AttackerHeuristic();
        setHeuristic(heuristic);
        
        GameState state = new GameState();
        SetupGenerator generator = new SetupGenerator();
        //GameBoard board = generator.generateShowcaseTwo();
        
        String setup = "r:3|r:4\n" +
                        "--- ---\n" + 
                        "   |   \n" +
                        "--- ---\n" + 
                        "b:4|b:3";
        GameBoard board = GameBoard.loadBoard(setup, 2, 3);
        System.out.println(board.transcript());
        state.setGameBoard(board);
        GameNode node = new GameNode(state);
        
        int initialDepth = 1;
        int range = 2; //2; //5; //9; //5; //1; //9; //6; //-1;
        //SearchResult result = iterativeDeepeningAlphaBeta(node, initialDepth, range, true);
        SearchResult result = IDAlphaBeta(node, initialDepth, range, true);
        
        // Print results.
        Stack<Integer> counts = result.getExploredNodes();
        Stack<Long> timings = result.getIterationTimes();
        //Stack<MoveAction> path = result.getPrincipalVariationPath();
        int N = counts.size();
        for(int i=0; i<N; i++) {
            String time = "( Finished in " + timings.pop() + " ms. )";
            System.out.println("NodeCount with Max Depth-" 
                    + (initialDepth + (N - (i + 1))) +
                    " -> " + counts.pop() +
                    " " + time);
        }
        /**
        System.out.println("Principal Variation Path:");
        int j = 0;
        for(MoveAction move : path) {
            System.out.println(j + ": " + move.toString());
            j++;
        }
        System.out.println();
        */
        MoveAction bestMove = result.getBestMove();
        System.out.println("Best Move: " + bestMove.toString());
    }
    
    
    
    public SearchResult IDAlphaBeta(GameNode node, int initialDepth, int range, boolean isMaxPlayer) {
        // Reset timeout.
        this.timeout = false;
        
        // Initial depth must be at least 1, if it is 0, you remain in the
        // current state, since no moves are applied and therefore a best move
        // will not be set.
        if(initialDepth < 1) {
            throw new IllegalArgumentException("Initial depth must be >= 1");
        }
        
        // Range can take the special value -1 or range >= 0
        if(range < -1) {
            throw new IllegalArgumentException("Range must be >= 0 "
                    + "or the special value -1 for an infinite range.");
        }
        
        // Initialize current depth.
        int depth = initialDepth;
        
        // Store the player that resides at the root, needed to give the correct
        // value for a terminal node, since its value must be from the
        // perspective of the root player.
        this.initialPlayer = isMaxPlayer;
        
        MoveAction bestMove = null;
        System.out.println("isMaxPlayer: " + isMaxPlayer);
        SearchResult result = new SearchResult();
        Stack<Integer> exploredNodes = new Stack<>();
        Stack<Long> iterationTimes = new Stack<>();
        //this.iterationBestMoves = new Stack<>();
        //this.alphaBetaBestMoves = new Stack<>();
        this.moveOrderOn = false;
        //this.alphaBetaPVPath = new Stack<>();
        
        //Stack<MoveAction> principalVariationPath = new Stack<>();
        //principalVariationPath.ensureCapacity(20);
        //principalVariationPath.setSize(100);
        
        // Apply iterative deepening.
        try {
            while(!this.timeout) {
                // Check if range has been exceeded.
                if(range != -1 && depth - initialDepth > range) {
                    System.out.println("Exceeded range.");
                    break;
                }
                
                // Reset best move.
                node.setBestMove(null);
                // Run alpha beta.
                System.out.println("Run IDAlphaBeta with max depth: " + depth);
                
                // Reset counters.
                //this.exploredNodes = 0;
                this.cutoffs = 0;
                this.nodeCount = 0;
                this.alphaBestMovesPops = 0;
                this.currentMaxDepth = depth;
                // Initialize the principal path array.
                //this.alphaBetaPVPath = new MoveAction[this.currentMaxDepth];
                //this.alphaBetaPVScore = new double[this.alphaBetaPVPath.length];
                // Initialize score array.
                /**
                for(int j=0; j<this.alphaBetaPVScore.length; j+=2) {
                    if(isMaxPlayer) {
                        this.alphaBetaPVScore[j] = Double.NEGATIVE_INFINITY;
                        if(j + 1 < this.alphaBetaPVScore.length) {
                            this.alphaBetaPVScore[j+1] = Double.POSITIVE_INFINITY;
                        }
                    } else {
                        this.alphaBetaPVScore[j] = Double.POSITIVE_INFINITY;
                        if(j + 1 < this.alphaBetaPVScore.length) {
                            this.alphaBetaPVScore[j+1] = Double.NEGATIVE_INFINITY;
                        }
                    }
                }
                
                principalVariationPath.clear();
                */
                double score;
                double alpha = Double.NEGATIVE_INFINITY; // Worst case maximizing player.
                double beta = Double.POSITIVE_INFINITY; // Worst case minimizing player.
                long start = System.currentTimeMillis();
                if(isMaxPlayer) {
                    score = alphaBetaMax(node, alpha, beta, depth);
                } else {
                    score = alphaBetaMin(node, alpha, beta, depth);
                }
                long end = System.currentTimeMillis();
                
                // Store the node count.
                exploredNodes.push(this.nodeCount);
                
                // Store the iteration computation time required at this depth.
                iterationTimes.push(end - start);
                
                // If a score of infinity or -infinity depending on the root player
                // indicates that the game is inevitably lost or won.
                // When this happens there is no point on continueing iterative
                // deepening.
                if(isMaxPlayer && score == Double.POSITIVE_INFINITY || 
                        !isMaxPlayer && score == Double.NEGATIVE_INFINITY) {
                    System.out.println("Inevitable Win!");
                    break;
                }
                
                // TODO in this case the best move for the current player has
                // not been set, since all moves are bad.
                if(isMaxPlayer && score == Double.NEGATIVE_INFINITY ||
                        !isMaxPlayer && score == Double.POSITIVE_INFINITY) {
                    System.out.println("Inevitable Loss!");
                    break;
                }
                
                // Update best move.
                bestMove = node.getBestMove();
                //this.iterationBestMoves.push(bestMove);
                // Check if bestMove is okay.
                // TODO remove this check if this function is stable.
                if(!bestMove.isOkay()) {
                    throw new RuntimeException("Corrupt MoveAction: " + 
                            bestMove.toString());
                }
                
                // Print statistical information.
                System.out.println("BestMove: " + bestMove.toString());
                System.out.println("Score: " + score);
                System.out.println("Cutoffs: " + this.cutoffs);                
                
                // Increment depth.
                depth++;
            }
        } catch(TimeoutException ex) {
            System.out.println("TIMED OUT");
        }
        
        // Attach results to SearchResult object.
        result.setBestMove(bestMove);
        result.setExploredNodes(exploredNodes);
        result.setIterationTimes(iterationTimes);
        
        return result;
    }
    
    private double alphaBetaMax(GameNode node, double alpha, double beta, int depth) throws TimeoutException {
        // Counts visited nodes.
        this.nodeCount++;
        
        // Stop if timeout is true.
        if(this.timeout) {
            throw new TimeoutException();
        }
        
        // State objects.
        GameState state = node.getState();
        GameBoard board = state.getGameBoard();        
        
        // Reached a terminal node.
        double e = endStateReached(board);
        if(e != -1) {
            return e;
        }        
        
        // Reached maximum depth.
        if(depth == 0) {
            // Return heuristic score for this state.
            return this.evaluation.score(state);
        }
        
        List<MoveAction> moves = board.getMoves(Team.RED);
        for(MoveAction move : moves) {
            // Apply move.
            board.applyMove(move);
            // Recursive call.
            //alpha = Math.max(alpha, alphaBetaMin(node, alpha, beta, depth - 1));
            double score = alphaBetaMin(node, alpha, beta, depth - 1);
            if(score > alpha) {
                alpha = score;
                // Store best move.
                node.setBestMove(move);
                node.setValue(alpha);
            }
            
            // Undo move.
            board.undoMove(move);
            
            // Cutoff.
            if(alpha >= beta) {
                return beta;
            }
        }
        
        return alpha;
    }
    
    private double alphaBetaMin(GameNode node, double alpha, double beta, int depth) throws TimeoutException {
        // Count visited nodes.
        this.nodeCount++;
        
        // Stop if timeout is true.
        if(this.timeout) {
            throw new TimeoutException();
        }
        
        // State objects.
        GameState state = node.getState();
        GameBoard board = state.getGameBoard();        
        
        // Reached a terminal  node.
        double e = endStateReached(board);
        if(e != -1) {
            return e;
        }
        
        // Reached maximum depth.
        if(depth == 0) {
            // Return heuristic score for this state.
            return this.evaluation.score(state);
        }
        
        List<MoveAction> moves = board.getMoves(Team.BLUE);
        for(MoveAction move : moves) {
            // Apply move.
            board.applyMove(move);
            // Recursive call.
            //beta = Math.min(beta, alphaBetaMax(node, alpha, beta, depth - 1));
            double score = alphaBetaMax(node, alpha, beta, depth - 1);
            if(score < beta) {
                beta = score;
                // Store the best move.
                node.setBestMove(move);
                node.setValue(beta);
            }
            // Undo move.
            board.undoMove(move);
            
            // Cutoff.
            if(beta <= alpha) {
                return alpha;
            }
        }
        
        return beta;
    }
    
    private double endStateReached(GameBoard board) {
        Team winner = board.isEndState();
        if(winner != null) {
            // initialPlayer equal to TRUE indicates that the root player was
            // the maximizing player.
            if(this.initialPlayer) {
                // Root player wins.
                if(winner == Team.RED) {
                    return Double.POSITIVE_INFINITY;
                } else { // Root player loses.
                    return Double.NEGATIVE_INFINITY;
                }
            } else {
                // Root player wins.
                if(winner == Team.BLUE) {
                    return Double.POSITIVE_INFINITY;
                } else { // Root player loses.
                    return Double.NEGATIVE_INFINITY;
                }
            }
        }
        
        return -1;
    }
}
