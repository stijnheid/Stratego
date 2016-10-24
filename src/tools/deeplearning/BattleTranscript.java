package tools.deeplearning;

import Game.BoardPosition;
import Game.GameBoard;
import Game.GamePiece;
import Game.InvalidPositionException;
import Game.Team;
import actions.MoveAction;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.search.Player;

/**
 * A battle can be described as an initial setup and a list of sequential
 * moves.
 */
public class BattleTranscript {
    
    private final GameBoard initialSetup;
    private final List<MoveAction> moves;
    // Indicates if the battle has ended.
    private boolean gameOver;
    // The winner of the battle.
    private Team winner;
    private long gameStart;
    private long gameDuration;
    private final Team attacker;
    private final Team defender;
    private int computationTime;
    private Player redPlayer;
    private Player bluePlayer;
    
    public BattleTranscript(GameBoard board, Team attacker, Team defender) {
        this.initialSetup = (GameBoard) board.clone();
        this.moves = new ArrayList<>();
        this.gameOver = false;
        this.winner = null;
        this.attacker = attacker;
        this.defender = defender;
    }
    
    public void addMove(MoveAction move) {
        moves.add((MoveAction) move.clone());
        //MoveAction clone = move.clone();
        // Want to keep map this move to a GamePiece on this original board.
    }
    
    public List<MoveAction> getMoves() {
        return this.moves;
    }
    
    public void print() {
        System.out.println("Initial Setup\n");
        System.out.println(initialSetup.transcript());
        
        // Should actually clone the board again.
        
        int moveNumber = 1;
        for(MoveAction move : moves) {
            initialSetup.applyMove(move);
            System.out.println("move#" + moveNumber + ": " + move.toString());
            System.out.println(initialSetup.transcript());
            moveNumber++;
        }
    }

    public Team getWinner() {
        return winner;
    }

    public void setWinner(Team winner) {
        this.winner = winner;
    }
    
    public boolean gameOver() {
        return this.gameOver;
    }
    
    public void startGame() {
        if(this.gameOver) {
            throw new IllegalStateException("Cannot start a game that has already ended.");
        }
        
        this.gameStart = System.currentTimeMillis();
    }
    
    public void endGame() {
        this.gameOver = true;
        this.gameDuration = System.currentTimeMillis() - this.gameStart;
    }

    public long getGameDuration() {
        return gameDuration;
    }


    public int getComputationTime() {
        return computationTime;
    }

    public void setComputationTime(int computationTime) {
        this.computationTime = computationTime;
    }

    public Player getRedPlayer() {
        return redPlayer;
    }

    public void setRedPlayer(Player redPlayer) {
        this.redPlayer = redPlayer;
    }

    public Player getBluePlayer() {
        return bluePlayer;
    }

    public void setBluePlayer(Player bluePlayer) {
        this.bluePlayer = bluePlayer;
    }
    
    public void save(String filename) {
        // Store the played game.
        // Denote board dimensions and #moves
        StringBuilder builder = new StringBuilder();
        builder.append(this.initialSetup.getWidth());
        builder.append(" ");
        builder.append(this.initialSetup.getHeight());
        builder.append(" ");
        // Store the initial setup.
        for(int row=0; row<this.initialSetup.getHeight(); row++) {
            for(int column=0; column<this.initialSetup.getWidth(); column++) {
                try {
                    GamePiece piece = this.initialSetup.getPiece(new BoardPosition(column, row));
                    if(piece != null) {
                        builder.append(piece.getRank().getPieceSymbol() + ":" + column + "," + row);
                        builder.append(" ");
                    }
                } catch (InvalidPositionException ex) {
                    Logger.getLogger(BattleTranscript.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        // # of moves
        builder.append(this.moves.size());
        builder.append(" ");
        // Store the moves.
    }
}
