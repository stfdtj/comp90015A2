package Whiteboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;

public class TextEditor {

    private String currentText = "";
    private Color color;
    private float size;
    private boolean bold = false;
    private boolean italic = false;
    private boolean underline = false;
    private Font font;
    private Rectangle2D bounds;

    public TextEditor() {}





    public JScrollPane CreateTextBox(Point start, Point end) {
        JTextArea textPane = new JTextArea();
        int height = (int) Math.abs(start.getY() - end.getY());
        int width = (int) Math.abs(start.getX() - end.getX());
        textPane.setPreferredSize(new Dimension(width, height));
        textPane.setText("Type something...");

        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setBounds(start.x, start.y, width, height);

        scroll.setVisible(true);
        scroll.addKeyListener(new KeyAdapter() {

        });

        textPane.requestFocusInWindow();
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    textPane.setVisible(false);
                    scroll.setVisible(false);
                }
            }
        });
        return scroll;
    }





    // getter and setter
    public void SetColor(String color) {
        this.color = Color.decode(color);
    }

    public void SetSize(float size) {
        this.size = size;
    }
    public void SetBold(boolean bold) {
        this.bold = bold;
    }
    public void SetItalic(boolean italic) {
        this.italic = italic;
    }
    public void SetUnderline(boolean underline) {
        this.underline = underline;
    }
    public void SetFont(Font font) {
        this.font = font;
    }
    public Color getColor() {
        return color;
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
    public String getCurrentText() {
        return currentText;
    }
    public Font getFont() {
        return font;
    }
}
