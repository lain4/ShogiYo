package game;

public interface ShogiMove extends Comparable<ShogiMove> {

    int getRow();

    int getCol();

    int getDir();

    int getPow();

    int getDrop();

    ShogiPiece getShogiDrop();

    boolean isPromoting();

    int getVictim();

    boolean isDrop();

    boolean hasKilled();

    ShogiPiece getShogi();

}
