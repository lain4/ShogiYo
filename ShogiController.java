package game;


import java.util.List;


interface ShogiController {

    boolean gameOver();

    ShogiMove getLastMove();

    int getTarget(int row, int col, int dir, int pow);

    boolean canDrop(ShogiPiece sp, int row, int col);

    boolean isLegal(int row, int col, int dir, int pow);

    ShogiPiece getPiece(int row, int col);

    void movePiece(int row, int col, int dir, int pow, boolean promo);

    void dropPiece(ShogiPiece sp, int row, int col);

    boolean isFriend(int row, int col);

    boolean isEnemy(int row, int col);

    boolean isEmpty(int row, int col);

    int[][] getBoard();

    int getSize();

    boolean sideWon();

    boolean turn();

    void newPosition();

    boolean pawnInCol(int col);

    int getAvailablePath(int row, int col, int dir);

    List<Integer> getMochi(boolean turn);

    int getSenKingRow();

    int getSenKingCol();

    int getGoKingRow();

    int getGoKingCol();

    boolean hasMochi();

    void undo(ShogiMove move);

    void setGoKing(int row, int col);

    void setSenKing(int row, int col);

    void move(ShogiMove move);


}
