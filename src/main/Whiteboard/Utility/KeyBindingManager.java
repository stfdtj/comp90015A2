package Whiteboard.Utility;

import Whiteboard.Canvas;
import Whiteboard.DrawingMode;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class KeyBindingManager {
    public static void bindKeysToCanvas(Canvas canvas) {

        Log.action("Binding Keys to canvas");
        InputMap im = null;
        ActionMap am = null;
        try{
            im = canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            am = canvas.getActionMap();
        } catch (RuntimeException e) {
            Log.error(e.getMessage());
        }
        int menuMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();



        // text
        KeyStroke textKey = KeyStroke.getKeyStroke(KeyEvent.VK_T, menuMask);

        assert im != null;
        im.put(textKey, "switchToTextMode");
        assert am != null;
        am.put("switchToTextMode", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setDrawingMode(DrawingMode.TEXT);
                 canvas.requestFocusInWindow();
            }
        });

        KeyStroke eraserKey = KeyStroke.getKeyStroke(KeyEvent.VK_E, menuMask);

        im.put(eraserKey, "switchToEraser");
        am.put("switchToEraser", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setDrawingMode(DrawingMode.ERASER);
                canvas.requestFocusInWindow();
            }
        });

        // saving

        KeyStroke saveKey = KeyStroke.getKeyStroke(KeyEvent.VK_S, menuMask);

        im.put(saveKey, "saveBoard");
        am.put("saveBoard", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.Saving(null);
                JOptionPane.showMessageDialog(
                        canvas,
                        "Whiteboard saved.",
                        "Save",
                        JOptionPane.INFORMATION_MESSAGE
                );
                canvas.requestFocusInWindow();
            }
        });


        // undo
        KeyStroke undoKS = KeyStroke.getKeyStroke(KeyEvent.VK_Z, menuMask);
        im.put(undoKS, "undo");
        am.put("undo", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                canvas.Undo();
            }
        });
    }
}
