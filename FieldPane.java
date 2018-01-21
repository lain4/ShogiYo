package game;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import tools.Tools;


final class FieldPane extends ToggleButton {

    private Image img = null;

    FieldPane(ToggleGroup tg, boolean color) {

        setToggleGroup(tg);

        setStyle("-fx-text-fill: black;" +
                "-fx-font-weight: lighter;" +
                "-fx-font-size: 20;" +
                "-fx-font-family: sans-serif;" +
                "-fx-text-alignment: center;" +
                "-fx-alignment: center;" +
                "-fx-border-width: 1;" +
                "-fx-border-color: black;" +
                "-fx-border-image-insets: 0.5;" +
                "-fx-border-style: solid;" +
                "-fx-min-width: 80;" +
                "-fx-min-height: 85;" +
                "-fx-base:" + (color ? "slategrey" : "#ecca8f;"));

        setOnDragOver(this::dragOver);
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

    private void dragOver(DragEvent eve) {

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

    final void setImg(int piece) {

        setRotate(piece < 0 ? 180 : 0);
        img = piece != 0 ? Tools.getShogiImage(piece) : null;
        setGraphic(new ImageView(img));

    }

}