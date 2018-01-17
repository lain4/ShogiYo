package game;


import tools.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


final class Board implements ShogiBoard {

    private static final int SIZE = 9;
    private boolean turn = true;
    private List<Integer> mochigata = new ArrayList<>();
    private boolean gameOver = false;
    private boolean checked = false;
    private int senKingRow = 0;
    private int senKingCol = 0;
    private int goKingRow = 0;
    private int goKingCol = 0;
    private int checkRow = 0;
    private int checkCol = 0;

    //ENUM-ORDINAL WITHIN 2D ARRAY
    private int board[][] = new int[SIZE][SIZE];
    private ShogiMove lastMove;

    Board() {
    }

    @Override
    public boolean gameOver() {
        return gameOver;
    }

    @Override
    public ShogiMove getLastMove() {
        return lastMove;
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }


    @Override
    public int getTarget(int row, int col, int dir, int pow) {

        row = getRowFor(row, dir, pow, turn);
        col = getColFor(col, dir, pow, turn);

        return board[row][col];
    }

    private int getRowFor(int row, int dir, int pow, boolean turn) {

        if (dir > 10)
            return row + (turn ? -pow : pow);
        else
            switch (dir) {
                case 0:
                case 1:
                case 2:
                    return row + (turn ? pow : -pow);
                case 5:
                case 6:
                case 7:
                    return row + (turn ? -pow : pow);
                default:
                    return row;
            }

    }

    private int getColFor(int col, int dir, int pow, boolean turn) {

        if (dir > 10)
            return col + ((dir == 50 ? 1 : -1)) * (turn ? 1 : -1);
        else
            switch (dir) {
                case 0:
                case 3:
                case 5:
                    return col + (turn ? pow : -pow);
                case 2:
                case 4:
                case 7:
                    return col + (turn ? -pow : pow);
                default:
                    return col;
            }

    }

    @Override
    public int getDef() {
        int value = 0;
        int kingRow = turn ? senKingRow : goKingRow;
        int kingCol = turn ? senKingCol : goKingCol;


        value += Math.abs(kingCol - SIZE / 2);
        if (kingRow == (turn ? SIZE - 1 : 0))
            value += 15;
        if (kingCol == 0 || kingCol == SIZE - 1)
            value += 15;


        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {

                int piece = board[row][col];

                if (isEnemy(row, col)) {
                    if ((piece == 1 || piece == 5 || piece == 6 || piece == 7) &&
                            kingCol == col)
                        value -= Tools.getValue(piece);
                    else {
                        value++;
                    }

                } else if (isFriend(row, col) && !isOwnKing(row, col)) {

                    if (Tools.areClose(row, col, kingRow, kingCol) &&
                            !Tools.isT2(piece)) {
                        value += Tools.getValue(piece);
                    }
                }
            }
        }

