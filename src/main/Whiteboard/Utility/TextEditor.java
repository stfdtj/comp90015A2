package Whiteboard.Utility;

import Whiteboard.Canvas;
import Whiteboard.DrawingMode;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class TextEditor {

    private String currentText = "Sample Text";
    private Color color = Color.BLACK;
    private float size = 12;
    private boolean bold = false;
    private boolean italic = false;
    private Font font = new Font("Arial", Font.PLAIN, 12);
    private Point location;
    private JTextArea textPane;
    private JScrollPane scroll;
    private final Canvas canvas;
    public JToolBar textFormatBar;
    private JComboBox<String> fontFamilyCombo;
    private JComboBox<Integer> fontSizeCombo;

    public TextEditor(Canvas canvas) {
        this.canvas = canvas;
    }

    public JScrollPane CreateTextBox(Point start, Point end) {
        Log.info("Creating TextBox");
        textPane = new JTextArea();
        int height = (int) Math.abs(start.getY() - end.getY());
        int width = (int) Math.abs(start.getX() - end.getX());
        textPane.setPreferredSize(new Dimension(width, height));
        textPane.setText(currentText);

        scroll = new JScrollPane(textPane);
        scroll.setBounds(start.x, start.y, width, height);



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


    public void SetColour(Color color) {
        this.color = color;
        if (textPane != null) {
            textPane.setForeground(color);
        }
    }

    public TextInfo PackCurrInfo() {
        return new TextInfo(currentText, color, DrawingMode.TEXT, size, bold, italic, font, location);
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
        fontFamilyCombo.addActionListener(_ -> {
            this.font = new Font((String)fontFamilyCombo.getSelectedItem(), Font.PLAIN,
                    (Integer)fontSizeCombo.getSelectedItem());
            if (textPane != null) {
                this.font = new Font((String)fontFamilyCombo.getSelectedItem(), Font.PLAIN,
                        (int) size);
                textPane.setFont(font);
            }
        });
        textFormatBar.add(fontFamilyCombo);


        Integer[] sizes = {8,10,12,14,18,24,36,48};
        fontSizeCombo = new JComboBox<>(sizes);
        fontSizeCombo.setSelectedItem(12);
        fontSizeCombo.addActionListener(_ -> {
            this.size = ((Integer)fontSizeCombo.getSelectedItem());
            if (textPane != null) {
                this.font = new Font(this.font.getFontName(), Font.PLAIN,
                        (int) size);
                textPane.setFont(font);

            }
        });
        textFormatBar.add(fontSizeCombo);


        JToggleButton boldBtn = new JToggleButton("B");
        boldBtn.setFont(boldBtn.getFont().deriveFont(Font.BOLD));
        boldBtn.setSize(new Dimension(24, 24));
        boldBtn.addActionListener(_ -> {
            if (textPane != null) {
                if (bold) {
                    bold = false;
                    this.font = new Font(this.font.getFontName(), Font.PLAIN,
                            (int) size);
                    textPane.setFont(font);
                } else {
                    bold = true;
                    this.font = new Font(this.font.getFontName(), Font.BOLD, (int) size);
                    textPane.setFont(font);
                }
            }
        });
        textFormatBar.add(boldBtn);

        JToggleButton italicBtn = new JToggleButton("I");
        italicBtn.setFont(italicBtn.getFont().deriveFont(Font.ITALIC));
        italicBtn.setSize(new Dimension(24, 24));
        italicBtn.addActionListener(_ -> {
            if (textPane != null) {
                if (italic) {
                    italic = false;
                    this.font = new Font(this.font.getFontName(), Font.PLAIN, (int) size);
                    textPane.setFont(font);
                } else {
                    italic = true;
                    this.font = new Font(this.font.getFontName(), Font.ITALIC, (int) size);
                    textPane.setFont(font);
                }
            }
        });
        textFormatBar.add(italicBtn);


        textFormatBar.setBounds(300, 0, 500,  30);
        textFormatBar.setVisible(false);
        return textFormatBar;
    }
}