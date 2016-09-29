package Game;

/**
 * Piece types.
 */
public enum Pieces {
    FLAG,
    BOMB,
    SPY,
    SCOUT,
    MINER,
    SERGEANT,
    LIEUTENANT,
    CAPTAIN,
    MAJOR,
    COLONEL,
    GENERAL,
    MARSHALL;
    
    public String getPieceSymbol() {
        switch(this) {
            case BOMB:
                return "B";
            case FLAG:
                return "F";
            case SPY:
                return "S";
            case SCOUT:
                return "1";
            case MINER:
                return "2";
            case SERGEANT:
                return "3";
            case LIEUTENANT:
                return "4";
            case CAPTAIN:
                return "5";
            case MAJOR:
                return "6";
            case COLONEL:
                return "7";
            case GENERAL:
                return "8";
            case MARSHALL:
                return "9";
            default:
                return "X";
        }
    }
}
