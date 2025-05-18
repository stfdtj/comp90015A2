package Whiteboard.Utility;

import Whiteboard.WhiteboardGUI;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;


public class ChatWindow {

    public JFrame frame;
    private final JTextArea messageDisplay;

    public ChatWindow(String name, WhiteboardGUI whiteboardGUI) {
        frame = new JFrame();
        frame.setTitle("Chat");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setSize(600, 500);


        // component1, display
        messageDisplay = new JTextArea();
        DefaultCaret caret = (DefaultCaret)messageDisplay.getCaret();
        caret.setBlinkRate(0);
        caret.setVisible(false);
        messageDisplay.setEditable(false);
        messageDisplay.setLineWrap(true);

        JScrollPane messageDisplayContainer = new JScrollPane(messageDisplay);


        // component2, toolbar
        JToolBar toolBar = new JToolBar();


        // component3, input area
        JTextArea messageInput = new JTextArea();
        messageInput.setText("Enter message here");
        JScrollPane inputContainer = new JScrollPane(messageInput);


        // component4, send button
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(_ -> {
            String message = messageInput.getText();
            try {
                String sending = name + ": " + message + "\n";
                whiteboardGUI.remoteService.BroadCastMessage(sending);
                Log.info("Sending: " + sending);
            } catch (RemoteException e) {
                Log.error(e.getMessage());
            }
            messageInput.setText("");
        });

        InputMap im = messageInput.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = messageInput.getActionMap();

        // enter to send; shift+enter to add new line
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "send");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK),
                DefaultEditorKit.insertBreakAction);

        am.put("send", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });



        frame.add(messageDisplayContainer);
        frame.add(toolBar);
        frame.add(inputContainer);
        frame.add(sendButton, BorderLayout.EAST);
    }

    public void AppendMessage(String message) {
        messageDisplay.append(message + "\n");
    }

}
