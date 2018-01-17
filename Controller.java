package game;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import tools.Tools;

public class Controller implements ShogiController {

    final SimpleBooleanProperty isPromotable = new SimpleBooleanProperty();
    final SimpleBooleanProperty promoted = new SimpleBooleanProperty(false);
    private ShogiBoard board = new Board();
    private BoardPane boardPane = new BoardPane();
    private InfoDisplay info = new InfoDisplay();
    private FieldPane sourcePane;
    private FieldPane targetPane;
    private Mochigata senMochi = new Mochigata(true);
    private Mochigata goMochi = new Mochigata(false);
    private SimpleBooleanProperty hasMoved = new SimpleBooleanProperty(false);

    public boolean isLegal(int row, int col, int dir, int pow) {
        return board.isLegal(row, col, dir, pow);
    }

    private int currentPiece() {
        if (isMochiSelected())
            return (turn() ?
                    senMochi.getSelection() : goMochi.getSelection())
                    .getOrd();
        else {
            int s_row = GridPane.getRowIndex(sourcePane);
            int s_col = GridPane.getColumnIndex(sourcePane);
            return hasMoved.get() ? board.getBoard()[s_row][s_col] : 0;
        }
    }

    public void execMove() {

        int s_row = GridPane.getRowIndex(sourcePane);
        int s_col = GridPane.getColumnIndex(sourcePane);
        int t_row = GridPane.getRowIndex(targetPane);
        int t_col = GridPane.getColumnIndex(targetPane);

        if (isMochiSelected()) {

            board.dropPiece(board.turn() ? senMochi.getSelection() : goMochi.getSelection(),
                    t_row,
                    t_col);

            info.add(board.getPiece(t_row, t_col), t_row, t_col, !board.turn());

        } else {

            boolean hasKilled = board.isEnemy(t_row, t_col);
            ShogiPiece sp = board.getPiece(s_row, s_col);

               /* if (isPromotable.get())
                    info.add(sp, t_row, t_col, hasKilled, promoted.get(), board.turn());
                else
                    info.add(sp, t_row, t_col, hasKilled, board.turn());*/

            board.movePiece(s_row, s_col,
                    getDirection(),
                    getPower(), promoted.get());

            if (hasKilled)
                if (board.gameOver())
                    showResult();
        }

        drawMochi();

        //redo();
    }

    private void showResult() {
        boardPane.setDisable(true);
        senMochi.setDisable(true);
        goMochi.setDisable(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("<(°.°<)\tThe King has fallen!\t(>°.°)>");
        alert.setHeaderText(turn() ? "Sente won!" : "Gote won!");
        alert.setContentText("Thank you for playing!\n ");

        alert.showAndWait();
    }

    private void drawMochi() {
        senMochi.draw(board.getMochi(true));
        goMochi.draw(board.getMochi(false));
    }

    public void flipBoard() {
        boardPane.setRotate(boardPane.getRotate() == 0 ? 180 : 0);
    }

    private int getPower() {

        int s_row = GridPane.getRowIndex(sourcePane);
        int s_col = GridPane.getColumnIndex(sourcePane);
        int t_row = GridPane.getRowIndex(targetPane);
        int t_col = GridPane.getColumnIndex(targetPane);

        switch (getDirection()) {
            case 0:
            case 1:
            case 2:
                return turn() ? t_row - s_row : s_row - t_row;
            case 3:
                return turn() ? t_col - s_col : s_col - t_col;
            case 4:
                return turn() ? s_col - t_col : t_col - s_col;
            default:
                return turn() ? s_row - t_row : t_row - s_row;
        }


    }

    private int getDirection() {

        int s_row = GridPane.getRowIndex(sourcePane);
        int s_col = GridPane.getColumnIndex(sourcePane);
        int t_row = GridPane.getRowIndex(targetPane);
        int t_col = GridPane.getColumnIndex(targetPane);

        if (Tools.getPiece(board.getBoard()[s_row][s_col]).getOrd() == Koma.KEIMA.getOrd())
            return (turn() ? s_col > t_col : s_col < t_col) ? 7 : 5;

        if (turn() ? (s_row < t_row && s_col < t_col) : (s_row > t_row && s_col > t_col))
            return 0;

        else if ((s_col == t_col) && (turn() ? (s_row < t_row) : (s_row > t_row)))
            return 1;

        else if (turn() ? (s_row < t_row && s_col > t_col) : (s_row > t_row && s_col < t_col))
            return 2;

        else if ((s_row == t_row) && (turn() ? (s_col < t_col) : (s_col > t_col)))
            return 3;

        else if ((s_row == t_row) && (turn() ? (s_col > t_col) : (s_col < t_col)))
            return 4;

        else if (board.turn() ? (s_row > t_row && s_col < t_col) : (s_row < t_row && s_col > t_col))
            return 5;

        else if ((s_col == t_col) && (turn() ? (s_row > t_row) : (s_row < t_row)))
            return 6;

        else
            return 7;

    }

    public boolean isMochiSelected() {
        return !(turn() ? senMochi.isEmpty() : goMochi.isEmpty());
    }

    public boolean isLegalTarget(FieldPane pane) {
        targetPane = pane;
        return sourcePane != targetPane &&
                board.isLegal(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane), getDirection(), getPower());
    }

