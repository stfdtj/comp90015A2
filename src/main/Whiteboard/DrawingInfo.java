package Whiteboard;

import java.awt.*;
import java.io.Serializable;

public class DrawingInfo implements Serializable {
    private Point start;
    private Point end;
    private Color color;
    private String type;
    private DrawingMode drawingMode;
    private float thickness;

    public DrawingInfo(Point start, Point end, Color color, String type, DrawingMode dm, float thickness) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.type = type;
        this.drawingMode = dm;
        this.thickness = thickness;
    }

    public Point getStart() {
        return start;
    }
    public Point getEnd() {
        return end;
    }
    public String getShape() {
        return type;
    }

    public float getThickness() {
        return thickness;
    }

    public DrawingMode getDrawingMode() {
        return drawingMode;
    }
    public Color getColor() { return color; }
}
