package game;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ChoiceBoxListCell;

final class InfoDisplay extends ListView<String> {

    private ObservableList<String> list = FXCollections.observableArrayList();
    private int count = 0;
    private boolean side = true;

    InfoDisplay() {

        setStyle("-fx-pref-height: 200;" +
                "-fx-pref-width: 180;" +
                "-fx-font-family: sans-serif;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: black;" +
                "-fx-border-style: solid;" +
                "-fx-border-width: 1;" +
                "-fx-font-size: 12;" +
                "-fx-cell-size: 18;");

        setItems(list);
        setMouseTransparent(true);

        setCellFactory(ChoiceBoxListCell.forListView(list));
    }

    private void addToList(String str) {


        if (!side) {
            list.set(count, list.get(count).concat("|\t|" + str + "|"));
            count++;
        } else
            list.add(count + ". |" + str);

        side = !side;
        scrollTo(count);
    }

    private char getColChar(int col) {
        return (char) (col + 'a');
    }

    final void reset() {
        list.clear();
        count = 0;
        side = true;
    }

    final void add(ShogiPiece sp, int row, int col) {
        String str = (sp.getName() + "*" + getColChar(col) + row);

        addToList(str);
    }

    final void add(ShogiPiece sp, int row, int col, boolean kill) {
        String str = sp.getName() + (kill ? "X" : "_") + getColChar(col) + row;

        addToList(str);
    }

    final void add(ShogiPiece sp, int row, int col, boolean kill, boolean promo) {
        String str = sp.getName() + (kill ? "X" : "_") + getColChar(col) + row + (promo ? "+" : "=");

        addToList(str);
    }

}