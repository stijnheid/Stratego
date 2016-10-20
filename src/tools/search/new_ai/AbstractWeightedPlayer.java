package tools.search.new_ai;

import Game.GameState;
import Game.Team;
import actions.MoveAction;
import tools.search.ai.InterruptableSearch;
import tools.search.ai.TreeSearch;
import tools.search.ai.WeightedEvaluation;

/**
 *
 */
public abstract class AbstractWeightedPlayer implements WeightedAIBot {

    protected boolean active;
    protected Team team;
    protected InterruptableSearch search;
    protected WeightedEvaluation evaluation;

    public AbstractWeightedPlayer(Team team) {
        this.team = team;
        this.search = new TreeSearch(null);
    }

    @Override
    public abstract MoveAction nextMove(GameState state);
    
    public abstract void setRange(int range);
    
    @Override
    public final int featureCount() {
        return this.evaluation.featureCount();
    }

    @Override
    public final void setWeights(double[] weights) {
        this.evaluation.setWeights(weights);
    }
    
    @Override
    public void stop() {
        this.active = false;
        this.search.timeout();
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public Team getTeam() {
        return this.team;
    }    
}
