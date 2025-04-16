package Whiteboard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

public class WhiteboardGUI extends JFrame {

    public Canvas canvas;



    public WhiteboardGUI(Boolean identity, String userName, String boardName) {

        setTitle(boardName);
        setSize(1920, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        try {
            Image icon = ImageIO.read(new File("src/main/Whiteboard/resources/whiteboard_icon.png"));
            this.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }


        JPanel titleBar = new JPanel();
        titleBar.setBackground(new Color(255, 245, 190)); // light yellow
        titleBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("WHITE BOARD NAME");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleBar.add(titleLabel);
        add(titleBar, BorderLayout.NORTH);


        JPanel menuBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuBar.setBackground(new Color(255,182,185,255));
        String[] buttons = {"Exit", "Settings", "Chat", "Help"};
        // some options should not be visiable to clients
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

        RemoteService remoteService = null;
        try {
            remoteService = new RemoteService();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        canvas = new Canvas(remoteService);

        JPanel canvasContainer = new JPanel(new BorderLayout());
        JPanel toolbarPanel = canvas.createToolbar();            // create toolbar
        JPanel thicknessPanel = canvas.createThicknessPanel();   // create slider

        canvasContainer.add(toolbarPanel, BorderLayout.NORTH);   // toolbar
        canvasContainer.add(thicknessPanel, BorderLayout.WEST);  // thickness
        canvasContainer.add(canvas, BorderLayout.CENTER);        // canvas

        add(canvasContainer, BorderLayout.CENTER);

        setVisible(true);
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
        addHoverEffect(button, Color.BLACK, Color.WHITE);

    }

    private void addHoverEffect(JButton button, Color hoverColor, Color normalColor) {
        // button.setForeground(normalColor); // initial color

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setForeground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setForeground(normalColor);
            }
        });
    }

}
