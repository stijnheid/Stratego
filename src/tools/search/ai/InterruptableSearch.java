package tools.search.ai;

/**
 *
 */
public abstract class InterruptableSearch {
    
    // Flag that indicates the algorithm must be interrupted.    
    protected boolean timeout;
    
    // Evaluation function to be used by search algorithm to evaluate board
    // state.    
    protected HeuristicEvaluation evaluation;
    
    public InterruptableSearch() {
        this.timeout = false;
        this.evaluation = null;
    }
    
    public abstract SearchResult search(GameNode node, int initialDepth, 
            int range, boolean isMaxPlayer, boolean moveOrdering);
    
    public void timeout() {
        this.timeout = true;
    }
    
    public void setHeuristic(HeuristicEvaluation evaluation) {
        this.evaluation = evaluation;
    }
}
