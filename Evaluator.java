package game;

import tools.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class Evaluator {

    private final ShogiController con;

    Evaluator(ShogiController con) {
        this.con = con;
    }

    public int evalPos(boolean turn) {

        if (con.gameOver())
            return con.sideWon() && con.turn() ? 1_000_000_000 : -1_000_000_000;

        return (getMaterialValue(turn) + getDef(turn) +
                getPresence(turn));
    }


    private List<ShogiMove> getMovesFor(int row, int col) {

        List<ShogiMove> moves = new ArrayList<>();
        ShogiPiece sp = con.getPiece(row, col);

        int victim;

        for (int dir : Objects.requireNonNull(sp).getDir()) {

            if (dir < 10) {

                if (con.isLegal(row, col, dir, 1)) {

                    if (sp.equals(Koma.KEIMA))
                        dir *= 10;

                    victim = con.getTarget(row, col, dir, 1);

                    moves.add(new Move(row, col, dir, 1, sp.isPromotable(), victim));
                }

            } else {

                dir -= 10;

                int pow = con.getAvailablePath(row, col, dir);

                while (pow > 0) {

                    if (con.isLegal(row, col, dir, pow)) {
                        victim = con.getTarget(row, col, dir, pow);


                        moves.add(new Move(row, col, dir, pow--, sp.isPromotable(), victim));
                    }
                }
            }
        }

        return moves;
    }


    final void setBoard(ShogiBoard sb) {
        con.setShogiBoard(sb);
    }

    final int getPresence(boolean turn) {

        boolean prev = con.turn();

        con.setTurn(turn);

        int value = 0;


        for (int row = 0; row < con.getSize(); row++) {
            for (int col = 0; col < con.getSize(); col++) {

                if (!con.isEmpty(row, col))
                    value += con.isFriend(row, col) ?
                            getPresence(row, col) : -getPresence(row, col);

            }
        }

        con.setTurn(prev);

        return value + (con.hasMochi() ? 5 : -10);
    }


    private int getPresence(int row, int col) {

        int value = 0;
        ShogiPiece sp = con.getPiece(row, col);

        if (sp != null && !sp.equals(Koma.OSHO)) {

            for (int dir : sp.getDir()) {

                if (dir < 10) {

                    int pow = 1;
                    if (sp.equals(Koma.KEIMA)) {
                        dir *= 10;
                        pow = 2;
                    }

                    if (con.isLegal(row, col, dir, pow)) {
                        value += sp.isPromotable() ? 2 : 3;
                    }

                } else {

                    dir -= 10;

                    int pow = 1;

                    while (true) {

                        if (con.isLegal(row, col, dir, pow)) {
                            value += sp.isPromotable() ? 1 : 2;
                        } else
                            break;

                        pow++;
                    }
                }
            }
        }

        return value;
    }


    final List<ShogiMove> getAllMoves() {

        List<ShogiMove> list = new ArrayList<>();

        ShogiPiece[] mochi = con.hasMochi() ? con.getMochi(con.turn())
                .stream()
                .map(Tools::getPiece)
                .distinct()
                .toArray(ShogiPiece[]::new) : null;


        for (int i = 0; i < con.getSize(); i++) {
            for (int j = 0; j < con.getSize(); j++) {

                if (con.isFriend(i, j))
                    list.addAll(getMovesFor(i, j));

                else if (con.isEmpty(i, j) && mochi != null) {

                    for (ShogiPiece sp : mochi) {
                        if (con.canDrop(sp, i, j)) {
                            ShogiMove move = new Move(sp.getOrd(), i, j);
                            list.add(move);
                        }
                    }

                }
            }
        }


        return list.stream()
                .filter(move -> con.isLegal(move.getRow(), move.getCol(), move.getDir(), move.getPow()))
                .sorted()
                .collect(Collectors.toList());
    }


    final int getDef(boolean turn) {

        boolean prev = con.turn();

        con.setTurn(turn);

        int value = 0;

        int kingRow = con.turn() ? con.getSenKingRow() : con.getGoKingRow();
        int kingCol = con.turn() ? con.getSenKingCol() : con.getGoKingCol();
        boolean generalsMoved = true;


        for (int col = 2; col < con.getSize() - 2; col++) {

            ShogiPiece sp = con.getPiece(con.turn() ? 8 : 0, col);

            if (sp != null) {

                if ((sp.equals(Koma.KINSHO) ||
                        sp.equals(Koma.GINSHO))) {

                    generalsMoved = false;
                    value -= 10;
                } else
                    value += 10;

            }

        }


        if (generalsMoved)
            value += Math.abs(kingCol - con.getSize() / 2) * 5;


        for (int row = 0; row < con.getSize(); row++) {
            for (int col = 0; col < con.getSize(); col++) {

                if (!con.isEmpty(row, col)) {

                    ShogiPiece piece = con.getPiece(row, col);

                    if (con.isEnemy(row, col)) {

                        value += (Math.abs(row - kingRow) + Math.abs(col - kingCol)) / 8;

                    } else if (con.isFriend(row, col) &&
                            (row != kingRow && col != kingCol) &&
                            kingRow == (con.turn() ? kingRow - 1 : kingRow + 1))

                        if (Math.abs(kingCol - col) <= 1 &&
                                !Tools.isT2(piece.getOrd()))
                            value += 2;
                }
            }
        }

        con.setTurn(prev);

        return value;
    }


    final int getMaterialValue(boolean turn) {

        boolean prev = con.turn();

        con.setTurn(turn);
        int value = 0;

        for (int row = 0; row < con.getSize(); row++) {
            for (int col = 0; col < con.getSize(); col++) {

                if (con.isFriend(row, col))
                    value += Tools.getValue(con.getPiece(row, col)) * 5;
                else if (con.isEnemy(row, col))
                    value -= Tools.getValue(con.getPiece(row, col)) * 5;
            }
        }


        if (!con.getMochi(con.turn()).isEmpty())
            value += con.getMochi(con.turn())
                    .stream()
                    .mapToInt(Tools::getValue)
                    .distinct()
                    .sum() * 6;
        if (!con.getMochi(!con.turn()).isEmpty())
            value -= con.getMochi(!con.turn())
                    .stream()
                    .mapToInt(Tools::getValue)
                    .distinct()
                    .sum() * 6;

        con.setTurn(prev);

        return value;
    }

}
