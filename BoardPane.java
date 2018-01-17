package game;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import tools.Tools;


final class BoardPane extends GridPane {


    final ShogiBoard board = new Board();
    final SimpleBooleanProperty hasMoved = new SimpleBooleanProperty(false);
    final SimpleBooleanProperty isPromotable = new SimpleBooleanProperty();
    final SimpleBooleanProperty promoted = new SimpleBooleanProperty(false);
    private final int SIZE = board.getSize();
    private Mochigata senMochi = new Mochigata(true);
    private Mochigata goMochi = new Mochigata(false);
    private FieldPane activePane;
    private ToggleGroup tg = new ToggleGroup();
    private InfoDisplay info = new InfoDisplay();
    private int row;
    private int col;
    private int s_row;
    private int s_col;
    private int t_row;
    private int t_col;


    BoardPane() {
        initBoard();
    }

    final void newGame() {

        setDisable(false);
        board.newPosition();
        senMochi.reset();
        goMochi.reset();
        info.reset();
        redo();
        drawBoard();

    }

    private void initBoard() {

        boolean color = false;
        for (row = 0; row < SIZE; row++) {
            for (col = 0; col < SIZE; col++) {
                FieldPane pane = new FieldPane(tg, color);
                color = !color;

                pane.setOnDragDetected(e -> {

                    if (!hasMoved.get() && isFriend(pane)) {
                        s_row = getRowIndex(pane);
                        s_col = getColumnIndex(pane);

                        activePane = pane;
                        pane.dragDetected(e);
                    }

                    if (board.turn())
                        senMochi.clear();
                    else
                        goMochi.clear();

                    e.consume();
                });

                pane.setOnDragDropped(e -> {

                    t_row = getRowIndex(pane);
                    t_col = getColumnIndex(pane);

                    if (board.isLegal(s_row, s_col, getDirection(), getPower()) && activePane != pane) {

                        pane.dragDrop(e);
                        activePane.setImg(0);
                        activePane = pane;
                        pane.setRotate(board.turn() ? 0 : 180);
                        hasMoved.set(true);
                        isPromotable.set(currentPiece().isPromotable() &&
                                (board.turn() ? t_row < 3 || s_row < 3 : t_row > 5 || s_row > 5));

                    }

                    e.consume();
                });

                pane.setOnMouseClicked(e -> {

                    if (isMochiSelected() &&
                            isEmpty(pane) &&
                            pawnCheck(pane)) {

                        drawBoard();
                        hasMoved.set(true);
                        pane.setImg(currentOrdinal());
                        pane.setRotate(board.turn() ? 0 : 180);
                        t_row = getRowIndex(pane);
                        t_col = getColumnIndex(pane);

                    } else {

                        redo();
                        senMochi.clear();
                        goMochi.clear();
                        drawBoard();

                    }

                    e.consume();

                });

                add(pane, col, row);
            }
        }

        drawBoard();

    }


    final void undoLast() {
        if (board.getLastMove() != null) {
            board.undo(board.getLastMove());

            drawBoard();
            drawMochi();
            redo();
        }
    }

    final InfoDisplay getInfo() {
        return info;
    }

    final void exec(ShogiMove move) {

        int row = move.getRow();
        int col = move.getCol();
        ShogiPiece sp = move.isDrop() ? move.getShogiDrop() : board.getPiece(row, col);

        board.move(move);

        if (move.isDrop())
            info.add(sp, row, col);
        else
            info.add(sp, row, col, move.hasKilled());

        drawBoard();
        drawMochi();
        redo();

        if (move.hasKilled())
            if (board.gameOver())
                showResult();
    }


    private boolean isFriend(FieldPane pane) {
        return board.isFriend(getRowIndex(pane), getColumnIndex(pane));
    }

    private boolean isEmpty(FieldPane pane) {
        return board.isEmpty(getRowIndex(pane), getColumnIndex(pane));
    }

    private boolean pawnCheck(FieldPane pane) {
        return currentOrdinal() != 6 || !board.pawnInCol(getColumnIndex(pane));
    }

    final void redo() {
        promoted.set(false);
        hasMoved.set(false);
        isPromotable.set(false);
        senMochi.setDisable(!board.turn());
        goMochi.setDisable(board.turn());

        drawBoard();
    }

    private ShogiPiece currentPiece() {
        return Tools.getPiece(currentOrdinal());
    }

    private int currentOrdinal() {
        if (isMochiSelected())
            return (board.turn() ?
                    senMochi.getSelection() : goMochi.getSelection())
                    .getOrd();
        else
            return hasMoved.get() ? board.getBoard()[s_row][s_col] : 0;
    }

