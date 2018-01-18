package game;


final class ShogiAI {


    private final Controller board;
    private final Evaluator evaluator;
    private final int maxDepth = 4;
    private ShogiMove myMove = null;


    ShogiAI(Controller board) {
        this.board = board;
        evaluator = new Evaluator(board);
    }

    private int evalPos() {

        return (evaluator.getMaterialValue() * 100 + evaluator.getDef() +
                evaluator.getPresence());
    }


    private int alphaBeta(int depth, int alpha, int beta) {

        if (depth == 0)
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
                    System.out.println("New Move! - " + maxValue);
                }

            }
        }

        return maxValue;
    }


    final ShogiMove getAlphaBeta() {

        int value = alphaBeta(maxDepth, -Integer.MAX_VALUE, Integer.MAX_VALUE);

        System.out.println(value);
        return myMove;

    }
}