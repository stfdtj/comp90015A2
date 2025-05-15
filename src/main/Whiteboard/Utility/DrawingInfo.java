package Whiteboard.Utility;

import Whiteboard.DrawingMode;

import java.awt.*;
import java.io.Serializable;

public class DrawingInfo extends Drawings implements Serializable {
    private final Point start;
    private final Point end;
    private final Color color;
    private final DrawingMode drawingMode;
    private final float thickness;


    public DrawingInfo(Point start, Point end, Color color, DrawingMode dm, float thickness) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.drawingMode = dm;
        this.thickness = thickness;
    }

    public Point getStart() {
        return start;
    }
    public Point getEnd() {
        return end;
    }

    public float getThickness() {
        return thickness;
    }

    public DrawingMode getDrawingMode() {
        return drawingMode;
    }
    public Color getColor() { return color; }
}
