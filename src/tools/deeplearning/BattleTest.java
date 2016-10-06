/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.deeplearning;

import Game.GameBoard;
import Game.Team;
import tools.search.ai.AIBot;
import tools.search.ai.SetupGenerator;
import tools.search.ai.players.DefaultPlayer;
import tools.search.ai.players.ModeratePlayer;

/**
 *
 * @author s122041
 */
public class BattleTest {
    public static void main (String[] args) {
        new BattleTest().testHeuristic();
    }
    
    private void testHeuristic() {
        SetupGenerator generator = new SetupGenerator();
        GameBoard board = generator.generateModeratePlayerSetup();
        
        BattleEngine battleEngine = new BattleEngine();
        
        AIBot attacker = new DefaultPlayer(Team.RED);
        AIBot defender = new ModeratePlayer(Team.BLUE);
        
        long computationTime = 2000;
        int maxIterations = 150;
        
        BattleTranscript transcript = battleEngine.battle(board, attacker, defender, computationTime, maxIterations);
        transcript.print();
    }
}
