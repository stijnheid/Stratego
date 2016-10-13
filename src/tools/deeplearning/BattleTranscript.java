package tools.deeplearning;

import Game.GameBoard;
import Game.Team;
import actions.MoveAction;
import java.util.ArrayList;
import java.util.List;

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
    
    public void save(String filename) {
        
    }
}
