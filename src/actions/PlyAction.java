/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package actions;

import Game.BoardPosition;
import Game.GamePiece;
import Game.Team;

/**
 *
 * @author s147724
 */
public class PlyAction extends Action {

    private final BoardPosition origin;
    private final BoardPosition destination;

    public PlyAction(Team team, 
            BoardPosition origin, 
            BoardPosition destination) {
        super(team);
        this.origin = origin;
        this.destination = destination;
    }

    public BoardPosition getOrigin() {
        return origin;
    }

    public BoardPosition getDestination() {
        return destination;
    }
    
}
