package tools.search.new_ai;

import Game.GameState;
import java.util.List;
import tools.search.ai.WeightedEvaluation;
import tools.search.ai.WeightedHeuristicTerm;

/**
 *
 */
public class TermedHeuristic implements WeightedEvaluation {
    private final List<WeightedHeuristicTerm> terms;

    public TermedHeuristic(List<WeightedHeuristicTerm> terms) {
        this.terms = terms;
    }

    @Override
    public int featureCount() {
        return this.terms.size();
    }

    @Override
    public void setWeights(double[] weights) {
        if(terms.size() != weights.length) {
            throw new IllegalArgumentException(
                    "length of weights array does not match #features: " + 
                            weights.length + " != " + featureCount());
        }

        // Assign weights to heuristic terms.
        int index = 0;
        for(WeightedHeuristicTerm term : terms) {
            term.setWeight(weights[index++]);
        }
    }

    @Override
    public double score(GameState state) {
        double score = 0;
        for(WeightedHeuristicTerm term : terms) {
            score+= term.score(state);
        }
        return score;
    }
}
