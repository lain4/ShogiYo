package game;

import tools.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


final class ShogiAI {


    private ShogiBoard board;
    private int maxDepth = 4;
    private ShogiMove myMove = null;
    private Node<ShogiNode> root = null;
    /*
        0 - EarlyGame
        1 - MidGame
        2 - Initiative
        3 - EndGame
     */
    private int phase = 0;


    ShogiAI(ShogiBoard board) {
        this.board = board;
    }


    final void init() {
        System.out.println("Initializing GameTree ..");
        //root = createTree();
    }


    private List<ShogiMove> getMovesFor(int row, int col) {

        List<ShogiMove> moves = new ArrayList<>();
        ShogiPiece sp = board.getPiece(row, col);
        boolean promo = sp.isPromotable();
        int victim;

        for (int dir : sp.getDir()) {

            if (dir < 10) {

                int pow = sp.equals(Koma.KEIMA) ? 2 : 1;

                if (board.isLegal(row, col, dir, pow)) {

                    if (sp.equals(Koma.KEIMA))
                        dir *= 10;

                    victim = board.getTarget(row, col, dir, pow);

                    moves.add(new Move(row, col, dir, pow, promo, victim));
                }
            } else {

                dir -= 10;

                int pow = board.getAvailablePath(row, col, dir);

                while (pow > 0) {

                    if (board.isLegal(row, col, dir, pow)) {
                        victim = board.getTarget(row, col, dir, pow);

                        moves.add(new Move(row, col, dir, pow--, promo, victim));
                    }
                }
            }
        }

        return moves;
    }


    private boolean isMate(Node<ShogiNode> node) {
        ShogiMove move = node.getMove();
        return board.isMate(move.getRow(), move.getCol(), move.getDir(), move.getPow());
    }

    private int evalPos() {

        return (board.getMaterialValue() * 100 + board.getDef() +
                board.getPresence());
    }


    private int alphaBeta(int depth, int alpha, int beta) {

        if (depth == 0)
            return evalPos();

        int maxValue = alpha;

        for (ShogiMove move : board.getAllMoves()) {

            board.move(move);
            int value = -alphaBeta(depth - 1, -beta, -maxValue);
            board.undo(move);

            if (value > maxValue) {
                maxValue = value;

                if (maxValue > beta) {
                    break;
                }

                if (depth == maxDepth)
                    myMove = move;

            }
        }

        return maxValue;
    }


    final ShogiMove getAlphaBeta() {

        int value = alphaBeta(maxDepth, -Integer.MAX_VALUE, Integer.MAX_VALUE);

        System.out.println(value);
        return myMove;

    }


    final ShogiMove getRandomMove() {

        System.out.println("---------------------------");
        System.out.println("SIZE: " + root.getSize());
        System.out.println("DEPTH: " + root.getDepth());
        System.out.println("---------------------------");

        Random r = new Random();
        int rand = r.nextInt(root.getChildren().size());

        if (root.getChildren().stream()
                .noneMatch(this::isMate)) {
            root = root.getChildren().get(rand);
            root.setParent(null);
            addLayer(root);
            return root.getMove();

        } else {
            root = root.getChildren()
                    .stream()
                    .filter(this::isMate)
                    .findFirst().get();
            root.setParent(null);
            //addLayer(root);
            return root.getMove();
        }
    }

    private int getValue(ShogiMove move) {
        int value;

        board.move(move);
        board.endTurn();
        value = evalPos();
        board.endTurn();
        board.undo(move);

        return value;
    }


    private <ShogiNode> void updateTree(Node<ShogiNode> node) {
        boolean prev = board.turn();
        if (node.isEmpty()) {
            System.out.println("adding leafs!");
            getAllMoves().forEach(node::addChild);
        } else {
            System.out.println("searching leafs!");
            board.endTurn();
            node.getChildren().forEach(this::updateTree);
            board.endTurn();
        }
        if (prev != board.turn()) board.endTurn();
    }

    private Node<ShogiNode> createTree() {
        ShogiMove first = new Move(2, 2, 6, 1, false, 0);
        Node<ShogiNode> root = new ShogiNode(first, getValue(first));

        while (root.getDepth() < maxDepth)
            addLayer(root);

        System.out.println("DEPTH: " + root.getDepth());
        System.out.println("SIZE: " + root.getSize());

        return root;
    }

    private <ShogiNode> void addLayer(Node<ShogiNode> node) {
        if (node.isEmpty()) {
            board.move(node.getMove());
            getAllMoves().forEach(node::addChild);
            board.undo(node.getMove());
        } else {
            board.move(node.getMove());
            node.getChildren().forEach(this::addLayer);
            board.undo(node.getMove());
        }

    }

    private <ShogiNode> void printTree(Node<ShogiNode> node, String str) {
        System.out.println(str + node.getData() + ": " + node.getValue());
        node.getChildren().forEach(e -> printTree(e, str + str));
    }


    private List<ShogiNode> getAllMoves() {

        List<ShogiNode> list = new ArrayList<>();
        List<ShogiPiece> mochi =
                board.getMochi(board.turn())
                        .stream()
                        .map(Tools::getPiece)
                        .distinct()
                        .collect(Collectors.toList());

        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {

                if (board.isFriend(i, j)) {
                    getMovesFor(i, j).forEach(move -> list.add(new ShogiNode(move, getValue(move))));

                } else if (board.isEmpty(i, j) && mochi != null) {

                    for (ShogiPiece sp : mochi) {
                        if (board.canDrop(sp, i, j)) {
                            ShogiMove move = new Move(sp.getOrd(), i, j);
                            list.add(new ShogiNode(move, getValue(move)));
                        }
                    }

                }
            }
        }

        Collections.sort(list);

        return list;
    }

}
