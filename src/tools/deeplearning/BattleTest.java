package tools.deeplearning;

import Game.GameBoard;
import Game.GameState;
import Game.Team;
import actions.MoveAction;
import tools.search.ai.AIBot;
import tools.search.ai.AlphaBetaSearch;
import tools.search.ai.GameNode;
import tools.search.ai.HeuristicEvaluation;
import tools.search.ai.SetupGenerator;
import tools.search.ai.players.Attacker;
import tools.search.ai.players.AttackerTwo;
import tools.search.ai.players.DefaultPlayer;
import tools.search.ai.players.ModerateAttacker;
import tools.search.ai.players.ModeratePlayer;
import tools.search.new_ai.DefenderOne;
import tools.search.new_ai.SparringAttacker;

/**
 *
 * @author s122041
 */
public class BattleTest {
    public static void main (String[] args) {
        //new BattleTest().testHeuristic();
        //new BattleTest().testSmallBoard();
        //new BattleTest().showCase();
        //new BattleTest().alphaBetaTest();
        //new BattleTest().testNew();
        new BattleTest().IDTest();
    }
    
    private void testHeuristic() {
        SetupGenerator generator = new SetupGenerator();
        GameBoard board = generator.generateModeratePlayerSetup();
        
        BattleEngine battleEngine = new BattleEngine();
        
        //AIBot attacker = new Attacker(Team.RED); //new DefaultPlayer(Team.RED);
        AIBot attacker = new DefaultPlayer(Team.RED);
        AIBot defender = new ModeratePlayer(Team.BLUE);
        
        long computationTime = 2000;
        int maxIterations = 30; //60; //150;
        
        BattleTranscript transcript = battleEngine.battle(board, attacker, defender, computationTime, maxIterations);
        transcript.print();
    }
    
    private void testSmallBoard() {
        SetupGenerator generator = new SetupGenerator();
        GameBoard smallBoard = generator.generateSmallBoard();
        
        BattleEngine battleEngine = new BattleEngine();
        
        // Behaves slightly different than the new attacker.
        // Why does miniMax currently visit more nodes or is that a distorted
        // count?
        AIBot attacker = new DefaultPlayer(Team.RED);
        AIBot defender = new DefaultPlayer(Team.BLUE);
        
        //AIBot attacker = new Attacker(Team.RED);
        //AIBot defender = new Attacker(Team.BLUE);
        
        long computationTime = 2000;
        int maxIterations = 10; //30;
        
        BattleTranscript transcript = battleEngine.battle(smallBoard, 
                attacker, defender, computationTime, maxIterations);
        transcript.print();
    }
    
    private void showCase() {
        SetupGenerator generator = new SetupGenerator();
        GameBoard board = generator.generateFourBySix();
        System.out.println("BoardState:\n" + board.transcript());
        
        BattleEngine engine = new BattleEngine();
        
        AIBot attacker = new ModerateAttacker(Team.RED);
        //AIBot attacker = new Attacker(Team.RED);
        //AIBot defender = new Attacker(Team.BLUE);
        AIBot defender = new ModeratePlayer(Team.BLUE);
        //AIBot defender = new Attacker(Team.BLUE);
        
        long computationTime = 2000;
        int maxIterations = 50; //80; //40; //20; //28; //40; //100; //40;
        
        BattleTranscript transcript = engine.battle(board, attacker, defender, computationTime, maxIterations);
        transcript.print();
    }
    
    private void alphaBetaTest() {
        SetupGenerator generator = new SetupGenerator();
        GameBoard board = generator.generateShowcase();
        
        AlphaBetaSearch search = new AlphaBetaSearch(null);
        HeuristicEvaluation evaluation = new Attacker.AttackerHeuristic();
        search.setHeuristic(evaluation);
        
        GameState state = new GameState();
        state.setGameBoard(board);
        
        System.out.println(board.transcript());
        
        GameNode node = new GameNode(state);
        MoveAction move = search.iterativeDeepeningAlphaBeta(node, 1, 12, true);
        System.out.println("Move: " + move.toString());
    }
    
    private void testNew() {
        System.out.println("Test New");
        SetupGenerator generator = new SetupGenerator();
        //GameBoard board = generator.generateFourBySix();
        //GameBoard board = generator.generateShowcaseTwo(); Nice Result.
        
        String setup = "r:3|r:4\n" +
                        "--- ---\n" +
                        "   |   \n" +
                        "--- ---\n" +
                        "b:4|b:3";
        
        GameBoard board = generator.generateShowcaseThree();
        //GameBoard board = GameBoard.loadBoard(setup, 2, 3);
        
        BattleEngine engine = new BattleEngine();
        
        AIBot attacker = new AttackerTwo(Team.RED);
        AIBot defender = new ModeratePlayer(Team.BLUE);
        
        long computationTime = 2000;
        int maxIterations = 36; //24; //20; //50;
        
        BattleTranscript transcript = engine.battle(board, attacker, defender, computationTime, maxIterations);
        transcript.print();        
    }
    
    private void IDTest() {
        /*
        String setup = "r:9|r:7|r:6|r:8\n" +
                        "--- --- --- ---\n" +
                        "r:4|r:5|r:S|r:2\n" +
                        "--- --- --- ---\n" +
                        "   |   |   |   \n" +
                        "--- --- --- ---\n" +
                        "   |   |   |   \n" +
                        "--- --- --- ---\n" +
                        "b:B|b:6|b:4|b:7\n" +
                        "--- --- --- ---\n" +
                        "b:F|b:B|b:9|b:5";
        
        String setup = "r:5|r:2|r:6|r:4\n" +
                        "--- --- --- ---\n" +
                        "r:S|r:8|r:7|r:9\n" +
                        "--- --- --- ---\n" +
                        "   |   |   |   \n" +
                        "--- --- --- ---\n" +
                        "   |   |   |   \n" +
                        "--- --- --- ---\n" +
                        "b:B|b:6|b:4|b:7\n" +
                        "--- --- --- ---\n" +
                        "b:F|b:B|b:9|b:5";
        
        String setup = "r:4|r:5|r:9|r:7\n" +
                            "--- --- --- ---\n" +
                            "r:6|r:S|r:8|r:2\n" +
                            "--- --- --- ---\n" +
                            "   |   |   |   \n" +
                            "--- --- --- ---\n" +
                            "   |   |   |   \n" +
                            "--- --- --- ---\n" +
                            "b:B|b:6|b:4|b:7\n" +
                            "--- --- --- ---\n" +
                            "b:F|b:B|b:9|b:5";
        
        
        String setup = "r:3|r:4\n" +
                        "--- ---\n" +
                        "   |   \n" +
                        "--- ---\n" +
                        "b:4|b:3";
        */
        
        String setup = "r:6|r:9|r:2|r:8\n" +
                        "--- --- --- ---\n" +
                        "r:S|r:7|r:4|r:5\n" +
                        "--- --- --- ---\n" +
                        "   |   |   |   \n" +
                        "--- --- --- ---\n" +
                        "   |   |   |   \n" +
                        "--- --- --- ---\n" +
                        "b:B|b:6|b:4|b:7\n" +
                        "--- --- --- ---\n" +
                        "b:F|b:B|b:9|b:5";
        GameBoard board = GameBoard.loadBoard(setup, 4, 6);
        
        BattleEngine engine = new BattleEngine();
        
        AIBot attacker = new SparringAttacker(Team.RED);
        AIBot defender = new DefenderOne(Team.BLUE);
        long time = 2000;
        int maxIterations = 25; //20;
        BattleTranscript report = engine.battle(board, attacker, defender, time, maxIterations);
        report.print();
    }
}
