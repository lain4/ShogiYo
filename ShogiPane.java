package game;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;

abstract class ShogiPane extends Label {

    private Image img;
    private int shogiOrdinal;

    ShogiPane() {
    }

    public ShogiPane(int shogiOrdinal) {
        this.shogiOrdinal = shogiOrdinal;
    }

    abstract int getPiece();

    Image getImage() {
        return img;
    }

    int getShogiOrdinal() {
        return shogiOrdinal;
    }

    final void dragDetected(MouseEvent eve) {

        if (img != null) {

            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(img);
            db.setContent(content);
            eve.consume();
        }
    }

    final void dragOver(DragEvent eve) {

        Dragboard db = eve.getDragboard();

        if (db.hasImage())
            eve.acceptTransferModes(TransferMode.MOVE);

        eve.consume();
    }

    final void dragDrop(DragEvent eve) {

        Dragboard db = eve.getDragboard();

        if (db.hasImage()) {
            setGraphic(new ImageView(db.getImage()));
            eve.setDropCompleted(true);
        } else
            eve.setDropCompleted(false);

        eve.consume();
    }


}
