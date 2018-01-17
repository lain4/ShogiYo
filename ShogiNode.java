package game;

import java.util.ArrayList;
import java.util.List;

final class ShogiNode implements Node<ShogiNode>, Comparable<ShogiNode> {

    private ShogiMove move;
    private List<Node<ShogiNode>> children = new ArrayList<>();
    private Node<ShogiNode> parent = null;
    private int value;


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
    public Node addChild(Node<ShogiNode> child) {
        child.setParent(this);
        children.add(child);
        return child;
    }

    @Override
    public Node<ShogiNode> getRoot() {
        return parent == null ? this : parent.getRoot();
    }

    @Override
    public void deleteNode() {
        if (parent != null) {
            int index = this.parent.getChildren().indexOf(this);
            this.parent.getChildren().remove(this);
            for (Node<ShogiNode> each : getChildren()) {
                each.setParent(this.parent);
            }
            this.parent.getChildren().addAll(index, this.getChildren());
        } else {
            deleteRootNode();
        }
        this.getChildren().clear();
    }

    private Node<ShogiNode> deleteRootNode() {
        if (parent != null) {
            throw new IllegalStateException("deleteRootNode not called on root");
        }
        Node<ShogiNode> newParent = null;
        if (!getChildren().isEmpty()) {
            newParent = getChildren().get(0);
            newParent.setParent(null);
            getChildren().remove(0);
            for (Node<ShogiNode> each : getChildren()) {
                each.setParent(newParent);
            }
            newParent.getChildren().addAll(getChildren());
        }
        this.getChildren().clear();
        return newParent;
    }

    @Override
    public Node addChild(ShogiNode child) {
        child.setParent(this);
        children.add(child);
        return child;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public void addChildren(List<ShogiNode> children) {
        children.forEach(e -> e.setParent(this));
    }

    @Override
    public boolean isFirst() {
        if (getParent() != null)
            return getParent().getChildren().get(0).equals(this);
        else
            return true;
    }

    @Override
    public List<Node<ShogiNode>> getChildren() {
        for (Node<ShogiNode> node : children)
            node.setParent(this);
        return children;
    }

    @Override
    public ShogiNode getData() {
        return this;
    }

    @Override
    public ShogiMove getMove() {
        return move;
    }

    @Override
    public void setMove(ShogiMove move) {
        this.move = move;
    }

    @Override
    public Node<ShogiNode> getParent() {
        return parent;
    }

    @Override
    public void setParent(Node<ShogiNode> parent) {
        this.parent = parent;
    }

    @Override
    public int compareTo(ShogiNode node) {
        int value = getMove().isDrop() ? getValue() - 100 : getValue();
        int other = node.getData().getMove().isDrop() ? node.getValue() - 100 : node.getValue();

        if (value > other)
            return 1;
        else if (value == other)
            return 0;
        else
            return -1;


    }
}
