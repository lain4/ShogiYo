package game;

import java.util.List;

public interface Node<T> {

    int getSize();

    int getDepth();

    Node addChild(Node<T> child);

    Node addChild(ShogiNode child);

    void addChildren(List<ShogiNode> children);

    List<Node<T>> getChildren();

    T getData();

    ShogiMove getMove();

    void setMove(ShogiMove move);

    Node<T> getParent();

    void setParent(Node<T> parent);

    int getValue();

    void setValue(int value);

    Node<T> getRoot();

    void deleteNode();

    boolean isEmpty();

    boolean isFirst();

}
