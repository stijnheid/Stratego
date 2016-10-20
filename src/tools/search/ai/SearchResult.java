package tools.search.ai;

import actions.MoveAction;

/**
 *
 */
public class SearchResult {
    
    private MoveAction move;

    public SearchResult() {
    }

    public MoveAction getBestMove() {
        return this.move;
    }

    public void setBestMove(MoveAction move) {
        this.move = move;
    }
}
