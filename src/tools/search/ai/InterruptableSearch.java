package tools.search.ai;

import actions.MoveAction;

/**
 *
 */
public abstract class InterruptableSearch {
    
    protected boolean timeout;
    
    public InterruptableSearch() {
        this.timeout = false;
    }
    
    public abstract MoveAction search(GameNode node, int initialDepth, int range, boolean isMaxPlayer);
    
    public void timeout() {
        this.timeout = true;
    }
}
