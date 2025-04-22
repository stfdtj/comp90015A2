package Whiteboard;

import java.awt.*;
import java.io.Serializable;

import static java.awt.AWTEventMulticaster.add;
import static java.awt.AWTEventMulticaster.remove;

public class TextInfo implements Serializable {

    public String text;
    public Color color;
    public DrawingMode drawingMode;
    public float size;
    public boolean bold = false;
    public boolean italic = false;
    public boolean underline = false;

    public TextInfo(String text, Color color, DrawingMode dm, float size, boolean bold, boolean italic,
                    boolean underline) {
        this.text = text;
        this.color = color;
        this.drawingMode = dm;
        this.size = size;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
    }



}
