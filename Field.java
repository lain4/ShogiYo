package game;

public interface Field {

    boolean isCovered();

    ShogiPiece getPiece();

    void setPiece(ShogiPiece sp);

}