    public void setTargetPane(FieldPane targetPane) {
        this.targetPane = targetPane;
    }

    public void setSource(FieldPane sourcePane) {
        this.sourcePane = sourcePane;
    }

    @Override
    public boolean isLegal(FieldPane pane) {
        return board.isLegal(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane), getDirection(), getPower());
    }

    @Override
    public boolean hasSpace(int piece, int row) {

        switch (Math.abs(piece)) {
            case 4:
                return (turn() ? row > 1 : row < 7);
            case 5:
            case 6:
                return (turn() ? row != 0 : row != 8);
            default:
                return true;
        }

    }

    @Override
    public boolean pawnInCol(int col) {
        return board.pawnInCol(col);
    }

    @Override
    public void dropPiece(int piece, int row, int col) {
        if (!isEmpty(row, col))
            System.err.println("Field not empty!");
        else if (!hasSpace(piece, row))
            System.err.println("Can't move post-Drop");
        else {

            //PAWN-DROP-RULE
            if (piece == Koma.FUHYO.ordinal() &&
                    pawnInCol(col)
                    ) {

                System.err.println("Only pawn-free columns!");

            } else {


            }
        }
    }

    @Override
    public void movePiece(int row, int col, int dir, int pow, boolean promo) {

    }

    @Override
    public boolean gameOver() {
        return false;
    }

    @Override
    public void newGame() {

    }

    @Override
    public boolean turn() {
        return board.turn();
    }

    @Override
    public boolean isFriend(int row, int col) {
        int piece = board.getBoard()[row][col];
        return turn() ? piece > 0 : piece < 0;
    }

    @Override
    public boolean isFriend(FieldPane pane) {
        return isFriend(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane));
    }

    @Override
    public boolean isEnemy(int row, int col) {
        int piece = board.getBoard()[row][col];
        return turn() ? piece < 0 : piece > 0;
    }

    @Override
    public boolean isEnemy(FieldPane pane) {
        return isEnemy(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane));
    }

    @Override
    public boolean isEmpty(int row, int col) {
        int piece = board.getBoard()[row][col];
        return piece == 0;
    }

    @Override
    public boolean isEmpty(FieldPane pane) {
        return isEmpty(GridPane.getRowIndex(pane), GridPane.getColumnIndex(pane));
    }

    @Override
    public boolean isPromotable(int row, int col) {
        return false;
    }

    @Override
    public boolean isPromotable(FieldPane pane) {
        return false;
    }

}
