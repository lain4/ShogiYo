package game;


import java.util.ArrayList;
import java.util.List;


public class Node<T> {

    private T data = null;
    private ShogiMove move;

    private List<Node<T>> children = new ArrayList<>();

    private Node<T> parent = null;

    Node(T data, ShogiMove move) {
        this.data = data;
        this.move = move;
    }

    public ShogiMove getMove() {
        return move;
    }


    public Node<T> addChild(Node<T> child) {
        child.setParent(this);
        children.add(child);
        return child;
    }

    public void addChildren(List<Node<T>> children) {
        children.forEach(this::setParent);
        this.children.addAll(children);
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getParent() {
        return parent;
    }

    private void setParent(Node<T> parent) {
        this.parent = parent;
    }

}
