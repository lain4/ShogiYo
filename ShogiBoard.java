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

    int see(int row, int col);

    int getPresence();

    boolean isLegal(ShogiMove move);

    ShogiMove getLastMove();

    boolean isLegal(int row, int col, int dir, int pow);

    boolean isFriend(int row, int col);

    boolean isEnemy(int row, int col);

    boolean isEmpty(int row, int col);

    boolean pawnInCol(int row);

    boolean gameOver();

    boolean canDrop(ShogiPiece sp, int row, int col);

    boolean hasMochi();

    int getAvailablePath(int row, int col, int dir);

    boolean isPromotable(int row, int col);

    boolean hasSpace(ShogiPiece sp, int row);

    boolean isMate(int row, int col, int dir, int pow);

    int getTarget(int row, int col, int dir, int pow);

    int getMaterialValue();

    boolean isEnemyKing(int row, int col);

    boolean isOwnKing(int row, int col);

    int getDef();

    void dropPiece(ShogiPiece sp, int row, int col);

    void endTurn();

    void setTurn(boolean turn);


    //MOVE PIECE @row, col -> DIRECTION dir -> POWER pow

    void movePiece(int row, int col, int dir, int pow, boolean promo);

    void move(ShogiMove move);

    void undo(ShogiMove move);

    //STARTPOS
    void newPosition();

}
