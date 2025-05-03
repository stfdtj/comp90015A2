package Whiteboard.Utility;

import Whiteboard.Canvas;
import Whiteboard.DrawingMode;
import Whiteboard.WhiteboardGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class KeyBindingManager {
    public static void bindKeysToCanvas(Canvas canvas) {

        Log.action("Binding Keys to canvas");
        InputMap im = null;
        ActionMap am = null;
        try{
            im = canvas.getInputMap(JComponent.WHEN_FOCUSED);
            am = canvas.getActionMap();
        } catch (RuntimeException e) {
            Log.error(e.getMessage());
        }


        im.put(KeyStroke.getKeyStroke("T"), "switchToTextMode");
        am.put("switchToTextMode", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setDrawingMode(DrawingMode.TEXT);
                 canvas.requestFocusInWindow();
            }
        });

        im.put(KeyStroke.getKeyStroke("E"), "switchToEraser");
        am.put("switchToEraser", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.setDrawingMode(DrawingMode.ERASER);
                canvas.requestFocusInWindow();
            }
        });


    }
}
