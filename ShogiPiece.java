package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public interface ShogiPiece {

    Image getImage();

    ImageView getIcon();

    boolean isPromotable();

    int[] getDir();

    boolean canMove(int dir, boolean pow);

    int getOrd();

    int getPromOrd();

    int getOriOrd();

    String getName();

}
