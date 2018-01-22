package game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;


public enum Koma implements ShogiPiece {


    OSHO((short) 0b111_1_1_111, false,
            "cat", "K"),                                    // 0 | 14
    HISHA((short) 0b010_1_1_010__010_1_1_010, true,
            "falcon", "T"),                                 // 1
    KAKUGYO((short) 0b101_0_0_101__101_0_0_101, true,
            "octo", "B"),                                   // 2
    GINSHO((short) 0b111_0_0_101, true,
            "penguin", "S"),                                // 3
    KEIMA((short) 0b101_0_0_000_101_0_0_000, true,
            "bee", "N"),                                    // 4
    KYOSHA((short) 0b010_0_0_000__010_0_0_000, true,
            "shrimp", "L"),                                 // 5
    FUHYO((short) 0b010_0_0_000, true,
            "butterfly", "P"),                              // 6


    //T2-PROMOTION
    RYUO((short) 0b010_1_1_010__11111111, false,
            "goat", "+T"),                                  // 7
    RYUMA((short) 0b101_0_0_101__11111111, false,
            "dragon", "+B"),                                // 8


    //KINSHO-PROMOTION
    KINSHO((short) 0b111_1_1_010,
            true, "crab", "G"),                            // 9
    NARIGIN(KINSHO.pattern,
            false, "gorilla", "+S"),                         // 10
    NARIKEI(KINSHO.pattern,
            false, "bear", "+N"),                           // 11
    NARIKYO(KINSHO.pattern,
            false, "snake", "+L"),                          // 12
    TOKIN(KINSHO.pattern,
            false, "bug", "+P");                            // 13


    private final short pattern;
    private final boolean power;
    private final String path;
    private final String symbol;

    Koma(short pattern, boolean power, String path, String symbol) {
        this.pattern = pattern;
        this.power = power;
        this.path = path;
        this.symbol = symbol;
    }

    @Override
    public int[] getDir() {
        List<Integer> list = new ArrayList<>();

        if (equals(Koma.KEIMA))
            return new int[]{5, 7};

        else
            //IF RANGE THEN dir *= 10
            for (int i = 0; i < 8; i++)
                if (canMove(i, false))
                    list.add(i + (canMove(i, true) ? 10 : 0));


        return list.stream()
                .mapToInt(i -> i)
                .toArray();
    }

    @Override
    public ImageView getIcon() {
        Image img = getImage();
        ImageView view = new ImageView(img);

        view.setFitHeight(40);
        view.setFitWidth(40);
        view.setPreserveRatio(true);
        return view;
    }

    @Override
    public int getOriOrd() {

        if (!power)

            switch (this) {
                case RYUO:
                    return 1;
                case RYUMA:
                    return 2;
                case NARIGIN:
                    return 3;
                case NARIKEI:
                    return 4;
                case NARIKYO:
                    return 5;
                case OSHO:
                    return 14;
                default:
                    return 6;
            }

        else
            return ordinal();
    }

    @Override
    public Image getImage() {
        return new Image(getClass().getResourceAsStream("/assets/" + path + "-P.png"));
    }


    @Override
    public boolean canMove(int dir, boolean pow) {

        if (this.equals(Koma.KEIMA)) dir /= 10;

        if (!pow)
            return ((1 << dir) & getPattern()) == 1 << dir;
        else
            return ((1 << dir) &
                    (getPattern() & getPower())) == 1 << dir;
    }

    //ENUM-ORDINAL w/ OSHO == 14
    @Override
    public int getOrd() {
        return ordinal() != 0 ? ordinal() : 14;
    }

    @Override
    public int getPromOrd() {
        if (power)

            switch (this) {

                case HISHA:
                    return 7;
                case KAKUGYO:
                    return 8;
                case GINSHO:
                    return 10;
                case KEIMA:
                    return 11;
                case KYOSHA:
                    return 12;
                default:
                    return 13;
            }

        else
            return ordinal();
    }


    private byte getPower() {
        return (byte) (pattern >> 8);
    }


    private byte getPattern() {
        return (byte) (pattern);
    }

    @Override
    public String getName() {
        return symbol;
    }

    @Override
    public boolean isPromotable() {
        return power;
    }

}
