package game;

public interface ShogiController {

    boolean turn();

    boolean isLegal(int row, int col, int dir, int pow);

    boolean isLegal(FieldPane pane);

    boolean isFriend(int row, int col);

    boolean isFriend(FieldPane pane);

    boolean isEnemy(int row, int col);

    boolean isEnemy(FieldPane pane);

    boolean isEmpty(int row, int col);

    boolean isEmpty(FieldPane pane);

    boolean isPromotable(int row, int col);

    boolean isPromotable(FieldPane pane);

    void dropPiece(int piece, int row, int col);

    void movePiece(int row, int col, int dir, int pow, boolean promo);

    boolean hasSpace(int piece, int row);

    boolean pawnInCol(int col);

    boolean gameOver();


    void newGame();

}
