package Whiteboard.Utility;

import Whiteboard.DrawingMode;

import java.awt.*;
import java.io.Serializable;

public class TextInfo extends Drawings implements Serializable {

    private final String text;
    private final Color color;
    public DrawingMode drawingMode;
    private final float size;
    private final boolean bold;
    private final boolean italic;
    private final Font font;
    private final Point location;

    public TextInfo(String text, Color color, DrawingMode dm, float size, boolean bold, boolean italic,
                     Font font, Point location) {
        this.text = text;
        this.color = color;
        this.drawingMode = dm;
        this.size = size;
        this.bold = bold;
        this.italic = italic;
        this.font = font;
        this.location = location;
    }



    // getter
    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }

    public DrawingMode getDrawingMode() {
        return drawingMode;
    }

    public float getSize() {
        return size;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public Font getFont() {
        return font;
    }
    public Point getLocation() {
        return location;
    }

}
