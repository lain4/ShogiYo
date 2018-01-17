package game;

import java.util.List;

public interface Node<T> {

    int getSize();

    int getDepth();

    Node addChild(ShogiNode child);

    List<Node<T>> getChildren();

    ShogiMove getMove();

    void setParent(Node<T> parent);

    Node<T> getRoot();

    boolean isEmpty();

}
