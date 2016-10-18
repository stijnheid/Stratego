/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.search.ai;

/**
 *
 * @author s122041
 */
public interface WeightedEvaluation extends HeuristicEvaluation{
    public int featureCount();
    public void setWeights(double[] weights);
}
