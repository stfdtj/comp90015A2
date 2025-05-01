package Whiteboard;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class KeyBindingManager {
    public static void bindKeysToWhiteboard(WhiteboardGUI gui) {

        JRootPane root = gui.getRootPane();
        Log.action("Binding Keys to Whiteboard");
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke("T"), "switchToTextMode");
        am.put("switchToTextMode", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.canvas.setDrawingMode(DrawingMode.TEXT);
                gui.canvas.requestFocusInWindow();
            }
        });

        im.put(KeyStroke.getKeyStroke("E"), "switchToEraser");
        am.put("switchToEraser", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.canvas.setDrawingMode(DrawingMode.ERASER);
                gui.canvas.requestFocusInWindow();
            }
        });


    }
}
