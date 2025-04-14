package Whiteboard;

import java.awt.*;

public class ShapeCustom {
    private Shape shape;
    private Color color;

    public ShapeCustom(Shape shape, Color color) {
        this.shape = shape;
        this.color = color;
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }
}
