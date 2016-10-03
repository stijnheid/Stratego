package tools.search;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.GameState;
import Game.GlobalSettings;
import Game.InvalidPositionException;
import Game.Pieces;
import Game.Team;
import Logic.Simulation;
import actions.MoveAction;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import tools.deeplearning.BattleEngine;
import tools.deeplearning.BattleTranscript;
import tools.search.ai.AIBot;
import tools.search.ai.SetupGenerator;
import tools.search.ai.players.DefaultPlayer;

/**
 *
 */
public class SearchToolPanel extends javax.swing.JPanel {

    //private SearchToolController controller;
    private UserInputController controller;
    
    /**
     * Creates new form SearchToolPanel
     */
    public SearchToolPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        startGameButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        moveCountLabel = new javax.swing.JLabel();
        turnLabel = new javax.swing.JLabel();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8"
            }
        ));
        jTable1.setGridColor(new java.awt.Color(0, 0, 0));
        jScrollPane1.setViewportView(jTable1);

        startGameButton.setText("Start");
        startGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startGameButtonActionPerformed(evt);
            }
        });

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        moveCountLabel.setText("MoveCount:");

        turnLabel.setText("Turn:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(startGameButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetButton)
                .addGap(18, 18, 18)
                .addComponent(moveCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(turnLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startGameButton)
                    .addComponent(resetButton)
                    .addComponent(moveCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(turnLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void startGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startGameButtonActionPerformed
        System.out.println("Start Game");
        // Check if the player's setup is valid.
        
        // Create a simulation object and run the battle engine with two AI bots.
        GameState state = new GameState();
        state.setRunning(true);
        
        // Setup the Table render.
        System.out.println("Set renderer");
        GameBoardCellRenderer renderer = new GameBoardCellRenderer(state, true);
        this.jTable1.setDefaultRenderer(Object.class, renderer);        
        
        // Create a board setup.
        //GameBoard board = new GameBoard(GlobalSettings.WIDTH, 
        //        GlobalSettings.HEIGHT, Team.RED, Team.RED);
        GameBoard board;
        SetupGenerator generator = new SetupGenerator();
        //board = generator.generateSetup();
        board = generator.mirroredSetup();
        //board = generator.smallSetup();
        
        // Set the JTable to the right dimensions.
        this.jTable1.setModel(new DefaultTableModel(board.getHeight(), 
                board.getWidth()));
        
        // Attach board to game state.
        state.setGameBoard(board);
        
        Simulation simulation = new Simulation(state, this.jTable1);
        BattleEngine battleEngine = new BattleEngine();
        
        AIBot attacker = new DefaultPlayer(Team.RED);
        AIBot defender = new DefaultPlayer(Team.BLUE);
        long computationTime = 2000;
        System.out.println("Invoke Battle Engine");
        BattleTranscript transcript = battleEngine.battle(state, 
                attacker, defender, computationTime, simulation);
        System.out.println("GAME ENDED");
        
        //flagReachabilityTest();
        //setup();
    }//GEN-LAST:event_startGameButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        System.out.println("Reset");
        // Stops the current game and resets the board.
        
    }//GEN-LAST:event_resetButtonActionPerformed

    private void runBattle() {
        
    }
    
    private void setup() {
        // Puts a custom renderer for the JTable.
        GameState state = new GameState();
        state.setRunning(true);
        
        // Table Model not anymore needed?
        //GameStateAdapter model = new GameStateAdapter(state);
        //this.jTable1.setModel(model);
        
        GameBoardCellRenderer renderer = new GameBoardCellRenderer(state, true);
        this.jTable1.setDefaultRenderer(Object.class, renderer);
        //this.controller = new SearchToolController(state);
        //this.controller.initialize();
        //this.jTable1.addMouseListener(this.controller);
        
        // Initialize the Simulation and setup controllers.
        Simulation simulation = new Simulation(state);
        this.controller = new UserInputController(state, simulation);
        // Make the table listen for mouse click input.
        this.jTable1.addMouseListener(this.controller);
        
        // Dummy setup.
        try {
            // Fill game state with data.
            GameBoard board = new GameBoard(GlobalSettings.WIDTH, 
                    GlobalSettings.HEIGHT, Team.RED, Team.BLUE);
            // Team RED
            board.setPiece(new BoardPosition(1, 0), new GamePiece(Pieces.BOMB, Team.RED));
            board.setPiece(new BoardPosition(2, 0), new GamePiece(Pieces.FLAG, Team.RED));
            GamePiece marshall = new GamePiece(Pieces.MARSHALL, Team.RED);
            //marshall.toggleHighlight();
            board.setPiece(new BoardPosition(5, 0), marshall);
            board.setPiece(new BoardPosition(2, 1), new GamePiece(Pieces.BOMB, Team.RED));
            board.setPiece(new BoardPosition(3, 0), new GamePiece(Pieces.BOMB, Team.RED));
            board.setPiece(new BoardPosition(7, 0), new GamePiece(Pieces.MINER, Team.RED));
            board.setPiece(new BoardPosition(3, 1), new GamePiece(Pieces.CAPTAIN, Team.RED));
            
            // Team BLUE
            board.setPiece(new BoardPosition(6, 5), new GamePiece(Pieces.BOMB, Team.BLUE));
            board.setPiece(new BoardPosition(5, 5), new GamePiece(Pieces.FLAG, Team.BLUE));
            board.setPiece(new BoardPosition(2, 5), new GamePiece(Pieces.GENERAL, Team.BLUE));
            board.setPiece(new BoardPosition(5, 4), new GamePiece(Pieces.BOMB, Team.BLUE));
            board.setPiece(new BoardPosition(4, 5), new GamePiece(Pieces.BOMB, Team.BLUE));
            board.setPiece(new BoardPosition(1, 5), new GamePiece(Pieces.MINER, Team.BLUE));
            board.setPiece(new BoardPosition(0, 5), new GamePiece(Pieces.SPY, Team.BLUE));
            
            state.setGameBoard(board);
        } catch (InvalidPositionException ex) {
            Logger.getLogger(SearchToolPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void flagReachabilityTest() {
        GameBoard board = new GameBoard(GlobalSettings.WIDTH, 
                GlobalSettings.HEIGHT, Team.RED, Team.BLUE);
        try {
            board.setPiece(new BoardPosition(0, 4), new GamePiece(Pieces.BOMB, Team.BLUE));
            //board.setPiece(new BoardPosition(1, 5), new GamePiece(Pieces.BOMB, Team.BLUE));
            board.setPiece(new BoardPosition(0, 5), new GamePiece(Pieces.FLAG, Team.BLUE));
            //board.setPiece(new BoardPosition(0, 5), new GamePiece(Pieces.BOMB, Team.BLUE));
            //board.setPiece(new BoardPosition(0, 5), new GamePiece(Pieces.BOMB, Team.BLUE));
            boolean reachable = board.isFlagUnreachable();
            
            System.out.println("Flag Unreachable: " + reachable);
        } catch (InvalidPositionException ex) {
            Logger.getLogger(SearchToolPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void endStateTest() {
        
    }
    
    private class GameTask extends SwingWorker<Void, MoveAction> {
        
        @Override
        protected Void doInBackground() throws Exception {
            return null;
        }

        @Override
        protected void done() {
            
        }

        @Override
        protected void process(List<MoveAction> chunks) {
            
        }

    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel moveCountLabel;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton startGameButton;
    private javax.swing.JLabel turnLabel;
    // End of variables declaration//GEN-END:variables

}

