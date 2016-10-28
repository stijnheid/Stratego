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
                return "0";
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
    
    public static Pieces bySymbol(String symbol) {
        switch(symbol) {
            case "B":
                return BOMB;
            case "F":
                return FLAG;
            case "0":
                return SPY;
            case "1":
                return SCOUT;
            case "2":
                return MINER;
            case "3":
                return SERGEANT;
            case "4":
                return LIEUTENANT;
            case "5":
                return CAPTAIN;
            case "6":
                return MAJOR;
            case "7":
                return COLONEL;
            case "8":
                return GENERAL;
            case "9":
                return MARSHALL;
            default:
                return null;
        }
    }
}
