package Whiteboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class TextEditor {

    private String currentText = "Sample Text";
    private Color color = Color.BLACK;
    private float size = 12;
    private boolean bold = false;
    private boolean italic = false;
    private boolean underline = false;
    private Font font = new Font("Arial", Font.PLAIN, 12);
    private boolean isEditing = false;
    private Point location;
    private JTextArea textPane;
    private JScrollPane scroll;
    private Canvas canvas;

    public TextEditor(Canvas canvas) {
        this.canvas = canvas;
    }





    public JScrollPane CreateTextBox(Point start, Point end) {
        textPane = new JTextArea();
        int height = (int) Math.abs(start.getY() - end.getY());
        int width = (int) Math.abs(start.getX() - end.getX());
        textPane.setPreferredSize(new Dimension(width, height));
        textPane.setText("Type something...");

        scroll = new JScrollPane(textPane);
        scroll.setBounds(start.x, start.y, width, height);

        scroll.setVisible(true);

        textPane.requestFocusInWindow();

        isEditing = true;
        location = start;

        textPane.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "finishTyping");
        textPane.getActionMap().put("finishTyping", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                FinishTyping();
                canvas.AddShapeLocalRemote(null, null, DrawingMode.TEXT, PackCurrInfo());
                canvas.RemoveTextBox();
            }
        });
        return scroll;
    }

    public void FinishTyping() {
        currentText = textPane.getText();
        scroll.setVisible(false);
        textPane.setVisible(false);
        isEditing = false;
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
    public boolean isEditing() {
        return isEditing;
    }

    public TextInfo PackCurrInfo() {
        return new TextInfo(currentText, color, DrawingMode.TEXT, size, bold, italic, underline, font, location);
    }
}
