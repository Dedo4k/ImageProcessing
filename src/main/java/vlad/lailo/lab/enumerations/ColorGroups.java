package vlad.lailo.lab.enumerations;

import javafx.scene.paint.Color;

public enum ColorGroups {
    RED(Color.RED),
    GREEN(Color.GREEN),
    BLUE(Color.BLUE),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    PURPLE(Color.PURPLE),
    BROWN(Color.BROWN),
    PINK(Color.PINK),
    GREY(Color.GREY)
    ;

    private final Color color;

    ColorGroups(Color color) {
        this.color = color;
    }

    public static Color get(int index) {
        return ColorGroups.values()[index].color;
    }
}
