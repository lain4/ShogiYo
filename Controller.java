package game;

import tools.Tools;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

final class Controller implements ShogiController {

    private final int[][] board;
    private final int SIZE;
    private ShogiBoard sb;
    private boolean turn = true;
    private int senKingRow = 0;
    private int senKingCol = 0;
    private int goKingRow = 0;
    private int goKingCol = 0;
    private int gameDone = 0;
    private ShogiMove lastMove;


    Controller(ShogiBoard sb) {
        this.sb = sb;
        board = sb.getBoard();
        SIZE = board.length;
    }

    @Override
    public boolean gameOver() {
        return gameDone != 0;
    }

    @Override
    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    @Override
    public ShogiMove getLastMove() {
        return lastMove;
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    @Override
    public ShogiBoard getShogiBoard() {
        return sb;
    }

    @Override
    public void setShogiBoard(ShogiBoard sb) {
        if (!this.sb.equals(sb))
            this.sb = sb;
    }

    @Override
    public int getTarget(int row, int col, int dir, int pow) {

        row = Tools.getRowFor(row, dir, pow, turn);
        col = Tools.getColFor(col, dir, pow, turn);

        return inBounds(row, col) ? board[row][col] : 0;
    }

    @Override
    public boolean canDrop(ShogiPiece sp, int row, int col) {
        return hasSpace(sp, row) &&
                !sp.equals(Koma.FUHYO) &&
                pawnInCol(col);
    }

    @Override
    public boolean isLegal(int row, int col, int dir, int pow) {

        if (inBounds(row, col) &&
                isFriend(row, col) &&
                Objects.requireNonNull(getPiece(row, col)).canMove(dir, pow > 1)) {

            if (Objects.requireNonNull(getPiece(row, col)).equals(Koma.KEIMA)) {

                row = Tools.getRowFor(row, dir, pow, turn);
                col = Tools.getColFor(col, dir, pow, turn);
                return ((pow == 2) && inBounds(row, col) && !isFriend(row, col));

            } else
                return isPath(row, col, dir, pow);

        } else
            return false;
    }

    @Override
    public ShogiPiece getPiece(int row, int col) {
        return inBounds(row, col) ? Tools.getPiece(board[row][col]) : null;
    }

    @Override
    public void movePiece(int row, int col, int dir, int pow, boolean promo) {


        if (!isLegal(row, col, dir, pow))
            System.err.println("Illegal move!");
        else {

            int newRow = Tools.getRowFor(row, dir, pow, turn);
            int newCol = Tools.getColFor(col, dir, pow, turn);

            if (turn ? (newRow > 2 && row > 2) : (newRow < 6 && row < 6)) promo = false;

            int victim = board[newRow][newCol];
            lastMove = new Move(row, col, dir, pow, promo, victim);

            move(row, col, newRow, newCol, promo);
        }
    }

    @Override
    public void dropPiece(ShogiPiece sp, int row, int col) {

        if (!canDrop(sp, row, col))
            System.err.println("Illegal drop!");

        else {

            int piece = sp.getOrd();

            lastMove = new Move(piece, row, col);

            piece *= turn ? 1 : -1;

            if (sb.dropExists(piece))
                board[row][col] = piece;
            else
                System.err.println("Drop not existing WTF?!");

            endTurn();
        }
    }

    @Override
    public boolean isFriend(int row, int col) {
        return turn ? board[row][col] > 0 : board[row][col] < 0;
    }

    @Override
    public boolean isEnemy(int row, int col) {
        return turn ? board[row][col] < 0 : board[row][col] > 0;
    }

    @Override
    public boolean isEmpty(int row, int col) {
        return board[row][col] == 0;
    }

    @Override
    public int[][] getBoard() {
        return board;
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public boolean sideWon() {
        return gameOver() && gameDone == 1;
    }

    @Override
    public boolean turn() {
        return turn;
    }

    @Override
    public void newPosition() {

        turn = true;
        sb.resetMochi();
        gameDone = 0;

        for (int row = 0; row < SIZE; row++)
            for (int col = 0; col < SIZE; col++)
                board[row][col] = 0;


        for (int i = 0; i < SIZE; i++) {
            board[2][i] = -Koma.FUHYO.ordinal();
            board[SIZE - 3][i] = Koma.FUHYO.ordinal();
        }


        board[1][SIZE - 2] = -Koma.KAKUGYO.ordinal();
        board[SIZE - 2][1] = Koma.KAKUGYO.ordinal();


        board[1][1] = -Koma.HISHA.ordinal();
        board[SIZE - 2][SIZE - 2] = Koma.HISHA.ordinal();


        board[0][4] = -14;
        goKingCol = 4;
        goKingRow = 0;
        board[SIZE - 1][4] = 14;
        senKingRow = 8;
        senKingCol = 4;


        board[0][3] = board[0][5] = -Koma.KINSHO.ordinal();
        board[SIZE - 1][3] = board[SIZE - 1][5] = Koma.KINSHO.ordinal();


        board[0][2] = board[0][SIZE - 3] = -Koma.GINSHO.ordinal();
        board[SIZE - 1][2] = board[SIZE - 1][SIZE - 3] = Koma.GINSHO.ordinal();


        board[0][1] = board[0][SIZE - 2] = -Koma.KEIMA.ordinal();
        board[SIZE - 1][1] = board[SIZE - 1][SIZE - 2] = Koma.KEIMA.ordinal();


        board[0][0] = board[0][SIZE - 1] = -Koma.KYOSHA.ordinal();
        board[SIZE - 1][0] = board[SIZE - 1][SIZE - 1] = Koma.KYOSHA.ordinal();

    }

    @Override
    public boolean pawnInCol(int col) {
        return IntStream.range(0, SIZE)
                .anyMatch(row -> board[row][col] == (turn ? Koma.FUHYO.ordinal() : -Koma.FUHYO.ordinal()));
    }

    @Override
    public int getAvailablePath(int row, int col, int dir) {

        int pow = 0;

        while (true) {
            if (!isLegal(row, col, dir, ++pow))
                return pow - 1;
        }

    }

    private void endTurn() {
        turn = !turn;
    }


    private boolean isPath(int row, int col, int dir, int pow) {

        if (pow == 0)
            return inBounds(row, col) &&
                    !isFriend(row, col);
        else {

            row = Tools.getRowFor(row, dir, 1, turn);
            col = Tools.getColFor(col, dir, 1, turn);

            return (pow <= 1 || isEmpty(row, col)) &&
                    isPath(row, col, dir, --pow);
        }
    }


    private boolean hasSpace(ShogiPiece sp, int row) {

        if (sp == null)
            return false;
        else

            switch (sp.getOrd()) {
                case 4:
                    return (turn ? row > 1 : row < 7);
                case 5:
                case 6:
                    return (turn ? row != 0 : row != 8);
                default:
                    return true;
            }
    }

    @Override
    public List<Integer> getMochi(boolean turn) {
        return sb.getMochi(turn);
    }

    @Override
    public int getSenKingRow() {
        return senKingRow;
    }

    @Override
    public int getSenKingCol() {
        return senKingCol;
    }

    @Override
    public int getGoKingRow() {
        return goKingRow;
    }

    @Override
    public int getGoKingCol() {
        return goKingCol;
    }

    @Override
    public boolean hasMochi() {
        return sb.hasMochi(turn);
    }

    @Override
    public void undo(ShogiMove move) {

        if (move.equals(lastMove)) lastMove = null;

        if (gameDone != 0 &&
                move.hasKilled() &&
                Tools.getPiece(move.getVictim()).equals(Koma.OSHO))
            gameDone = 0;

        endTurn();

        if (!move.isDrop()) {

            int row = Tools.getRowFor(move, turn);
            int col = Tools.getColFor(move, turn);

            int piece = move.isPromoting() ? Tools.getOrigin(board[row][col]) : board[row][col];
            int victim = move.getVictim();


            board[move.getRow()][move.getCol()] = piece;
            board[row][col] = victim;


            if (Math.abs(piece) == Koma.OSHO.getOrd())

                if (!turn)
                    setGoKing(row, col);
                else
                    setSenKing(row, col);

            else if (Math.abs(victim) == Koma.OSHO.getOrd())

                if (turn)
                    setGoKing(row, col);
                else
                    setSenKing(row, col);


            if (move.hasKilled())
                sb.dropExists(Tools.getOrigin(-victim));

        } else {
            sb.addToMochi(turn ? move.getDrop() : -move.getDrop());
            board[move.getRow()][move.getCol()] = 0;
        }

    }

    @Override
    public void setGoKing(int row, int col) {
        goKingRow = row;
        goKingCol = col;
    }

    @Override
    public void setSenKing(int row, int col) {
        senKingRow = row;
        senKingCol = col;
    }

    @Override
    public void move(ShogiMove move) {

        if (move.isDrop())
            dropPiece(move.getShogiDrop(), move.getRow(), move.getCol());
        else
            movePiece(move.getRow(), move.getCol(), move.getDir(), move.getPow(), move.isPromoting());
    }


    private void move(int row, int col, int t_row, int t_col, boolean promo) {

        ShogiPiece sp;

        if (isEnemy(t_row, t_col)) {
            sp = getPiece(t_row, t_col);

            if (!Objects.requireNonNull(sp).equals(Koma.OSHO))
                sb.addToMochi(turn ? sp.getOriOrd() : -sp.getOriOrd());
            else
                gameDone = turn ? 1 : -1;
        }

        sp = getPiece(row, col);

        //AUTO-PROMO (FUHYO, KYOSHA, KEIMA)
        if (!hasSpace(sp, t_row) || promo)
            board[row][col] = sp.getPromOrd() * (turn ? 1 : -1);


        board[t_row][t_col] = board[row][col];
        board[row][col] = 0;


        if (sp.equals(Koma.OSHO))
            if (turn)
                setSenKing(t_row, t_col);
            else
                setGoKing(t_row, t_col);


        endTurn();
    }

}
