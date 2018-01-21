package game;

import tools.Tools;

final class Move implements ShogiMove {

    private final int row;
    private final int col;
    private int dir;
    private int pow;
    private boolean promo;
    private int victim = 0;
    private int piece = 0;

    Move(int row, int col, int dir, int pow, boolean promo, int victim) {
        this.row = row;
        this.col = col;
        this.dir = dir;
        this.pow = pow;
        this.promo = promo;
        this.victim = victim;
    }

    Move(int piece, int row, int col) {
        this.row = row;
        this.col = col;
        this.piece = piece;
    }

    @Override
    public int compareTo(ShogiMove other) {
        return Integer.compare(Tools.getValue(other.getVictim()), Tools.getValue(getVictim()));
    }

    @Override
    public ShogiPiece getShogiDrop() {
        return Tools.getPiece(piece);
    }

    @Override
    public int getDrop() {
        return piece;
    }

    @Override
    public boolean hasKilled() {
        return victim != 0;
    }

    @Override
    public boolean isDrop() {
        return piece != 0;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getCol() {
        return col;
    }

    @Override
    public int getDir() {
        return dir;
    }

    @Override
    public int getPow() {
        return pow;
    }

    @Override
    public boolean isPromoting() {
        return promo;
    }

    @Override
    public int getVictim() {
        return victim;
    }

}