        return value;
    }

    @Override
    public int see(int row, int col) {
        int value = 0;
        ShogiMove move = getSmallestAtk(row, col);

        if (move != null) {
            int piece = Tools.getValue(board[move.getRow()][move.getCol()]);
            move(move);
            value = piece - see(row, col);
            undo(move);
        }

        return value;
    }

    private ShogiMove getSmallestAtk(int row, int col) {
        int piece = Integer.MAX_VALUE;
        ShogiMove move = null;
        endTurn();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (isFriend(i, j) && i != row && j != col &&
                        (Tools.hasRange(board[i][j]) || Tools.areClose(row, col, i, j)) &&
                        Tools.getValue(board[i][j]) < piece) {
                    piece = board[i][j];
                    move = getMovesFor(i, j).stream()
                            .filter(e -> isAttacking(e, row, col))
                            .findFirst()
                            .orElse(null);
                }
            }
        }

        endTurn();

        return move;
    }

    private boolean isAttacking(ShogiMove move, int t_row, int t_col) {

        int row = move.getRow();
        int col = move.getCol();
        int dir = move.getDir();
        int pow = move.getPow();
        if (!isLegal(row, col, (dir > 10 ? dir / 10 : dir), pow) || (row == t_row && col == t_col))
            return false;
        else {

            row = getRowFor(row, dir, pow, turn);
            col = getColFor(col, dir, pow, turn);

            return row == t_row && col == t_col;
        }

    }

    private List<ShogiMove> getMovesFor(int row, int col) {

        List<ShogiMove> moves = new ArrayList<>();
        ShogiPiece sp = getPiece(row, col);

        int victim;

        for (int dir : sp.getDir()) {

            if (dir < 10) {

                if (isLegal(row, col, dir, 1)) {

                    if (sp.equals(Koma.KEIMA))
                        dir *= 10;

                    victim = getTarget(row, col, dir, 1);

                    moves.add(new Move(row, col, dir, 1, sp.isPromotable(), victim));
                }

            } else {

                dir -= 10;

                int pow = getAvailablePath(row, col, dir);

                while (pow > 0) {

                    if (isLegal(row, col, dir, pow)) {
                        victim = getTarget(row, col, dir, pow);


                        moves.add(new Move(row, col, dir, pow--, sp.isPromotable(), victim));
                    }
                }
            }
        }

        return moves;
    }

    @Override
    public boolean isLegal(ShogiMove move) {

        return isLegal(move.getRow(), move.getCol(), move.getDir(), move.getPow());
    }

    @Override
    public List<ShogiMove> getAllMoves() {

        List<ShogiMove> list = new ArrayList<>();

        ShogiPiece[] mochi = hasMochi() ? getMochi(turn)
                .stream()
                .map(Tools::getPiece)
                .distinct()
                .toArray(ShogiPiece[]::new) : null;


        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {

                if (isFriend(i, j)) {
                    list.addAll(getMovesFor(i, j));

                } else if (isEmpty(i, j) && mochi != null) {

                    for (ShogiPiece sp : mochi) {
                        if (canDrop(sp, i, j)) {
                            ShogiMove move = new Move(sp.getOrd(), i, j);
                            list.add(move);
                        }
                    }

                }
            }
        }

        return list;
    }

    @Override
    public boolean canDrop(ShogiPiece sp, int row, int col) {
        return hasSpace(sp, row) &&
                !(sp.equals(Koma.FUHYO) && pawnInCol(col));
    }

    @Override
    public boolean isLegal(int row, int col, int dir, int pow) {

        if (inBounds(row, col) &&
                isFriend(row, col) &&
                getPiece(row, col).canMove(dir, pow > 1)) {

            if (getPiece(row, col).equals(Koma.KEIMA)) {

                row = getRowFor(row, dir, pow, turn);
                col = getColFor(col, dir, pow, turn);
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

        ShogiPiece sp = getPiece(row, col);

        if (sp == null || !isLegal(row, col, dir, pow)) System.out.println("Illegal move!");
        else {

            int newRow = getRowFor(row, dir, pow, turn);
            int newCol = getColFor(col, dir, pow, turn);

            if (turn ? (newRow > 2 && row > 2) : (newRow < 6 && row < 6)) promo = false;

            int victim = board[newRow][newCol];
            lastMove = new Move(row, col, dir, pow, promo, victim);
            System.out.printf("Start: [%d][%d]\nZiel: [%d][%d]\n", row, col, newRow, newCol);
            move(row, col, newRow, newCol, promo);

        }
    }


    @Override
    public void setTurn(boolean turn) {
        this.turn = turn;
    }


    @Override
    public void dropPiece(ShogiPiece sp, int row, int col) {
        if (!isEmpty(row, col) || !canDrop(sp, row, col))
            System.err.println("Illegal drop!");
        else {

            lastMove = new Move(sp.getOrd(), row, col);
            drop(sp.getOrd(), row, col);
            if (isChecking(row, col)) {
                checked = true;
                checkRow = row;
                checkCol = col;
            }

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
    public List<Integer> getMochi(boolean player) {
        if (mochigata.isEmpty())
            return mochigata;
        else
            return mochigata.stream()
                    .filter(i -> player ? i > 0 : i < 0)
                    .sorted()
                    .collect(Collectors.toList());
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    @Override
    public boolean isEnemyKing(int row, int col) {
        return board[row][col] == (turn ? -14 : 14);
    }

    @Override
    public boolean isOwnKing(int row, int col) {
        return board[row][col] == (turn ? 14 : -14);
    }

    @Override
    public boolean turn() {
        return turn;
    }

    @Override
    public void newPosition() {

        turn = true;
        mochigata.clear();
        gameOver = false;
        checked = false;

        for (int row = 0; row < SIZE; row++)
            for (int col = 0; col < SIZE; col++)
                board[row][col] = 0;

        //Pawns
        for (int i = 0; i < SIZE; i++) {
            board[2][i] = -Koma.FUHYO.ordinal();
            board[SIZE - 3][i] = Koma.FUHYO.ordinal();
        }

        //Turm
        board[1][SIZE - 2] = -Koma.KAKUGYO.ordinal();
        board[SIZE - 2][1] = Koma.KAKUGYO.ordinal();

        //Läufer
        board[1][1] = -Koma.HISHA.ordinal();
        board[SIZE - 2][SIZE - 2] = Koma.HISHA.ordinal();

        //König
        board[0][4] = -14;
        goKingCol = 4;
        goKingRow = 0;
        board[SIZE - 1][4] = 14;
        senKingRow = 8;
        senKingCol = 4;

        //Golden
        board[0][3] = board[0][5] = -Koma.KINSHO.ordinal();
        board[SIZE - 1][3] = board[SIZE - 1][5] = Koma.KINSHO.ordinal();

        //Silber
        board[0][2] = board[0][SIZE - 3] = -Koma.GINSHO.ordinal();
        board[SIZE - 1][2] = board[SIZE - 1][SIZE - 3] = Koma.GINSHO.ordinal();

        //Springer
        board[0][1] = board[0][SIZE - 2] = -Koma.KEIMA.ordinal();
        board[SIZE - 1][1] = board[SIZE - 1][SIZE - 2] = Koma.KEIMA.ordinal();

        //Lanzen
        board[0][0] = board[0][SIZE - 1] = -Koma.KYOSHA.ordinal();
        board[SIZE - 1][0] = board[SIZE - 1][SIZE - 1] = Koma.KYOSHA.ordinal();

    }

    @Override
    public boolean pawnInCol(int col) {
        for (int row = 0; row < SIZE; row++)
            if (board[row][col] == (turn ? Koma.FUHYO.ordinal() : -Koma.FUHYO.ordinal()))
                return true;

        return false;
    }

    @Override
    public boolean isPromotable(int row, int col) {
        return isEmpty(row, col) &&
                (turn ? row < 3 : row > 5) &&
                getPiece(row, col).isPromotable();
    }

    @Override
    public int getAvailablePath(int row, int col, int dir) {

        int pow = 0;

        while (true) {
            if (!isLegal(row, col, dir, ++pow))
                return pow - 1;
        }

    }

    private void drop(int piece, int row, int col) {
        piece *= turn ? 1 : -1;

        if (mochigata.remove((Integer) piece))
            board[row][col] = piece;
        else
            System.err.println("Drop not existing WTF?!");

    }

    @Override
    public void endTurn() {
        turn = !turn;
    }


    private boolean isAttacking(int row, int col, int t_row, int t_col) {
        ShogiPiece sp = getPiece(row, col);
        boolean atk = false;
        int newRow;
        int newCol;

        if (sp == null) return false;
        else {
            for (int dir : sp.getDir()) {

                if (dir < 10) {

                    newRow = getRowFor(row, dir, 1, turn);
                    newCol = getColFor(col, dir, 1, turn);

                    return newRow == t_row && newCol == t_col;

                } else {

                    dir -= 10;

                    int pow = getAvailablePath(row, col, dir);

                    while (pow > 0 && !atk) {

                        newRow = getRowFor(row, dir, pow, turn);
                        newCol = getColFor(col, dir, pow, turn);

                        atk = newRow == t_row && newCol == t_col;
                        if (atk) break;
                        pow--;
                    }
                }

            }

            return atk;
        }
    }


    private boolean isPath(int row, int col, int dir, int pow) {

        if (pow == 0)
            return inBounds(row, col) &&
                    !isFriend(row, col);
        else {

            row = getRowFor(row, dir, 1, turn);
            col = getColFor(col, dir, 1, turn);

            return
                    (pow <= 1 || isEmpty(row, col)) &&
                            isPath(row, col, dir, --pow);
        }
    }


    @Override
    public boolean hasSpace(ShogiPiece sp, int row) {

        if (sp == null) return false;
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
    public boolean isMate(int row, int col, int dir, int pow) {

        return isLegal(row, col, dir, pow) &&
                getTarget(row, col, dir, pow) == (turn ? -14 : 14);
    }

    private int getPresence(int row, int col) {
        int value = 0;
        ShogiPiece sp = getPiece(row, col);

        if (sp != null && !sp.equals(Koma.OSHO)) {

            for (int dir : sp.getDir()) {

                if (dir < 10) {

                    int pow = 1;
                    if (sp.equals(Koma.KEIMA)) {
                        dir *= 10;
                        pow = 2;
                    }

                    if (isLegal(row, col, dir, pow)) {
                        value += sp.isPromotable() ? 1 : 2;
                    }

                } else {

                    dir -= 10;

                    int pow = getAvailablePath(row, col, dir);

                    while (pow-- > 0) {
                        if (isLegal(row, col, dir, pow)) {
                            value += sp.isPromotable() ? 1 : 2;
                            ;
                        } else break;
                    }
                }
            }
        }
        return value;
    }

    @Override
    public boolean hasMochi() {
        return mochigata.stream()
                .anyMatch(e -> turn ? e > 0 : e < 0);
    }


    @Override
    public int getMaterialValue() {
        int value = 0;

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (getSide(row, col))
                    value += Tools.getValue(board[row][col]);
                else
                    value -= Tools.getValue(board[row][col]);
            }
        }

        if (!mochigata.isEmpty())
            value += mochigata.stream()
                    .mapToInt(i -> Tools.getValue(i) * (i > 0 ? 1 : -1))
                    .sum() * (turn ? 1 : -1);

        return value;
    }

    private boolean getSide(int row, int col) {
        return turn ? board[row][col] > 0 : board[row][col] < 0;
    }


    @Override
    public int getPresence() {
        int pres = 0;


        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (isFriend(row, col)) {
                    pres += getPresence(row, col);
                }
            }
        }

        return pres;
    }


    @Override
    public void undo(ShogiMove move) {

        if (move.equals(lastMove)) lastMove = null;
        gameOver = false;
        checked = false;

        endTurn();

        if (!move.isDrop()) {

            int victim = move.getVictim();

            int row = getRowFor(move.getRow(), move.getDir(), move.getPow(), turn);
            int col = getColFor(move.getCol(), move.getDir(), move.getPow(), turn);

            int piece = move.isPromoting() ? Tools.getOrigin(board[row][col]) : board[row][col];

            board[move.getRow()][move.getCol()] = piece;
            board[row][col] = victim;

            ShogiPiece ownPiece = Tools.getPiece(piece);
            ShogiPiece enPiece = Tools.getPiece(victim);

            if (ownPiece != null && ownPiece.equals(Koma.OSHO)) {
                if (turn) {
                    senKingCol = move.getCol();
                    senKingRow = move.getRow();
                } else {
                    goKingRow = move.getRow();
                    goKingCol = move.getCol();
                }
            } else if (enPiece != null && enPiece.equals(Koma.OSHO)) {
                if (turn) {
                    goKingCol = row;
                    goKingRow = col;
                } else {
                    senKingRow = row;
                    senKingCol = col;
                }
            }

            if (move.hasKilled())
                mochigata.remove((Integer) (Tools.getOrigin(-victim)));


        } else {
            mochigata.add(move.getDrop() * (turn ? 1 : -1));
            board[move.getRow()][move.getCol()] = 0;
        }

    }

    @Override
    public void move(ShogiMove move) {

        if (move.isDrop())
            dropPiece(move.getShogiDrop(), move.getRow(), move.getCol());
        else
            movePiece(move.getRow(), move.getCol(), move.getDir(), move.getPow(), move.isPromoting());
    }

    private boolean isMate(ShogiMove move) {

        return isMate(move.getRow(), move.getCol(), move.getDir(), move.getPow());
    }

    private boolean isChecking(int row, int col) {
        return getMovesFor(row, col).stream()
                .anyMatch(this::isMate);
    }


    private void move(int row, int col, int t_row, int t_col, boolean promo) {

        ShogiPiece sp = getPiece(row, col);

        if (isEnemy(t_row, t_col)) {
            ShogiPiece enemy = getPiece(t_row, t_col);

            if (!enemy.equals(Koma.OSHO))
                mochigata.add(enemy.getOriOrd() * (turn ? 1 : -1));
            else
                gameOver = true;

        }

        //AUTO-PROMO (FUHYO, KYOSHA, KEIMA)
        if (!hasSpace(sp, t_row) || promo)
            board[row][col] = sp.getPromOrd() * (turn ? 1 : -1);


        board[t_row][t_col] = board[row][col];
        board[row][col] = 0;

        if (isOwnKing(t_row, t_col)) {
            if (turn) {
                senKingRow = t_row;
                senKingCol = t_col;
            } else {
                goKingRow = t_row;
                goKingCol = t_col;
            }
        }
        /*
            else if(isChecking(t_row, t_col)) {
                //System.out.println(see(t_row, t_col));
                checked = true;
                checkRow = t_row;
                checkCol = t_col;
            }
        */

        endTurn();
    }

}
