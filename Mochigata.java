package game;


import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import tools.Tools;

import java.util.List;


final class Mochigata extends GridPane {

    private final ToggleGroup tg = new ToggleGroup();

    Mochigata(boolean player) {

        for (int row = 0; row < 5; row++)
            for (int col = 0; col < 3; col++) {
                MochiPane mp = new MochiPane(tg);
                add(mp, col, row);
            }

        setStyle("-fx-base: black;" +
                "-fx-min-width: 170;" +
                "-fx-spacing: 0;" +
                "-fx-hgap: 0;" +
                "-fx-vgap: 0;" +
                "-fx-rotate: " + (player ? "0" : "180"));
    }

    final void reset() {
        clear();
        getChildren().stream()
                .map(m -> (MochiPane) m)
                .forEach(e -> e.setKoma(null));
    }

    final void clear() {
        tg.getToggles()
                .forEach(e -> e.setSelected(false));
    }

    final boolean isEmpty() {
        return tg.getSelectedToggle() == null || tg.getSelectedToggle().getUserData() == null;
    }

    final ShogiPiece getSelection() {
        return (ShogiPiece) tg.getSelectedToggle()
                .getUserData();
    }

    final void draw(List<Integer> mochi) {

        clear();

        getChildren().stream()
                .filter(e -> e.getClass() == MochiPane.class)
                .map(f -> (MochiPane) f)
                .limit(mochi.size() + 1)
                .forEach(field -> {
                    int x = mochi.isEmpty() ? 0 : mochi.remove(mochi.size() - 1);
                    field.setKoma(Tools.getPiece(x));
                });

    }

}
