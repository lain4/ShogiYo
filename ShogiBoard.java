package game;


import java.util.List;

interface ShogiBoard {

    int getSize();

    // ? SENTE : GOTE
    boolean turn();

    int[][] getBoard();

    List<Integer> getMochi(boolean player);

    ShogiPiece getPiece(int row, int col);

    List<ShogiMove> getAllMoves();

    int getPresence();

    ShogiMove getLastMove();

    boolean isLegal(int row, int col, int dir, int pow);

    boolean isFriend(int row, int col);

    boolean isEnemy(int row, int col);

    boolean isEmpty(int row, int col);

    boolean pawnInCol(int row);

    boolean gameOver();

    boolean canDrop(ShogiPiece sp, int row, int col);

    int getAvailablePath(int row, int col, int dir);

    boolean isMate(int row, int col, int dir, int pow);

    int getTarget(int row, int col, int dir, int pow);

    int getMaterialValue();

    int getDef();

    void dropPiece(ShogiPiece sp, int row, int col);

    void endTurn();


    //MOVE PIECE @row, col -> DIRECTION dir -> POWER pow

    void movePiece(int row, int col, int dir, int pow, boolean promo);

    void move(ShogiMove move);

    void undo(ShogiMove move);

    //STARTPOS
    void newPosition();

}
