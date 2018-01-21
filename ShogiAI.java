package game;


import java.util.List;

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


    private int alphaBeta(int depth, int alpha, int beta) {
        posCount++;

        if (depth == 0 || board.gameOver())
            return evaluator.evalPos(board.turn());

        int maxValue = alpha;
        List<ShogiMove> moves = evaluator.getAllMoves();

        for (ShogiMove move : moves) {

            int value;

            if (moves.get(0).equals(move)) {

                board.move(move);
                value = -alphaBeta(depth - 1, -beta, -maxValue);
                board.undo(move);

            } else {

                board.move(move);
                value = -alphaBeta(depth - 1, -maxValue - 1, -maxValue);
                board.undo(move);

                if (alpha < value && value < beta) {

                    board.move(move);
                    value = -alphaBeta(depth - 1, -beta, -value);
                    board.undo(move);
                }
            }

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

    private int alphaBeta(Node<ShogiBoard> node, int depth, int alpha, int beta, boolean isMaxing) {
        posCount++;

        if (depth == 0 || node.isLeaf() || board.gameOver())
            return evaluator.evalPos(isMaxing);

        int maxValue = alpha;
        evaluator.setBoard(node.getData());

        for (Node<ShogiBoard> child : node.getChildren()) {

            int value = -alphaBeta(child, depth - 1, -beta, -maxValue, !isMaxing);

            if (value > maxValue) {
                maxValue = value;

                if (maxValue > beta) {
                    break;
                }

                if (depth == maxDepth) {
                    System.out.println("New Move!> " + maxValue + " sho");
                }

            }
        }

        return maxValue;
    }


    private <ShogiBoard> Node<ShogiBoard> createTree() {


        Node<ShogiBoard> root = new Node<>((ShogiBoard) board.getShogiBoard());

        expandNode(root);

        return root;
    }

    private <ShogiBoard> void expandNode(Node<ShogiBoard> node) {

        for (ShogiMove move : evaluator.getAllMoves()) {

            board.move(move);
            Node<ShogiBoard> child = new Node<>((ShogiBoard) board.getShogiBoard());
            board.undo(move);
            node.addChild(child);
        }

    }


    private void printTree(Node<Integer> node, String appender) {
        System.out.println(appender + node.getData());
        node.getChildren().forEach(each -> printTree(each, appender + appender));
    }


    final ShogiMove getRandomMove() {
        ShogiMove move = evaluator.getAllMoves()
                .stream()
                .findFirst()
                .get();

        return move;
    }


    final ShogiMove getAlphaBeta() {
        posCount = 0;

        float time = System.nanoTime();
        int value = alphaBeta(maxDepth, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        time = (System.nanoTime() - time) / 1_000_000_000;

        System.out.printf("--------------------\n" +
                        "SPD\t %.2f s\n" +
                        "POS\t %d\n" +
                        "TMP\t %.2f p/s\n" +
                        "Value\t%d sho\n" +
                        "--------------------\n",
                time, posCount, posCount / time, value);

        return myMove;

    }
}