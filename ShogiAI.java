package game;


import java.util.List;
import java.util.Random;

final class ShogiAI {


    private final ShogiController board;
    private final Evaluator evaluator;
    private final int maxDepth = 4;
    private ShogiMove myMove = null;


    ShogiAI(ShogiController board) {
        this.board = board;
        evaluator = new Evaluator(board);
    }

    private int evalPos() {

        if (board.gameOver())
            return board.sideWon() && board.turn() ? 1_000_000_000 : -1_000_000_000;

        return (evaluator.getMaterialValue() * 100 + evaluator.getDef() +
                evaluator.getPresence());
    }


    private int alphaBeta(int depth, int alpha, int beta) {

        if (depth == 0 || board.gameOver())
            return evalPos();

        int maxValue = alpha;

        for (ShogiMove move : evaluator.getAllMoves()) {

            board.move(move);
            int value = -alphaBeta(depth - 1, -beta, -maxValue);
            board.undo(move);

            if (value > maxValue) {
                maxValue = value;

                if (maxValue > beta) {
                    break;
                }

                if (depth == maxDepth) {
                    myMove = move;
                    System.out.println("\nNew Move! -> " + maxValue);
                }

            }
        }

        return maxValue;
    }

    final ShogiMove getRandomMove() {
        List<ShogiMove> list = evaluator.getAllMoves();

        return list.get(new Random().nextInt(list.size()));
    }


    final ShogiMove getAlphaBeta() {

        int value = alphaBeta(maxDepth, -Integer.MAX_VALUE, Integer.MAX_VALUE);

        System.out.println(value);
        return myMove;

    }
}