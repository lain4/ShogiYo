package game;


import java.util.List;

interface ShogiBoard {

    int[][] getBoard();

    void addToMochi(int piece);

    void resetMochi();

    List<Integer> getMochi(boolean player);

    List<Integer> getMochi();

    boolean dropExists(int piece);

    boolean hasMochi(boolean turn);


}
