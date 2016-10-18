package tools.search.ai;

import Game.GameState;

/**
 * Represents a term in a linear heuristic evaluation function. A term can be
 * assigned a weight.
 */
public abstract class WeightedHeuristicTerm implements HeuristicEvaluation {
    // Default is full weight.
    private double weight = 1.0;
    
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public double getWeight() {
        return this.weight;
    } 

    @Override
    public double score(GameState state) {
        return (this.weight * computeScore(state));
    }
    
    public abstract double computeScore(GameState state);
}
