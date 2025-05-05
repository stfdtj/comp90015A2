package Whiteboard;

import Whiteboard.Utility.KeyBindingManager;
import Whiteboard.Utility.Log;
import Whiteboard.Utility.TextEditor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class WhiteboardGUI extends JFrame {

    public Canvas canvas;
    public WhiteboardFunctions remoteService;



    public WhiteboardGUI(Boolean identity, String userName, String boardName, WhiteboardFunctions remoteService) {

        Log.action("Creating WhiteboardGUI");

        setTitle(boardName);
        setSize(1920, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        this.remoteService = remoteService;

        try {
            Image icon = ImageIO.read(new File("src/main/Whiteboard/resources/whiteboard_icon.png"));
            this.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }


        JPanel titleBar = new JPanel();
        titleBar.setBackground(new Color(255, 245, 190));
        titleBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("WHITE BOARD NAME");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleBar.add(titleLabel);
        add(titleBar, BorderLayout.NORTH);


        JPanel menuBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuBar.setBackground(new Color(255,182,185,255));
        String[] buttons = {"Exit", "Settings", "Chat", "Help"};
        // some options should not be visible to clients
        if (identity) {
            SetServerPack(menuBar);
        }
        for (String label : buttons) {
            JButton button = new JButton(label);
            SetButtonStyle(button);
            menuBar.add(button);
            menuBar.add(Box.createRigidArea(new Dimension(15, 0)));
        }
        add(menuBar, BorderLayout.NORTH);

        Log.action("Creating Canvas");

        canvas = new Canvas(remoteService, identity, userName);
        JScrollPane canvasScroller = new JScrollPane(canvas);
        add(canvasScroller, BorderLayout.CENTER);



        JPanel canvasContainer = new JPanel(new BorderLayout());
        JPanel toolbarPanel = canvas.createToolbar();
        JPanel thicknessPanel = canvas.createThicknessPanel();

        canvasContainer.add(toolbarPanel, BorderLayout.NORTH);
        canvasContainer.add(canvas.textEditor.CreateTextFormatBar(), BorderLayout.PAGE_END);
        canvasContainer.add(thicknessPanel, BorderLayout.WEST);
        canvasContainer.add(canvas, BorderLayout.CENTER);


        add(canvasContainer, BorderLayout.CENTER);


        setVisible(true);
        revalidate();
    }


    private void SetServerPack(JPanel menuBar) {
        String[] buttons = {"File", "Invite", "Manage Clients"};
        for (String label : buttons) {
            JButton btn = new JButton(label);
            SetButtonStyle(btn);
            menuBar.add(btn);
            menuBar.add(Box.createRigidArea(new Dimension(15, 0)));
        }
    }

    private void SetButtonStyle(JButton button) {
        button.setFocusPainted(false); // no dotted focus border
        button.setBorderPainted(false); // remove border
        button.setContentAreaFilled(false); // no background fill
        button.setOpaque(false); // fully transparent
        button.setForeground(Color.WHITE); // or any color
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHoverEffect(button);

    }

    private void addHoverEffect(JButton button) {
        // button.setForeground(normalColor); // initial color

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setForeground(Color.WHITE);
            }
        });
    }

}
