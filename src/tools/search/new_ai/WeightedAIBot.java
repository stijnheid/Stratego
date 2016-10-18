package tools.search.new_ai;

import tools.search.ai.AIBot;

/**
 *
 */
public interface WeightedAIBot extends AIBot {
    public int featureCount();
    public void setWeights(double[] weights);
}
