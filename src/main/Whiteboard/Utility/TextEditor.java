package Whiteboard.Utility;

import Whiteboard.Canvas;
import Whiteboard.DrawingMode;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class TextEditor {

    private String currentText = "Sample Text";
    private Color color = Color.BLACK;
    private float size = 12;
    private boolean bold = false;
    private boolean italic = false;
    private boolean underline = false;
    private Font font = new Font("Arial", Font.PLAIN, 12);
    private Point location;
    private JTextArea textPane;
    private JScrollPane scroll;
    private Canvas canvas;
    public JToolBar textFormatBar;
    private JComboBox<String> fontFamilyCombo;
    private JComboBox<Integer> fontSizeCombo;
    private JToggleButton boldBtn, italicBtn;

    public TextEditor(Canvas canvas) {
        this.canvas = canvas;
    }





    public JScrollPane CreateTextBox(Point start, Point end) {
        Log.info("Creating TextBox");
        textPane = new JTextArea();
        int height = (int) Math.abs(start.getY() - end.getY());
        int width = (int) Math.abs(start.getX() - end.getX());
        textPane.setPreferredSize(new Dimension(width, height));
        textPane.setText("Type something...");

        scroll = new JScrollPane(textPane);
        scroll.setBounds(start.x, start.y, width, height);

        //Log.info("x: "+scroll.getBounds().x + "y: "+scroll.getBounds().y);

        // textPane.requestFocusInWindow();


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

    }



    // getter and setter

    public void SetColour(Color color) {
        this.color = color;
        if (textPane != null) {
            textPane.setForeground(color);
        }
    }

    public TextInfo PackCurrInfo() {
        return new TextInfo(currentText, color, DrawingMode.TEXT, size, bold, italic, underline, font, location);
    }

    public void CleanTextBox() {
        this.textPane = null;
        this.scroll = null;
    }

    public JToolBar CreateTextFormatBar() {
        textFormatBar = new JToolBar();
        textFormatBar.setFloatable(false);
        textFormatBar.setBackground(new Color(243,243,243));
        textFormatBar.setLayout(new FlowLayout(FlowLayout.LEFT));

        String[] fonts =
                java.awt.GraphicsEnvironment
                        .getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames();
        fontFamilyCombo = new JComboBox<>(fonts);
        fontFamilyCombo.setSelectedItem("Arial");
        fontFamilyCombo.addActionListener(e -> {
            this.font = new Font((String)fontFamilyCombo.getSelectedItem(), Font.PLAIN,
                    (Integer)fontSizeCombo.getSelectedItem());
            if (textPane != null) {
                textPane.setFont(font);
            }
        });
        textFormatBar.add(fontFamilyCombo);


        Integer[] sizes = {8,10,12,14,18,24,36,48};
        fontSizeCombo = new JComboBox<>(sizes);
        fontSizeCombo.setSelectedItem(12);
        fontSizeCombo.addActionListener(e -> {
            this.size = ((Integer)fontSizeCombo.getSelectedItem());
            if (textPane != null) {
                textPane.setFont(textPane.getFont().deriveFont(size));

            }
        });
        textFormatBar.add(fontSizeCombo);


        boldBtn = new JToggleButton("B");
        boldBtn.setFont(boldBtn.getFont().deriveFont(Font.BOLD));
        boldBtn.setSize(new Dimension(24, 24));
        boldBtn.addActionListener(e -> {
            if (textPane != null) {
                if (bold) {
                    bold = false;
                    textPane.getFont().deriveFont(Font.PLAIN);
                } else {
                    bold = true;
                    textPane.getFont().deriveFont(Font.BOLD);
                }
            }
        });
        textFormatBar.add(boldBtn);

        italicBtn = new JToggleButton("I");
        italicBtn.setFont(italicBtn.getFont().deriveFont(Font.ITALIC));
        italicBtn.setSize(new Dimension(24, 24));
        italicBtn.addActionListener(e -> {
            if (textPane != null) {
                if (italic) {
                    italic = false;
                    textPane.getFont().deriveFont(Font.PLAIN);
                } else {
                    italic = true;
                    textPane.getFont().deriveFont(Font.ITALIC);

                }
            }
        });
        textFormatBar.add(italicBtn);


        textFormatBar.setBounds(300, 0, 500,  30);
        textFormatBar.setVisible(false);
        return textFormatBar;
    }
}