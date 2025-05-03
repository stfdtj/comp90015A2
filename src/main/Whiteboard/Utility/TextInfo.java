package Whiteboard.Utility;

import Whiteboard.DrawingMode;

import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.Serializable;

import static java.awt.AWTEventMulticaster.add;
import static java.awt.AWTEventMulticaster.remove;

public class TextInfo implements Serializable {

    private String text;
    private Color color;
    public DrawingMode drawingMode;
    private float size;
    private boolean bold = false;
    private boolean italic = false;
    private boolean underline = false;
    private Font font;
    private Point location;

    public TextInfo(String text, Color color, DrawingMode dm, float size, boolean bold, boolean italic,
                    boolean underline, Font font, Point location) {
        this.text = text;
        this.color = color;
        this.drawingMode = dm;
        this.size = size;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
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

    public boolean isUnderline() {
        return underline;
    }
    public Font getFont() {
        return font;
    }
    public Point getLocation() {
        return location;
    }

}
