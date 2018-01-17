package game;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;


final class MochiPane extends ToggleButton {


    MochiPane(ToggleGroup tg) {

        setToggleGroup(tg);
        setStyle("-fx-base: black;");

        autosize();
        visibleProperty().bind(graphicProperty().isNotNull());
    }

    final void setKoma(ShogiPiece sp) {

        setUserData(sp);

        setGraphic(sp != null ? sp.getIcon() : null);

    }

}
