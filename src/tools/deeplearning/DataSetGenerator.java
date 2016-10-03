package tools.deeplearning;

import Game.GameBoard;
import Game.Team;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates random board setups against a given defensive setup and runs the
 * battle in the BattleEngine and stores the result, the result is composed
 * of the initial setup and the winner of the battle.
 * 
 * TODO can extend this by also encoding the moves.
 */
public class DataSetGenerator {
    
    // Use the observor pattern.
    List<DeepLearner> learners = new ArrayList<>();
    private final BattleEngine battleEngine;
    
    public void addLearner(DeepLearner learner) {
        this.learners.add(learner);
    }
    
    public DataSetGenerator() {
        this.battleEngine = new BattleEngine();
    }
    
    private void generate() {
        BattleTranscript result = this.battleEngine.battle();
        
        // Notify all listeners.
        for(DeepLearner learner : this.learners) {
            learner.learn(null, Team.RED, true);
        }
    }
    
    private GameBoard generateSetup() {
        return null;
    }
}
