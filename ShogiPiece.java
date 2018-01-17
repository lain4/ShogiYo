package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public interface ShogiPiece {

    Image getImage();

    ImageView getIcon();

    boolean isPromotable();

    byte getPattern();

    byte getPower();

    int[] getDir();

    boolean canMove(int dir, boolean pow);

    int getOrd();

    int getPromOrd();

    int getOriOrd();

    ShogiPiece getPromo();

    ShogiPiece getOrigin();

    String getName();

}
