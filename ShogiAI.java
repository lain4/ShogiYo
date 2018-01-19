package game;


import java.util.Comparator;

final class ShogiAI {


    private final ShogiController board;
    private final Evaluator evaluator;
    private final int maxDepth = 4;
    private ShogiMove myMove = null;
    private int posCount = 0;


    ShogiAI(ShogiController board) {
        this.board = board;
        evaluator = new Evaluator(board);
    }

    private int evalPos() {

        if (board.gameOver())
            return board.sideWon() && board.turn() ? 1_000_000_000 : -1_000_000_000;

        return (evaluator.getMaterialValue() + evaluator.getDef() +
                evaluator.getPresence());
    }


    private int alphaBeta(int depth, int alpha, int beta) {
        posCount++;

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
                    System.out.println("New Move!> " + maxValue + " sho");
                }

            }
        }

        return maxValue;
    }


    private Node<Integer> createTree() {

        Node<Integer> root = new Node<>(evalPos(), null);

        expandNode(root);

        return root;
    }

    private void expandNode(Node<Integer> node) {

        for (ShogiMove move : evaluator.getAllMoves()) {

            board.move(move);
            Node<Integer> child = new Node<>(evalPos(), move);
            board.undo(move);
            node.addChild(child);
        }

    }


    private void printTree(Node<Integer> node, String appender) {
        System.out.println(appender + node.getData());
        node.getChildren().forEach(each -> printTree(each, appender + appender));
    }


    final ShogiMove getRandomMove() {
        Node<Integer> move = createTree();

        move = move.getChildren()
                .stream()
                .sorted(Comparator.comparingInt(Node::getData))
                .findFirst().get();

        printTree(move, "_");

        return move.getMove();
    }


    final ShogiMove getAlphaBeta() {
        posCount = 0;

        float time = System.nanoTime();
        int value = alphaBeta(maxDepth, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        time = (System.nanoTime() - time) / 1_000_000_000;

        System.out.printf("--------------------\n" +
                        "SPD\t %.2f s\n" +
                        "POS\t %d\nTM" +
                        "P\t %.2f p/s\n" +
                        "Value\t%d sho\n" +
                        "--------------------\n",
                time, posCount, posCount / time, value);

        return myMove;

    }
}