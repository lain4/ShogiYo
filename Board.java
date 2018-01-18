package game;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


final class Board implements ShogiBoard {

    private static final int SIZE = 9;
    private final List<Integer> mochigata = new ArrayList<>();

    private final int[][] board = new int[SIZE][SIZE];

    Board() {
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
    public void addToMochi(int piece) {
        mochigata.add(piece);
    }

    @Override
    public boolean dropExists(int piece) {
        return mochigata.remove((Integer) piece);
    }


    @Override
    public boolean hasMochi(boolean turn) {
        return mochigata.stream()
                .anyMatch(e -> turn ? e > 0 : e < 0);
    }


    @Override
    public void resetMochi() {
        mochigata.clear();
    }

}