    private boolean isMochiSelected() {
        return !(board.turn() ? senMochi.isEmpty() : goMochi.isEmpty());
    }

    final void flipBoard() {
        setRotate(getRotate() == 0 ? 180 : 0);
    }

    private int getDirection() {
        ShogiPiece sp = board.getPiece(s_row, s_col);

        if (sp.equals(Koma.KEIMA))
            return (board.turn() ? s_col > t_col : s_col < t_col) ? 7 : 5;

        else if (board.turn() ? (s_row < t_row && s_col < t_col) : (s_row > t_row && s_col > t_col)) {

            if (sp.equals(Koma.KAKUGYO))
                return (Math.abs(s_row - t_row) == Math.abs(s_col - t_col)) ? 0 : -1;
            else
                return 0;
        } else if ((s_col == t_col) && (board.turn() ? (s_row < t_row) : (s_row > t_row)))
            return 1;

        else if (board.turn() ? (s_row < t_row && s_col > t_col) : (s_row > t_row && s_col < t_col)) {

            if (sp.equals(Koma.KAKUGYO))
                return (Math.abs(s_row - t_row) == Math.abs(s_col - t_col)) ? 2 : -1;
            else
                return 2;
        } else if ((s_row == t_row) && (board.turn() ? (s_col < t_col) : (s_col > t_col)))
            return 3;

        else if ((s_row == t_row) && (board.turn() ? (s_col > t_col) : (s_col < t_col)))
            return 4;

        else if (board.turn() ? (s_row > t_row && s_col < t_col) : (s_row < t_row && s_col > t_col)) {
            if (sp.equals(Koma.KAKUGYO))
                return (Math.abs(s_row - t_row) == Math.abs(s_col - t_col)) ? 5 : -1;
            else
                return 5;
        } else if ((s_col == t_col) && (board.turn() ? (s_row > t_row) : (s_row < t_row)))
            return 6;
        else if (board.turn() ? (s_row > t_row && s_col > t_col) : (s_row < t_row && s_col < t_col)) {
            if (sp.equals(Koma.KAKUGYO))
                return (Math.abs(s_row - t_row) == Math.abs(s_col - t_col)) ? 7 : -1;
            else
                return 7;
        } else
            return -1;

    }

    private int getPower() {

        switch (getDirection()) {
            case 0:
            case 1:
                return board.turn() ? t_row - s_row : s_row - t_row;
            case 2:
                return board.turn() ? t_row - s_row : s_row - t_row;
            case 3:
                return board.turn() ? t_col - s_col : s_col - t_col;
            case 4:
                return board.turn() ? s_col - t_col : t_col - s_col;
            default:
                return board.turn() ? s_row - t_row : t_row - s_row;
        }

    }

    final void promote() {
        activePane.setImg(currentPiece().getPromOrd());
    }

    final void execMove() {

        if (isMochiSelected()) {

            info.add(currentPiece(), t_row, t_col);
            board.dropPiece(currentPiece(),
                    t_row,
                    t_col);

        } else {

            boolean isKilling = board.isEnemy(t_row, t_col);
            ShogiPiece sp = board.getPiece(s_row, s_col);

            if (board.turn() ? (s_row < 3 || t_row < 3) : (s_row > 5 || t_row > 5))
                info.add(sp, t_row, t_col, isKilling, promoted.get());
            else
                info.add(sp, t_row, t_col, isKilling);

            board.movePiece(s_row, s_col,
                    getDirection(),
                    getPower(), promoted.get());

            if (isKilling)
                if (board.gameOver())
                    showResult();
        }

        drawMochi();

        redo();
    }

    private void drawMochi() {
        senMochi.draw(board.getMochi(true));
        goMochi.draw(board.getMochi(false));
    }

    final Mochigata getSente() {
        return senMochi;
    }

    final Mochigata getGote() {
        return goMochi;
    }

    private void showResult() {
        setDisable(true);
        senMochi.setDisable(true);
        goMochi.setDisable(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("｡◕ ‿ ◕｡\tThe King has fallen!\t｡◕ ‿ ◕｡");
        alert.setHeaderText(!board.turn() ? "Sente won!" : "Gote won!");
        alert.setContentText("Thank you for playing!\n ");

        alert.showAndWait();
    }

    private void drawBoard() {

        int[][] intBoard = board.getBoard();
        row = 0;
        col = 0;
        tg.selectToggle(null);

        getChildren().stream()
                .filter(e -> e.getClass() == FieldPane.class)
                .map(field -> (FieldPane) field)
                .forEach(f -> {

                    f.setImg(intBoard[row][col++]);

                    if (col == SIZE) {
                        row++;
                        col = 0;
                    }

                });

    }


}
