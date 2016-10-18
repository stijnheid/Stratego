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
import tools.search.ai.players.ModeratePlayerTwo;

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
        new BattleTest().testNew();
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
}
