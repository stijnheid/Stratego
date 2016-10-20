package tools.search.ai;

import Game.GameBoard;
import Game.GameState;
import Game.Team;
import actions.MoveAction;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import tools.search.ai.players.Attacker;

/**
 *
 */
public class TreeSearch extends InterruptableSearch {
    
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
    private List<MoveAction> currentPVPath;
    private boolean[] moveOrderState;
    
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

    @Override
    public tools.search.ai.SearchResult search(
            GameNode node, int initialDepth, int range, boolean isMaxPlayer, boolean moveOrdering) {
        return IDAlphaBeta(node, initialDepth, range, isMaxPlayer, moveOrdering);
    }
    
    public class MySearchResult extends SearchResult {
        
        private Stack<Integer> exploredNodes;
        private MoveAction bestMove;
        private Stack<Long> iterationTimes;
        private Stack<List<MoveAction>> principalVariationPaths;
        private Stack<Integer> cutoffs;
        
        public MySearchResult() {
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

        public Stack<List<MoveAction>> getPrincipalVariationPaths() {
            return principalVariationPaths;
        }

        public void setPrincipalVariationPaths(Stack<List<MoveAction>> principalVariationPaths) {
            this.principalVariationPaths = principalVariationPaths;
        }

        public Stack<Integer> getCutoffs() {
            return cutoffs;
        }

        public void setCutoffs(Stack<Integer> cutoffs) {
            this.cutoffs = cutoffs;
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
        MySearchResult result = new MySearchResult();
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
        //result.setPrincipalVariationPath(principalVariationPath);
        
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
        new TreeSearch(null).performanceTest();
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
        int range = 9; //3; //1; //9; //2; //5; //9; //5; //1; //9; //6; //-1;
        //SearchResult result = iterativeDeepeningAlphaBeta(node, initialDepth, range, true);
        long start = System.currentTimeMillis();
        MySearchResult result = IDAlphaBeta(node, initialDepth, range, true, true);
        long end = System.currentTimeMillis();
        System.out.println("IDAlphaBeta ended in " +
                (end - start) + " ms. with range [" + initialDepth + ", " + (initialDepth + range) + "]");
        
        // Print results.
        Stack<Integer> counts = result.getExploredNodes();
        Stack<Long> timings = result.getIterationTimes();
        Stack<Integer> cutoffs = result.getCutoffs();
        //Stack<MoveAction> path = result.getPrincipalVariationPath();
        int N = counts.size();
        for(int i=0; i<N; i++) {
            String time = "( Finished in " + timings.pop() + " ms. )";
            System.out.println("Max Depth-" 
                    + (initialDepth + (N - (i + 1))) +
                    " -> #VisitedNodes=" + counts.pop() +
                    ", Cutoffs=" + cutoffs.pop() +
                    " " + time);
        }
        
        Stack<List<MoveAction>> pvPaths = result.getPrincipalVariationPaths();
        List<MoveAction> path = pvPaths.pop();
        System.out.println("Principal Variation Path:");
        int i = 0;
        for(MoveAction move : path) {
            System.out.println("#" + i + " " + move);
            i++;
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
    
    public MySearchResult IDAlphaBeta(GameNode node, int initialDepth, int range, boolean isMaxPlayer, boolean useMoveOrdering) {
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
        MySearchResult result = new MySearchResult();
        Stack<Integer> exploredNodes = new Stack<>();
        Stack<Long> iterationTimes = new Stack<>();
        Stack<Integer> iterationCutoffs = new Stack<>();
        Stack<List<MoveAction>> iterationPVPaths = new Stack<>();
        //this.iterationBestMoves = new Stack<>();
        //this.alphaBetaBestMoves = new Stack<>();
        // Deactivate move ordering.
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
                
                // Activate move ordering if a principal variation path of at least
                // length 3 is known.
                // Must be at least 2, since that's the minimal depth from
                // which a previous path is defined.
                if(useMoveOrdering && depth > 1) {
                    this.moveOrderOn = true;
                    // All booleans initialize to false as desired.
                    this.moveOrderState = new boolean[this.currentPVPath.size()];                    
                }
                
                System.out.println("Move Ordering: " + this.moveOrderOn);
                
                double score;
                double alpha = Double.NEGATIVE_INFINITY; // Worst case maximizing player.
                double beta = Double.POSITIVE_INFINITY; // Worst case minimizing player.
                LinkedList<MoveAction> path = new LinkedList<>();
                long start = System.currentTimeMillis();
                if(isMaxPlayer) {
                    System.out.println("alphaBetaMax");
                    score = alphaBetaMax(node, alpha, beta, depth, path);
                } else {
                    System.out.println("alphaBetaMin");
                    score = alphaBetaMin(node, alpha, beta, depth, path);
                }
                long end = System.currentTimeMillis();
    
                /**
                // Print PV Path.
                System.out.println("Path Size: " + path.size());
                int z = 0;
                for(MoveAction m : path) {
                    System.out.println(z + ": " + m.toString());
                    z++;
                }*/
                
                this.currentPVPath = path;
                
                // Store the node count.
                exploredNodes.push(this.nodeCount);
                
                // Store the iteration computation time required at this depth.
                iterationTimes.push(end - start);
                
                // Store the cutoffs.
                iterationCutoffs.push(this.cutoffs);
                
                // Store the Principal Variation Path.
                iterationPVPaths.push(path);
                
                // Update best move.
                //System.out.println("Move Score: " + score);
                // Fixed bug where bestMove was previously null if all
                // possible moves are infinitely bad.
                bestMove = node.getBestMove();
                if(bestMove == null) {
                    throw new RuntimeException("No best move.");
                }
                
                //this.iterationBestMoves.push(bestMove);
                // Check if bestMove is okay.
                // TODO remove this check if this function is stable.
                if(!bestMove.isOkay()) {
                    throw new RuntimeException("Corrupt MoveAction: " + 
                            bestMove.toString());
                }
                
                if(isMaxPlayer && bestMove.getTeam() != Team.RED) {
                    throw new RuntimeException("Returned move for wrong team: " + bestMove);
                } else if(!isMaxPlayer && bestMove.getTeam() != Team.BLUE) {
                    throw new RuntimeException("Returned move for wrong team: " + bestMove);
                }                
                
                // If a score of infinity or -infinity depending on the root player
                // indicates that the game is inevitably lost or won.
                // When this happens there is no point on continueing iterative
                // deepening.
                if(isMaxPlayer && score == Double.POSITIVE_INFINITY || 
                        !isMaxPlayer && score == Double.NEGATIVE_INFINITY) {
                    System.out.println("Inevitable Win! " + isMaxPlayer);
                    break;
                }
                
                // TODO in this case the best move for the current player has
                // not been set, since all moves are bad.
                if(isMaxPlayer && score == Double.NEGATIVE_INFINITY ||
                        !isMaxPlayer && score == Double.POSITIVE_INFINITY) {
                    System.out.println("Inevitable Loss!" + isMaxPlayer);
                    break;
                }
                
                // Print statistical information.
                long begin = System.currentTimeMillis();
                System.out.println("BestMove: " + bestMove.toString());
                System.out.println("Score: " + score);
                System.out.println("Cutoffs: " + this.cutoffs);
                System.out.println("#VisitedNodes: " + this.nodeCount);
                System.out.println("Computation Time: " + (end - start) + " ms.");
                long termination = System.currentTimeMillis();
                System.out.println("Message output took " + (termination - begin) + " ms.");
                System.out.println();
                
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
        result.setCutoffs(iterationCutoffs);
        result.setPrincipalVariationPaths(iterationPVPaths);
        
        return result;
    }
    
    private double alphaBetaMax(GameNode node, double alpha, double beta, int depth, LinkedList<MoveAction> pathToLeaf) throws TimeoutException {
        //System.out.println("AlphaBetaMax alpha=" + alpha + ", beta=" + beta + ", depth=" + depth);
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
            //double score = this.evaluation.score(state);
            //System.out.println("Leaf Score: " + score);
            //return score;
            return this.evaluation.score(state);
        }
        
        LinkedList<MoveAction> bestPath = new LinkedList<>();
        List<MoveAction> moves = board.getMoves(Team.RED);
        if(this.moveOrderOn) {
            orderMoves(moves, depth, Team.RED);
        }
        //System.out.println("MAX depth=" + depth + ", First move=" + moves.get(0));
        for(MoveAction move : moves) {
            //System.out.println(depth + " Max Applied: " + move);
            // Apply move.
            board.applyMove(move);
            // Recursive call.
            LinkedList<MoveAction> path = new LinkedList<>();
            //alpha = Math.max(alpha, alphaBetaMin(node, alpha, beta, depth - 1));
            GameNode next = new GameNode(state);
            double score = alphaBetaMin(next, alpha, beta, depth - 1, path);
            if(score > alpha) { // || node.getBestMove() == null) {
                //System.out.println(depth + " Max Better Score: " + score + " > " + alpha);
                path.addFirst(move);
                bestPath = path;
                alpha = score;
                // Store best move.
                if(move.getTeam() != Team.RED) {
                    throw new RuntimeException("MOVE != RED");
                }
                // Only store best move if in root, else by re-using the
                // node object lower level sets and cutoffs can set the bestMove
                // as last. Which leads to state corruption.                
                if(this.currentMaxDepth == depth) {
                    node.setBestMove(move);
                    node.setValue(alpha);
                }
            }
            
            // Undo move.
            board.undoMove(move);
            
            // Cutoff.
            if(alpha >= beta) {
                this.cutoffs++;
                pathToLeaf.addAll(bestPath);
                return beta;
            }
        }
        
        pathToLeaf.addAll(bestPath);
        return alpha;
    }
    
    private double alphaBetaMin(GameNode node, double alpha, double beta, int depth, LinkedList<MoveAction> pathToLeaf) throws TimeoutException {
        //System.out.println("AlphaBetaMin alpha=" + alpha + ", beta=" + beta + ", depth=" + depth);
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
            //double score = this.evaluation.score(state);
            //System.out.println("Leaf Score: " + score);
            //return score;
            return this.evaluation.score(state);
        }
        
        LinkedList<MoveAction> bestPath = new LinkedList<>();
        List<MoveAction> moves = board.getMoves(Team.BLUE);
        if(this.moveOrderOn) {
            orderMoves(moves, depth, Team.BLUE);
        }
        //System.out.println("MIN depth=" + depth + ", First move=" + moves.get(0));
        for(MoveAction move : moves) {
            //System.out.println(depth + " Min Applied: " + move);
            // Apply move.
            board.applyMove(move);
            // Recursive call.
            LinkedList<MoveAction> path = new LinkedList<>();
            //beta = Math.min(beta, alphaBetaMax(node, alpha, beta, depth - 1));
            //GameNode next = new GameNode(state);
            // TODO dangerous to re-use the node object.
            GameNode next = new GameNode(state);
            double score = alphaBetaMax(next, alpha, beta, depth - 1, path);
            // node.getBestMove() will only occur for the first move.
            // It is needed to always set a move, since if all moves are
            // infinitely as bad, then no move will be set.
            if(score < beta) {// || node.getBestMove() == null) {
                //System.out.println(depth + " Min Better Score: " + score + " < " + beta);
                path.addFirst(move);
                bestPath = path;
                beta = score;
                // Store the best move.
                if(move.getTeam() != Team.BLUE) {
                    throw new RuntimeException("MOVE != BLUE");
                }
                // Only store best move if in root, else by re-using the
                // node object lower level sets and cutoffs can set the bestMove
                // as last. Which leads to state corruption.
                if(this.currentMaxDepth == depth) {
                    node.setBestMove(move);
                    node.setValue(beta);
                }
            }
            // Undo move.
            board.undoMove(move);
            
            // Cutoff.
            if(beta <= alpha) {
                this.cutoffs++;
                pathToLeaf.addAll(bestPath);
                return alpha;
            }
        }
        
        pathToLeaf.addAll(bestPath);
        
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
    
    private void orderMoves(List<MoveAction> moves, int depth, Team team) {
        if(moves.isEmpty()) {
            throw new IllegalArgumentException("Provided moves list is empty.");
        }
        
        // Can always order the moves such that the attacks are processed first.
        /**
        Collections.sort(moves, new Comparator<MoveAction>() {
            @Override
            public int compare(MoveAction move, MoveAction otherMove) {
                if(move.isApplied() && otherMove.isApplied()) {
                    // Favor an attack with a highly ranked piece.
                    int attackVal = (move.getPiece().getRank().ordinal() - move.getEnemy().getRank().ordinal());
                    int otherVal = (otherMove.getPiece().getRank().ordinal() - move.getEnemy().getRank().ordinal());
                    if(attackVal >= otherVal) {
                        return 1;
                    } else {
                        return -1;
                    }
                    
                    // Can also incorporate the strategy to only use attacks
                    // where you kill the opponent with a slightly higher piece.
                } else if(true) {
                    
                }
            }
        });*/
        
        //System.out.println("orderMoves() #moves=" + moves.size() + ", depth=" + depth + ", #pvMoves=" + this.currentPVPath.size());

        // Order the move of the most recent principal variation path as the
        // first move in the list, to enhance pruning.
        int actualDepth = ((this.currentPVPath.size() + 1) - depth);
        
        // Reached the next depth that is of course not included in the
        // pv path of the previous iteration, so nothing to order, so stop.
        if(actualDepth == this.currentPVPath.size()) {
            return;
        }
        
        // Move ordering only needs to happen for the first time the algorithm
        // proceeds to a lower depth.
        if(this.moveOrderState[actualDepth]) {
            return;
        }
        this.moveOrderState[actualDepth] = true;
        
        // Get best move at this depth in the previous iteration.
        // TODO not the most efficient, since the loop up in a LinkedList takes
        // O(n) instead of O(1) constant time.
        MoveAction best = this.currentPVPath.get(actualDepth);
        
        if(best.getTeam() != team) {
            throw new RuntimeException("Team mismatch.");
        }
        
        // Can use the lastIndexOf because the list does not contain duplicates
        // so basically want indexOf(element).
        int index = moves.lastIndexOf(best);
        if(index == -1) {
            System.out.println("Moves:");
            for(MoveAction move : moves) {
                System.out.println(move.toString());
            }
            System.out.println("orderMoves() #moves=" + moves.size() + ", depth=" + depth + ", #pvMoves=" + this.currentPVPath.size());
            throw new IllegalArgumentException("Provided bestMove:" + 
                    best.toString() + " is not contained in given moves list.");
        }
        
        // Swap best move with first element, such that it is processed first.
        MoveAction first = moves.get(0);
        // Use the mathcing element of the new list just to be sure that
        // no object reference problems will occur.
        // TODO also inefficient if implemented with a LinkedList.
        moves.set(0, moves.get(index));
        moves.set(index, first);
    }
    
    private void performanceTest() {
        System.out.println("Run Performance Test");
        HeuristicEvaluation heuristic = new Attacker.AttackerHeuristic();
        //SparringAttacker attacker = new SparringAttacker(Team.RED);
        //WeightedEvaluation heuristic = attacker.new SparringAttackerHeuristic();
        
        //AlphaBetaSearch search = new AlphaBetaSearch(heuristic);
        
        setHeuristic(heuristic);
        
        GameState state = new GameState();
        SetupGenerator generator = new SetupGenerator();
        //GameBoard board = generator.generateFourBySix();
        GameBoard board = generator.generateWholeSetup();
        
        String setup = "r:3|r:4|r:3|r:4\n" +
                        "--- --- --- ---\n" + 
                        "r:4|r:8|r:S|r:4\n" +
                        "--- --- --- ---\n" +
                        "   |   |   |   \n" +
                        "--- --- --- ---\n" +
                        "   |   |   |   \n" +
                        "--- --- --- ---\n" +
                        "b:4|b:9|b:3|b:4\n" +
                        "--- --- --- ---\n" + 
                        "b:4|b:3|b:4|b:3";
        //GameBoard board = GameBoard.loadBoard(setup, 4, 6);
        System.out.println(board.transcript());
        state.setGameBoard(board);
        GameNode node = new GameNode(state);
        
        boolean moveOrdering = !false;
        int initialDepth = 1;
        int range = 7; //8; //9; //10; //9; //9; //3; //1; //9; //2; //5; //9; //5; //1; //9; //6; //-1;
        //SearchResult result = iterativeDeepeningAlphaBeta(node, initialDepth, range, true);
        MySearchResult result = IDAlphaBeta(node, initialDepth, range, true, moveOrdering);
        MoveAction m = result.getBestMove();
        board.applyMove(m);
        System.out.println("Board:\n" + board.transcript());
        long start = System.currentTimeMillis();
        result = IDAlphaBeta(node, initialDepth, range, true, moveOrdering);
        //search.iterativeDeepeningAlphaBeta(node, initialDepth, range, true);
        long end = System.currentTimeMillis();
        System.out.println("IDAlphaBeta ended in " +
                (end - start) + " ms. with range [" + initialDepth + ", " + (initialDepth + range) + "]");

        // Print results.
        Stack<Integer> counts = result.getExploredNodes();
        Stack<Long> timings = result.getIterationTimes();
        Stack<Integer> cutoffsStack = result.getCutoffs();
        //Stack<MoveAction> path = result.getPrincipalVariationPath();
        int N = counts.size();
        for(int i=0; i<N; i++) {
            String time = "( Finished in " + timings.pop() + " ms. )";
            System.out.println("Max Depth-" 
                    + (initialDepth + (N - (i + 1))) +
                    " -> #VisitedNodes=" + counts.pop() +
                    ", Cutoffs=" + cutoffsStack.pop() +
                    " " + time);
        }
        
        Stack<List<MoveAction>> pvPaths = result.getPrincipalVariationPaths();
        List<MoveAction> path = pvPaths.pop();
        System.out.println("Principal Variation Path:");
        int i = 0;
        for(MoveAction move : path) {
            System.out.println("#" + i + " " + move);
            i++;
        }
        
        MoveAction bestMove = result.getBestMove();
        System.out.println("Best Move: " + bestMove.toString());
    }
}
