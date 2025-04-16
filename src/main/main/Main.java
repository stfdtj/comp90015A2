package main;



import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*
* colours:
* blue:138,198,209,255
* green: 187,222,214,255
* skin: 243,243,243,255
* pink: 255,182,185,255
* */


public class Main {

    private static JFrame frame;
    private static ArrayList<JButton> buttons = new ArrayList<>();


    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::MainGUI);
    }

    // set up panel
    private static void MainGUI () {

        frame = new JFrame("Welcome");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        try {
            Image icon = ImageIO.read(new File("src/main/main/resources/whiteboard_icon.png"));
            frame.setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome to WhiteBoard", SwingConstants.CENTER);
        title.setFont(new Font("Nunito", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.setBackground(new Color(138,198,209,255));

        // set up buttons
        JButton createBtn = new JButton("Create New Whiteboard");
        JButton openBtn = new JButton("Open Existing Whiteboard");
        JButton joinBtn = new JButton("Join Someone's Whiteboard");
        buttons.add(createBtn);
        buttons.add(openBtn);
        buttons.add(joinBtn);
        Dimension btnSize = new Dimension(250, 30);
        for (JButton button : buttons) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setFont(new Font("Nunito", Font.PLAIN, 14));
            button.setMaximumSize(btnSize);

            button.setFocusPainted(false);
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            button.setBackground(new Color(255,182,185,255));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));

            panel.add(button);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
        }


        frame.add(panel);
        frame.setVisible(true);

        // create new white board
        createBtn.addActionListener(e -> {
            // identity, username, boardname, port
            Server.main(new String[]{"true", "triss", "board", "8080"});
        });


        // open existing whiteboard
        openBtn.addActionListener(e -> {
            System.out.println("Open selected");
            Server.main(new String[]{});
        });


        // join someone as client
        joinBtn.addActionListener(e -> {
            // ip, port, username
            Client.main(new String[]{"192.168.0.24", "1099", "triss"});
        });
    }
}
