package game;

import java.util.ArrayList;
import java.util.List;

final class ShogiNode implements Node<ShogiNode>, Comparable<ShogiNode> {

    private final ShogiMove move;
    private final List<Node<ShogiNode>> children = new ArrayList<>();
    private Node<ShogiNode> parent = null;
    private final int value;


    ShogiNode(ShogiMove move, int value) {
        this.move = move;
        this.value = value;
    }

    @Override
    public int getSize() {
        Node<ShogiNode> root = getRoot();
        return getSize(root);
    }

    @Override
    public int getDepth() {
        Node<ShogiNode> root = getRoot();
        return getDepth(root) - 1;
    }

    private int getDepth(Node<ShogiNode> node) {

        if (node == null || node.isEmpty())
            return 1;
        else {
            return getDepth(node.getChildren().get(0)) + 1;
        }
    }

    private int getSize(Node<ShogiNode> node) {
        int size = 1;
        if (node.isEmpty())
            return size;
        else {
            for (Node<ShogiNode> child : node.getChildren()) {
                size += getSize(child);
            }
        }
        return size;
    }

    @Override
    public Node<ShogiNode> getRoot() {
        return parent == null ? this : parent.getRoot();
    }

    @Override
    public Node addChild(ShogiNode child) {
        child.setParent(this);
        children.add(child);
        return child;
    }

    private int getValue() {
        return value;
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public List<Node<ShogiNode>> getChildren() {
        for (Node<ShogiNode> node : children)
            node.setParent(this);
        return children;
    }

    private ShogiNode getData() {
        return this;
    }

    @Override
    public ShogiMove getMove() {
        return move;
    }

    @Override
    public void setParent(Node<ShogiNode> parent) {
        this.parent = parent;
    }

    @Override
    public int compareTo(ShogiNode node) {
        int value = getMove().isDrop() ? getValue() - 100 : getValue();
        int other = node.getData().getMove().isDrop() ? node.getValue() - 100 : node.getValue();

        return Integer.compare(value, other);


    }
}
