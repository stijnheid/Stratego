package Game;

import actions.MoveAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Gameboard represents the current board state by keeping track of pieces
 * and their positions. The game board keeps track of the width and height of 
 * board, which team is assigned the attacker and defender and the number of
 * moves the attacker has made.
 * Furthermore it contains many helper methods that are
 * useful to the AI algorithms to query the board state. Like obtaining the
 * possible moves for a particular piece. Obtaining all the pieces of a
 * particular team. Applying and undoing moves.
 *
 */
public class GameBoard {
    
    private final int width;
    private final int height;
    private final GamePiece[][] board;
    // Can also keep a list of pieces rather than a multidimensional array.
    
    private final Team attacker;
    private final Team defender;
    
    private int offensiveMoves;
    private final int offensiveMovesLimit = GlobalSettings.MAXIMUM_MOVES;
    
    private Team currentTurn;
    
    // Keep track of water cells.
    List<BoardPosition> waterCells;
    
    public GameBoard(int w, int h, Team attacker, Team defender) {
        if(attacker == defender) {
            throw new IllegalArgumentException("Attacker equals Defender");
        }
        
        if(attacker == null) {
            throw new IllegalArgumentException("Attacker cannot be null.");
        }
        
        if(defender == null) {
            throw new IllegalArgumentException("Defender cannot be null.");
        }
        
        this.width = w;
        this.height = h;
        this.attacker = attacker;
        this.defender = defender;
        this.board = new GamePiece[h][w];
        this.offensiveMoves = 0;
        this.waterCells = new ArrayList<>();
    }
    
    public void incrementMoveCount() {
        this.offensiveMoves++;
    }
    
    public void decrementMoveCount() {
        this.offensiveMoves--;
        if(this.offensiveMoves < 0) {
            throw new RuntimeException("offensiveMoves < 0");
        }
    }
    
    public int getMoveCount() {
        return this.offensiveMoves;
    }
    
    public void setMoveCount(int count) {
        this.offensiveMoves = count;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }   
    
    /**
     * A board position is valid when it represents cell coordinates that are
     * part of the board, that is 0 <= x < width and 0 <= y < height.
     */ 
    public boolean isValidPosition(BoardPosition position) {
        return (position.getX() >= 0 && position.getX() < this.width 
                && position.getY() >= 0 && position.getY() < this.height);
    }
    
    /**
     * Returns the piece at the given position on the board, if the provided
     * position is valid, that is an actual position on the game board and not
     * outside of the board. The special return value "null" indicates that a
     * possition is free.
     * 
     * @param position to get the piece from
     * @return the game piece of null if the cell is free.
     * @throws InvalidPositionException if the provided position is a position
     * outside the board.
     */
    public GamePiece getPiece(BoardPosition position) throws InvalidPositionException {
        if(!isValidPosition(position)) {
            throw createInvalidPositionException(position, null);
        }
        /** DEBUGGING
        System.out.println(position.toString());
        
        GamePiece piece = null;
        try {
            piece = this.board[position.getY()][position.getX()];
        } catch(ArrayIndexOutOfBoundsException e) {
            System.out.println("OutOfBounds for: " + position.toString());
        }
        
        return piece;*/
        //System.out.println(position.toString());
        return this.board[position.getY()][position.getX()];
    }
    
    public void movePiece(BoardPosition destination, GamePiece piece) 
            throws InvalidPositionException {
        if(!isValidPosition(destination)) {
            throw createInvalidPositionException(destination, piece);
        }
        if(!isEmpty(destination)) {
            throw new IllegalArgumentException("Given destination is not free.");
        }
        
        System.out.println("Move Piece: (" + destination.getX() + "," 
                + destination.getY() + ") -> " + piece.getRank().name());
        
        // Increment move counter if the piece is from the attacker.
        if(piece.getTeam() == this.attacker) {
            incrementMoveCount();
        }
        
        BoardPosition current = piece.getPosition();
        this.board[current.getY()][current.getX()] = null;
        
        // Update position of piece.
        piece.setPosition(destination);
        // Set piece to new position.
        this.board[destination.getY()][destination.getX()] = piece;
    }
    
