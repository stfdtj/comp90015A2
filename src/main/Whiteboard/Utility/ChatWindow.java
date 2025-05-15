package Whiteboard.Utility;

import Whiteboard.WhiteboardGUI;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
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



        frame.add(messageDisplayContainer);
        frame.add(toolBar);
        frame.add(inputContainer);
        frame.add(sendButton, BorderLayout.EAST);
    }

    public void AppendMessage(String message) {
        messageDisplay.append(message + "\n");
    }

}
