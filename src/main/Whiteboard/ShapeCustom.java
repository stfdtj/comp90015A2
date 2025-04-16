package Whiteboard;

import java.awt.*;

public class ShapeCustom {
    public Shape shape;
    public Color color;
    public DrawingMode drawingMode;
    public String text = "";
    public float thickness;


    public ShapeCustom(Shape shape, Color color, DrawingMode dm, float thickness) {
        this.shape = shape;
        this.color = color;
        this.drawingMode = dm;
        this.thickness = thickness;
    }

}
