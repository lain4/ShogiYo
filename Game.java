package game;


import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public final class Game extends HBox {

    private BoardPane grid = new BoardPane();
    private ShogiAI shogAI = new ShogiAI(grid.board);


    public Game() {

        getChildren().addAll(mochigata(), boardBox(), buttonBar());
    }

    private HBox boardBox() {

        HBox outer = new HBox();
        VBox inner = new VBox();

        VBox rowCoords = new VBox();
        rowCoords.setStyle("-fx-background-color: black;" +
                "-fx-spacing: 70;" +
                "-fx-alignment: center-right");

        HBox colCoords = new HBox();
        colCoords.setStyle("-fx-background-color: black;" +
                "-fx-spacing: 65;" +
                "-fx-alignment: center;");


        for (int i = 0; i < 9; i++) {

            Label label = new Label();
            label.setText(String.valueOf(i));
            label.setStyle("-fx-spacing: 30;" +
                    "-fx-font-size: 14;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-style: italic;" +
                    "-fx-font-family: sans-serif;");
            rowCoords.getChildren().add(label);

            Label label2 = new Label();
            label2.setStyle("-fx-spacing: 30;" +
                    "-fx-font-size: 14;" +
                    "-fx-font-weight: bold;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-style: italic;" +
                    "-fx-font-family: sans-serif;");
            label2.setText(String.valueOf((char) (i + 'a')));

            colCoords.getChildren().add(label2);

        }

        inner.getChildren().addAll(grid, colCoords);
        outer.getChildren().addAll(inner, rowCoords);

        return outer;
    }

    private VBox mochigata() {

        VBox outer = new VBox();

        VBox info = new VBox();

        info.getChildren()
                .addAll(grid.getInfo(), controls());

        outer.setStyle("-fx-background-color: black;" +
                "-fx-alignment: center;" +
                "-fx-spacing: 15;");

        outer.getChildren()
                .addAll(grid.getGote(), info, grid.getSente());

        return outer;
    }

    private HBox controls() {

        HBox box = new HBox();
        box.setStyle("-fx-border-style: hidden;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 1");

        Button confirm = new Button();

        Image ok = new Image(getClass().getResourceAsStream("/assets/OK.png"));
        confirm.setGraphic(new ImageView(ok));
        confirm.setStyle("-fx-base: limegreen");

        confirm.disableProperty()
                .bind(grid.hasMoved.not());

        confirm.setOnAction(e -> {
            try {
                grid.execMove();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        });


        Button redo = new Button();

        Image cancel = new Image(getClass().getResourceAsStream("/assets/Cross.png"));
        redo.setGraphic(new ImageView(cancel));
        redo.setStyle("-fx-base: orangered;");

        redo.disableProperty().bind(grid.hasMoved.not());
        redo.setOnAction(e -> grid.redo());


        ToggleButton promo = new ToggleButton();

        Image crown = new Image(getClass().getResourceAsStream("/assets/Star.png"));
        promo.setGraphic(new ImageView(crown));
        promo.setStyle("-fx-base: plum;");
        promo.selectedProperty().bindBidirectional(grid.promoted);

        promo.disableProperty().bind(grid.isPromotable.not());
        promo.setOnAction(e -> {
            grid.promote();
        });


        box.getChildren().addAll(redo, promo, confirm);

        return box;
    }

    private VBox buttonBar() {

        VBox buttons = new VBox();

        Button rotateBoard = new Button("Rotate");
        rotateBoard.setOnAction(e -> grid.flipBoard());


        Button newGame = new Button("New Game");
        newGame.setOnAction(e -> {
            grid.newGame();
            shogAI.init();
        });


        Button randomMove = new Button("Random");
        randomMove.setOnAction(e -> {

            grid.exec(shogAI.getRandomMove());
        });


        Button miniMaxButton = new Button("NAB");
        miniMaxButton.setOnAction(e -> {

            grid.exec(shogAI.getAlphaBeta());

        });


        Button lastMove = new Button("Re:last");
        lastMove.setOnAction(e -> {
            grid.undoLast();
        });


        Button exit = new Button("Exit");
        exit.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });


        buttons.getChildren()
                .addAll(newGame, randomMove, miniMaxButton, lastMove, rotateBoard, exit);


        buttons.getChildren()
                .forEach(e -> {
                    e.setStyle("-fx-base: burlywood;" +
                            "-fx-text-alignment: center;" +
                            "-fx-font-family: sans-serif;" +
                            "-fx-font-weight: 300;" +
                            "-fx-font-size: 16;" +
                            "-fx-border-style: solid;" +
                            "-fx-border-color: black;" +
                            "-fx-pref-height: 50;" +
                            "-fx-pref-width: 200;");
                });


        buttons.setStyle("-fx-background-color: black;");

        return buttons;

    }

}