    /**
     * Method that updates the position of a given piece. This method takes care
     * of updating the internal position reference of the piece object with the
     * new one. It is not possible to move a piece to a non-empty cell or an
     * invalid board position.
     * 
     * @param position new position for the given piece.
     * @param piece who's position is changed.
     * @throws InvalidPositionException if the given position does not lie within
     * the board.
     * @throws IllegalArgumentException if the given position is not empty.
     */
    public void setPiece(BoardPosition position, GamePiece piece) 
            throws InvalidPositionException {
        if(!isValidPosition(position)) {
            throw createInvalidPositionException(position, piece);
        }
        
        if(!isEmpty(position)) {
            throw new IllegalArgumentException("Given position is not free");
        }
        
        //System.out.println("Set Piece: (" + position.getX() + "," 
        //        + position.getY() + ") -> " + piece.getRank().name() + " (" + piece.getTeam() + ")");
        
        BoardPosition old = piece.getPosition();
        // If position is set for the first time it will not have an old position.
        // This will occur when a player is setting up his board with pieces.
        if(old != null) {
            // Remove piece from old position.
            this.board[old.getY()][old.getX()] = null;
        }

        // Attach new position to piece.
        piece.setPosition(position);
        // Set piece to new position.
        this.board[position.getY()][position.getX()] = piece;
    }
    
    public void removePieceAt(BoardPosition position) throws InvalidPositionException {
        if(!isValidPosition(position)) {
            throw createInvalidPositionException(position, null);
        }
        
        this.board[position.getY()][position.getX()] = null;
    }
    
    /**
     * Return a list containing the game pieces for the given team.
     * 
     * @param team
     * @return a list of game pieces.
     */ 
    public List<GamePiece> getTeam(Team team) {
        List<GamePiece> army = new ArrayList<>();
        
        for(int r=0; r<this.height; r++) {
            for(int c=0; c<this.width; c++) {
                GamePiece piece = this.board[r][c];
                if(piece != null && piece.getTeam() == team) {
                    army.add(piece);
                }
            }
        }
        return army;
    }
    
    /**
     * Returns the movable pieces from a particular team, with movable pieces
     * we mean pieces that are capable of moving and may or may not be currently
     * impeded. This means that the Flag and Bombs are removed from the List,
     * however pieces that are capable of moving, but current inable because
     * they are blocked by other neighbouring pieces are still included.
     * 
     * @param team
     * @return list of game pieces that are capable of moving.
     */
    public List<GamePiece> getMovablePieces(Team team) {
        List<GamePiece> army = getTeam(team);
        Iterator<GamePiece> iterator = army.iterator();
        
        while(iterator.hasNext()) {
            GamePiece piece = iterator.next();
            if(piece.isStatic()) {
               iterator.remove();
            }
        }
        return army;
    }
    
    /**
     * 
     * @param team
     * @return a list of moves the given team can apply.
     */
    public List<MoveAction> getMoves(Team team) {
        List<MoveAction> moves = new ArrayList<>();
        List<GamePiece> movable = getMovablePieces(team);
        for(GamePiece piece : movable) {
            moves.addAll(getMovesForPiece(piece));
        }
        return moves;
    }
    
    /**
     * 
     * @return a list of game pieces that are highlighted.
     */
    public List<GamePiece> getHihglightedPieces() {
        List<GamePiece> pieces = new ArrayList<>();
        
        for(int r=0; r<this.height; r++) {
            //System.out.println("Width: " + this.width);
            for(int c=0; c<this.width; c++) {
                //System.out.println("GetHighlighted: r=" + r + ", c=" + c);
                GamePiece piece = this.board[r][c];
                if(piece != null && piece.isHightlighted()) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }
    
    // Unused.
    public boolean isReachable(BoardPosition position, BoardPosition destination) throws InvalidPositionException {
        if(!isValidPosition(position)) {
            throw createInvalidPositionException(position, null);
        }
        if(!isValidPosition(destination)) {
            throw createInvalidPositionException(destination, null);
        }
        
        if(!isEmpty(destination)) {
            return false;
        }
        if(isEmpty(position)) {
            return false;
        }
        
        if(Math.abs(position.getX() - destination.getX()) <= 1 &&
                Math.abs(position.getY() - destination.getY()) <= 1) {
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @param position representing a board cell
     * @return true if the board cell at the given position is empty.
     * @throws Game.InvalidPositionException if the position does not lie on
     * the board.
     */ 
    public boolean isEmpty(BoardPosition position) throws InvalidPositionException {
        GamePiece piece = getPiece(position);
        return (piece == null);
    }
    
    private InvalidPositionException createInvalidPositionException(BoardPosition position, GamePiece piece) {
        String message = "Invalid position: (" +
                position.getX() + "," + position.getY() + ")";
        
        if(piece != null) {
            message+= (" -> " + piece.getRank().name());
        }
        return new InvalidPositionException(message);
    }
    
    /**
     * 
     * @param piece
     * @return a list of positions to which the given piece can move, that is
     * positions that are free or contain an enemy.
     */
    public List<BoardPosition> getValidMoves(GamePiece piece) {
        BoardPosition start = piece.getPosition();
        List<BoardPosition> neighbours = getNeighbours(start);
        Iterator<BoardPosition> iterator = neighbours.iterator();
        while(iterator.hasNext()) {
            // This try-catch is unnecessary since the valid position check is
            // done in the getHeighbours() function.
            try {
                BoardPosition pos = iterator.next();
                GamePiece other = getPiece(pos);
                // Remove the position if it is a cell containing a friendly
                // piece.
                if(other != null && !piece.isEnemy(other)) { 
                    iterator.remove();
                }    
            } catch (InvalidPositionException ex) {
                Logger.getLogger(GameBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return neighbours;
    }
    
    /**
     * Returns a list of moves that the given piece can perform, a move is
     * defined as moving the piece to an empty cell or attacking another piece
     * with this piece. Moving the piece to a cell occupied by a friendly unit
     * is not allowed.
     * 
     * @param piece to query moves from.
     * @return a list of allowed moves for this piece.
     */
    public List<MoveAction> getMovesForPiece(GamePiece piece) {
        List<MoveAction> moves = new ArrayList<>();
        BoardPosition origin = piece.getPosition();
        List<BoardPosition> neighbours = getNeighbours(origin);
        for(BoardPosition pos : neighbours) {
            try {
                // Valid move if cell is empty or cell contains enemy.
                // Invalid if cell contains friendly unit.
                GamePiece other = getPiece(pos);
                if(other == null) { // Empty cell.
                    moves.add(new MoveAction(piece.getTeam(), piece, origin, pos));
                } else if(piece.isEnemy(other)) { // Enemy.
                    MoveAction m = new MoveAction(piece.getTeam(), piece, origin, pos);
                    // Marks this move as an attack.
                    m.setAttack();
                    m.setEnemy(other);
                    moves.add(m);
                }
            } catch (InvalidPositionException ex) {
                Logger.getLogger(GameBoard.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return moves;
    }
    
    /**
     * Returns all valid orthogonal neighbouring cells of a given center point.
     * By valid we mean positions that are actually on the board, for example if
     * you take the corner position as center, than you will have two virtual
     * orthogonally neighbouring cells that fall outside the board.
     * 
     * @param position center from which you want to know the orthogonal neighbours.
     * @return real orthogonally neighbouring cells. (no virtual cells)
     */
    public List<BoardPosition> getNeighbours(BoardPosition position) {
        int x = position.getX();
        int y = position.getY();
        // Right neigbour.
        BoardPosition one = new BoardPosition(x + 1, y);
        // Left neighbour.
        BoardPosition two = new BoardPosition(x - 1, y);
        // Top neighbour.
        BoardPosition three = new BoardPosition(x, y + 1);
        // Bottom neighbour.
        BoardPosition four = new BoardPosition(x, y - 1);
        // Add all neighbours to a list.
        List<BoardPosition> list = new ArrayList<>();
        list.add(one);
        list.add(two);
        list.add(three);
        list.add(four);
        
        // Remove the invalid neighbours, these are virtual cells that exist
        // if you would extend the board, but which do not actually lie on the
        // game board.
        Iterator<BoardPosition> iterator = list.iterator();
        while(iterator.hasNext()) {
            if(!isValidPosition(iterator.next())) {
                iterator.remove();
            }
        }
        return list;
    }
    
    /**
     * Kills the given piece.
     * 
     * @param piece to be killed.
     */
    public void killPiece(GamePiece piece) {
        if(!piece.isAlive()) {
            throw new IllegalStateException("killPiece(): Piece is already dead.");
        }        
        
        BoardPosition position = piece.getPosition();
        // Delete the reference of the piece.
        this.board[position.getY()][position.getX()] = null;
        piece.die();
    }
    
    /**
     * Revives the given piece.
     * 
     * @param piece to be revied.
     */
    public void revivePiece(GamePiece piece) {
        if(piece.isAlive()) {
            throw new IllegalStateException("revivePiece(): Piece is not dead.");
        }
        
        BoardPosition position = piece.getPosition();
        this.board[position.getY()][position.getX()] = piece;
        piece.revive();
    }
    
    /**
     * Get a list of pieces of the same type for a particular team.
     * 
     * @param team player's team
     * @param type piece type
     * @return list of game pieces of the particular type for the given team.
     */
    public List<GamePiece> getPieces(Team team, Pieces type) {
        List<GamePiece> pieces = new ArrayList<>();
        for(int r=0; r<this.height; r++) {
            for(int c=0; c<this.width; c++) {
                GamePiece piece = this.board[r][c];
                // If this piece is of the given rank and given team.
                if(piece != null && piece.getRank() == type 
                        && piece.getTeam() == team) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }
    
    /**
     * The games ends in either of these cases:
     * The attacker has not won the game and exceeded the movement limit.
     * The attacker has captured the flag.
     * The defender successfully killed all the miners and the flag his
     * surrounded by bombs, hence making it impossible for the attacker to
     * capture the flag.
     * 
     * @return null if this state is not an end state or the winning team if it
     * is an end state.
     */    
    public Team isEndState() {
        if(this.offensiveMoves >= this.offensiveMovesLimit) {
            return this.defender;
        }
        
        // In our game the attacker should not have a flag, but for testing it
        // is useful to let both teams compete for the flag.
        /*
        List<GamePiece> attackerFlag = getPieces(this.attacker, Pieces.FLAG);
        if(attackerFlag.isEmpty()) {
            return this.defender;
        }
        */
        
        // Has the flag been captured? Count the # of flags on the defending
        // side.
        List<GamePiece> defenderFlag = getPieces(this.defender, Pieces.FLAG);
        if(defenderFlag.isEmpty()) {
            //System.out.println("Defender has no flag.");
            return this.attacker;
        }
        
        
        // The game is in an end state if the attacker has no miners left and
        // the defending side has the flag completely surrounded with bombs.
        List<GamePiece> miners = getPieces(this.attacker, Pieces.MINER);
        if(miners.isEmpty()) {
            // No miners left, check if the flag is surrounded by bombs.
            if(isFlagUnreachable()) {
                //System.out.println("No miners left and flag unreachable.");
                return this.defender;
            }
        }
        
        // Attacker has no pieces left to move.
        List<GamePiece> movablePiecesAttacker = getMovablePieces(this.attacker);
        if(movablePiecesAttacker.isEmpty()) {
            //System.out.println("Attacker has no more movable pieces.");
            return this.defender;
        }
        
        // Defender has no pieces left to move.
        List<GamePiece> movablePiecesDefender = getMovablePieces(this.defender);
        if(movablePiecesDefender.isEmpty()) {
            //System.out.println("Defender has no more movable pieces.");
            return this.attacker;
        }
        
        // No end state.
        return null;
    }
    
    /**
     * This algorithm cannot handle a general case where a player is allowed
     * to fill n rows. It can only handle the specific case where a player must
     * fill two rows. Instead we solve the simple case that is relevant
     * for our project, in which a player is only allowed to fill the last 2
     * rows with pieces, that is either row 0 and 1, or row (h - 1) and (h - 2),
     * where h indicates the height of the board, that is the # of rows.
     * If the flag is positioned at row 1 or row (h - 2) that it can never be
     * surrounded by bombs, in order to surround it you should put a bomb on
     * row 2 for the first case and row (h - 3) for the latter. However this is
     * not allowed by definition. Hence a flag in this setting can only be 
     * surrounded if it is placed on the last row, which is either row 0 or row
     * (h - 1). In that case it is simple to verify if a bomb is surrounded,
     * because the only valid way to surround a bomb placed on the last row is
     * by placing bombs at all its orthogonal neighbouring cells.
     * 
     * @return true if the flag is surrounded by bombs else it returns false.
     */
    public boolean isFlagUnreachable() {
        List<GamePiece> flags = getPieces(this.defender, Pieces.FLAG);
        if(flags.isEmpty()) {
            return false;
        }
        
        GamePiece flag = flags.get(0);
        BoardPosition origin = flag.getPosition();
        int y = origin.getY();
        
        // Is the flag surrounded by bombs?
        boolean surrounded = false;
        if(y == 0 || y == (this.height - 1)) {
            // Check now if all the nieghbours of the flag are bombs otherwise
            // it is not surrounded by bombs.
            boolean bombs = true;
            List<BoardPosition> neighbours = this.getNeighbours(origin);
            for(BoardPosition neighbour : neighbours) {
                try {
                    GamePiece piece = getPiece(neighbour);
                    // This neighbour is an empty cell or the surrounding piece
                    // is not a bomb, this means the flag is not surrounded
                    // by bombs.
                    if(piece == null || piece.getRank() != Pieces.BOMB) {
                        bombs = false;
                        break;
                    }
                    
                } catch (InvalidPositionException ex) {
                    Logger.getLogger(GameBoard.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if(bombs) {
                surrounded = true;
            }
        }
        return surrounded;
    }
    
    /**
     * Takes the Manhatten Distance between two board positions. This distance
     * does not take obstructing pieces and blocking water pools into account.
     * 
     * 
     * @param start
     * @param end
     * @return 
     */
    public static int distance(BoardPosition start, BoardPosition end) {
        int x = Math.abs(start.getX() - end.getX());
        int y = Math.abs(start.getY() - end.getY());
        return (x + y);
    }
    
    /**
     * Method intended for the AI to apply a move.
     * This method does not check if the game board is already in an end state.
     * 
     * @param move 
     */
    public void applyMove(MoveAction move) {
        if(move.isApplied()) {
            throw new RuntimeException("Move: " + move.toString() + " has already been applied.");
        }
        
        //GamePiece piece = move.getPiece();
        BoardPosition origin = move.getOrigin();
        
        // Beware that the GamePiece reference stored in the move is a reference
        // to a copy of the original GamePiece, this copy can have a different
        // position than the original piece. This happens only if a search
        // algorithm is interrupted and therefore is not able to undo all moves
        // and in that case it can happen that the copied GamePieces have 
        // incorrect positions compared to the original board. That's why it is
        // now better to get the original GamePiece from the board based on the
        // origin stored in the MoveAction which is always valid. Rather than
        // accessing the GamePiece reference stored in the MoveAction which
        // refers to a copy of the GamePiece not the original.
        GamePiece piece = null;
        try {
            piece = this.getPiece(origin);
        } catch (InvalidPositionException ex) {
            Logger.getLogger(GameBoard.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(piece == null) {
            throw new NullPointerException("boardPiece == null, move=" + 
                    move.toString() + 
                    ", origin=" +
                    origin.toString() + "\nBoardState:\n" + transcript());
        }
        
        /** Too strict, complicates the situation where we want to keep 
         * cloned move objects for the BattleTranscript.
        if(move.getPiece() != piece) {
            throw new RuntimeException("Apply Move Referrences Differ piece=" + 
                    piece.toString() + ", movePiece="
                    + move.getPiece().toString() + ", pieceRef=" + piece + ", movePieceRef=" + move.getPiece());
        }*/
        
        // The internal position of the piece must equal the origin position
        // of the MoveAction.
        if(!piece.getPosition().equals(origin)) {
            throw new RuntimeException(piece.toString() 
                    + " has incorrect position for applying move " 
                    + move.toString() + "\nBoardState:\n" + this.transcript());
        }
        
        BoardPosition destination = move.getDestination();
        //System.out.println("Execute move: " + move.toString());
        
        // Set flag to true on move object.
        move.setApplied(true);
        
        // Switch turns.
        setCurrentTurn(move.getTeam().opposite());
        //switchTurns(move.getTeam());
        
        try {
            // Increment move count if the piece is from the attacker.
            if(piece.getTeam() == this.attacker) {
                //System.out.println("Increment MoveCount");
                incrementMoveCount();
                //System.out.println("moveCount: " + getMoveCount());
            }
            
            // Empty original position of the piece.
            this.board[origin.getY()][origin.getX()] = null;
            // Update the position of the attacking piece to the new position.
            // We do this always, even if the piece would die, so that we
            // know the spot from where it died.
            //piece.setPosition(destination);            
            
            // Destination of piece.
            //this.board[destination.getY()][destination.getX()] = piece;               
            
            // Is it an attack?
            GamePiece opponent = this.getPiece(destination);            
            
            boolean isAttack = false;
            // Contains an enemy.
            if(opponent != null && piece.isEnemy(opponent)) {
                isAttack = true;
                //System.out.println("isATTACK");
                // Apply attack.
                int result = piece.attack(opponent);
                if(result == 1) { // Piece wins.
                    killPiece(opponent);
                    move.setDeadOpponent(opponent);
                    this.board[destination.getY()][destination.getX()] = piece;
                    piece.setPosition(destination);
                } else if(result == -1) { // Opponents wins.
                    killPiece(piece);
                    // The position where the attacker dies must be updated
                    // after is has been killed, else killPiece removes
                    // the opponent.
                    piece.setPosition(destination);
                    
                    move.setDeadAttacker(piece);
                    return;
                } else { // Both die.
                    killPiece(piece);
                    // The position where the attacker dies must be updated
                    // after is has been killed, else killPiece removes
                    // the opponent.
                    piece.setPosition(destination);
                    
                    killPiece(opponent);
                    move.setDeadAttacker(piece);
                    move.setDeadOpponent(opponent);
                    return;
                }
            } else {
                piece.setPosition(destination);
                this.board[destination.getY()][destination.getX()] = piece;
            }
            
            // If the code reaches this position then the piece either attacked
            // the opponent successfully or he is moving to an empty cell.
            //System.out.println("Successful ATTACK or MOVE to EMPTY CELL");
            if(!isAttack) {
                //System.out.println("Move to EMPTY CELL");
            }
        } catch (InvalidPositionException ex) {
            Logger.getLogger(GameBoard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Method intended for the AI to undo a move.
     * @param move 
     */
    public void undoMove(MoveAction move) {
        // Check if flag was set and by that if the move has actually been
        // applied.
        if(!move.isApplied()) {
            throw new IllegalArgumentException(move.toString() + " has never been applied.");
        }
        
        GamePiece piece = move.getPiece();
        BoardPosition origin = move.getOrigin();
        BoardPosition destination = move.getDestination();
        
        if(!piece.getPosition().equals(destination)) {
            throw new RuntimeException(piece.toString() 
                    + " has incorrect position for undoing move " 
                    + move.toString() + "\nBoardState:\n" + this.transcript());
        }
        
        // Reset applied flag to false.
        move.setApplied(false);        
        
        GamePiece deadAttacker = move.getDeadAttacker();
        GamePiece deadOpponent = move.getDeadOpponent();
        
        // Check if the reference to both GamePiece objects are the same.
        if(deadAttacker != null && deadAttacker != piece) {
            throw new RuntimeException("References differ.\n Piece: " 
                    + piece.toString() + "\n deadAttacker: " + piece.toString());
        }
        
        // Switch turn back to original.
        setCurrentTurn(move.getTeam());
        
        // Move piece back to the original position.
        // Piece and Attacker refer to the same object, so restoring the
        // position can be done on any.
        piece.setPosition(origin);
        
        if(deadAttacker != null && deadOpponent != null) {
            // Both died.
            revivePiece(deadAttacker);
            revivePiece(deadOpponent);
        } else if(deadAttacker != null) {
            // Attacker died.
            revivePiece(deadAttacker);
        } else if(deadOpponent != null) {
            // Remove the attacker from the position where the opponent died.
            this.board[destination.getY()][destination.getX()] = null;
            // Opponent died, thus the attacker wins.
            // Revival of the opponent will overwrite the attacking piece
            // stored at the destination.
            revivePiece(deadOpponent);
            
            // Move piece back to the original position.
            //piece.setPosition(origin);
            this.board[origin.getY()][origin.getX()] = piece;    
        } else {
            // No one died, so the attacker moved to an empty cell.
            // Remove the piece from the destination.
            this.board[destination.getY()][destination.getX()] = null;
            // Move piece back to the original position.
            //piece.setPosition(origin);
            this.board[origin.getY()][origin.getX()] = piece;    
        }
        
        // Decrement move count if the piece is from the attacker.
        if(piece.getTeam() == this.attacker) {
            decrementMoveCount();
        }
    }

    /**
     * Shortcut method used to setup the initial board positions in a convenient
     * way.
     * 
     * @param x
     * @param y
     * @param rank
     * @param team
     * @throws InvalidPositionException 
     */
    public void setupPiece(int x, int y, Pieces rank, Team team) 
            throws InvalidPositionException {
        BoardPosition position = new BoardPosition(x, y);
        setupPiece(position, rank, team);
    }
    
    /**
     * Should be used to conveniently setup pieces on the board before the game
     * starts.
     * 
     * @param position
     * @param rank
     * @param team 
     * @throws Game.InvalidPositionException 
     */
    public void setupPiece(BoardPosition position, Pieces rank, Team team) 
            throws InvalidPositionException {
        if(!isValidPosition(position)) {
            throw createInvalidPositionException(position, null);
        }
        if(!isEmpty(position)) {
            throw new IllegalArgumentException("Given position is not free");
        }
        
        GamePiece piece = new GamePiece(rank, team, position);
        this.board[position.getY()][position.getX()] = piece;
    }

    @Override
    public Object clone() {
        GameBoard clone = new GameBoard(width, height, attacker, defender);
        // Copy all pieces to the clone.
        for(int r=0; r<height; r++) {
            for(int c=0; c<width; c++) {
                try {
                    BoardPosition position = new BoardPosition(c, r);
                    GamePiece piece = getPiece(position);
                    if(piece != null) {
                        GamePiece clonedPiece = (GamePiece) piece.clone();
                        clone.setPiece(position, clonedPiece);
                    }
                } catch (InvalidPositionException ex) {
                    Logger.getLogger(GameBoard.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        // Copy other important metadata.
        clone.setMoveCount(getMoveCount());
        // TODO Copy the positions of the water cells.
        
        return clone;
    }
    
    public Team getAttacker() {
        return this.attacker;
    }
    
    public Team getDefender() {
        return this.defender;
    }

    public Team getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(Team currentTurn) {
        this.currentTurn = currentTurn;
    }
    
    private void switchTurns(Team current) {
        this.currentTurn = current.opposite();
    }
    
    /**
     * Output a textual representation of the current board state.
     * @return a string that represents the board state.
     */
    public String transcript() {
        StringBuilder builder = new StringBuilder();
        
        for(int r=0; r<this.height; r++) {
            for(int c=0; c<this.width; c++) {
                GamePiece piece = this.board[r][c];
                if(piece != null) {
                    builder.append(piece.getTeam().getSymbol())
                            .append(":")
                            .append(piece.getRank().getPieceSymbol());
                } else {
                    builder.append("   ");
                }
                
                if(c < this.width - 1) {
                    builder.append("|");
                }
            }
            builder.append("\n");
            
            if(r < this.height - 1) {
                for(int i=0; i<this.width; i++) {
                    builder.append("---");
                    if(i < this.width - 1) {
                        builder.append(" ");
                    }
                }
                builder.append("\n");
            }
        }
        return builder.toString();
    }
    
    public static GameBoard loadBoard(String transcript, int w, int h) {
        GameBoard board = new GameBoard(w, h, Team.RED, Team.BLUE);
        String[] lines = transcript.split("\n");
        
        int row = 0;
        for(String line : lines) {
            if(line.contains("---")) {
                continue;
            }
            
            String[] cells = line.split("\\|");
            int column = 0;
            for(String cell : cells) {
                // Cell is not empty.
                //if(!cell.contains("\\s*")) {
                if(!cell.contains("   ")) {
                    // Parse the team.
                    char teamSymbol = cell.charAt(0);
                    Team team;
                    if(teamSymbol == 'r') {
                        team = Team.RED;
                    } else if(teamSymbol == 'b') {
                        team = Team.BLUE;
                    } else {
                        throw new RuntimeException("Bad team identifier for cell: " + cell);
                    }
                    
                    // Parse the piece.
                    char pieceSymbol = cell.charAt(2);
                    try {
                        //GamePiece piece = new GamePiece(Pieces.bySymbol(pieceSymbol + ""), team);
                        board.setupPiece(column, row,
                                Pieces.bySymbol(pieceSymbol + ""), team);
                    } catch (InvalidPositionException ex) {
                        Logger.getLogger(GameBoard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                column++;
            }
            row++;
        }
        
        return board;
    }
    
    /**
     * Unsafe operation that merges two game boards blindly without checking
     * for conflicts. This function is intended for the training facility to
     * be used for merging player setups.
     */ 
    public void mergeBoard(GameBoard other) {
        if(this.width != other.width || this.height != other.height) {
            throw new IllegalArgumentException("Dimensions mismatch.");
        }
        
        for(int row=0; row<other.height; row++) {
            for(int column=0; column<other.width; column++) {
                // Must create a new GamePiece to prevent state corruption.
                // With boards that use the same reference, changes are reflected
                // and can corrupt state.
                GamePiece piece = other.board[row][column];
                if(piece != null) {
                    // Transfer piece to this board.
                    if(this.board[row][column] == null) { // Empty cell.
                        this.board[row][column] = ((GamePiece) piece.clone());
                    } else {
                        throw new IllegalStateException("Cannot merge boards, positiion already occupied.");
                    }
                }
            }
        }
    }
    
    public boolean isSetupValid() {
        boolean valid = true;
        for(int row=0; row<this.height; row++) {
            for(int column=0; column<this.width; column++) {
                GamePiece piece = this.board[row][column];
                if(piece != null) {
                    if(this.board[row][column] != null) {
                        // Verify that the position stored within the piece
                        // object is correct.
                        if(!(new BoardPosition(column, row)).equals(piece.getPosition())) {
                            valid = false;
                            return valid;
                        }
                    }
                }
            }
        }
        return valid;
    }
}
