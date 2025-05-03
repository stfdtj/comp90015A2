package main;



import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

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
    private static JPanel launcher, settings;
    private static Properties props = new Properties();
    private static String path = "src/main/main/resources/config.properties";



    public static void main(String[] args) {
        try (FileReader reader = new FileReader(path)) {
            props.load(reader);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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

        JPanel cards = new JPanel(new CardLayout());

        launcher = CreateLauncherPanel();

        cards.add(launcher, "main");

        settings = CreateSettingsPanel(cards);
        cards.add(settings, "settings");

        buttons.get(3).addActionListener(e -> {
            CardLayout cl = (CardLayout)cards.getLayout();
            cl.show(cards, "settings");
        });
        frame.add(cards);
        frame.setVisible(true);
    }


    private static JPanel CreateLauncherPanel() {
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
        JButton settingBtn = new JButton("Settings");
        buttons.add(createBtn);
        buttons.add(openBtn);
        buttons.add(joinBtn);
        buttons.add(settingBtn);
        Dimension btnSize = new Dimension(250, 30);
        for (JButton button : buttons) {
            SetButtonStyle(button);
            button.setMaximumSize(btnSize);
            panel.add(button);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
        }


        // create new white board
        createBtn.addActionListener(e -> {
            // identity, username, boardname, port
            // Server.main(new String[]{"true", "triss", "board", "8080"});

            String boardName = "blank";
            String port = props.getProperty("rmi.port");
            // dialog window
            Form form = new Form(frame, "Create New Whiteboard");
            JTextField board = new JTextField(25);
            form = form.addRow("Enter board name:", board);
            form = form.addButton("OK", JOptionPane.OK_OPTION);
            int result = form.showDialog();
            if (result == JOptionPane.OK_OPTION) {
                if (!board.getText().equals("")) {
                    boardName = board.getText();
                }

            }

            String name = props.getProperty("user.name");
//            new Thread(() -> {
//                try {
//                    main.Server.main(new String[]{"true", name, boardName, port});
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }).start();
            // try to create a new process
            String classpath = System.getProperty("java.class.path");
            System.out.println(classpath);
            String[] cmd = {
                    "java", "-cp", classpath,
                    "main.Server",
                    "true", name, boardName, port
            };
            System.out.println("New process created");
            try {
                Runtime.getRuntime().exec(cmd);
                frame.setState(Frame.ICONIFIED);
                frame.dispose();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });


        // open existing whiteboard
        openBtn.addActionListener(e -> {
            Form form = new Form(frame, "dialog");
            form.showDialog();
        });


        // join someone as client
        joinBtn.addActionListener(e -> {
            // ip, port, username
            String ip = "";
            String port = "";
            // dialog window
            Form form = new Form(frame, "Join Someone's Whiteboard");
            JTextField ipField = new JTextField(25);
            form = form.addRow("Enter Ip address:", ipField);
            JTextField portField = new JTextField(25);
            form = form.addRow("Enter Port:", portField);
            form = form.addButton("OK", JOptionPane.OK_OPTION);
            int result = form.showDialog();
            if (result == JOptionPane.OK_OPTION) {
                ip = ipField.getText();
                port = portField.getText();
            }


            String username = props.getProperty("user.name");
            System.out.println(username);
            Client.main(new String[]{ip, port, username});
            frame.setState(Frame.ICONIFIED);
            frame.dispose();
        });

        return panel;
    }

    private static JPanel CreateSettingsPanel(JPanel cards) {
        JPanel settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.setBackground(new Color(138,198,209,255));


        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton back = new JButton("Back");
        SetButtonStyle(back);
        back.addActionListener(e -> {
            CardLayout cl = (CardLayout)cards.getLayout();
            cl.show(cards, "main");
        });
        topBar.add(back);
        settingsPanel.add(topBar, BorderLayout.NORTH);

        // settings panel
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("Username:  ");
        JLabel port = new JLabel("Port:  ");
        // load property
        String username = props.getProperty("user.name");
        String portNum = props.getProperty("rmi.port");
        JTextField usernameField = new JTextField(20);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setMaximumSize(usernameField.getPreferredSize());
        JTextField portField = new JTextField(5);
        portField.setAlignmentX(Component.CENTER_ALIGNMENT);
        portField.setMaximumSize(portField.getPreferredSize());


        usernameField.setText(username);
        portField.setText(portNum);
        form.add(name);
        form.add(usernameField);
        form.add(port);
        form.add(portField);

        settingsPanel.add(form, BorderLayout.CENTER);

        JButton save = new JButton("Save");
        bottomBar.add(save);
        SetButtonStyle(save);
        save.addActionListener(e -> {
            String user = usernameField.getText();
            String pNum = portField.getText();
            props.setProperty("user.name", user);
            props.setProperty("rmi.port", pNum);

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(path);
                props.store(out, "saved");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
        settingsPanel.add(bottomBar, BorderLayout.SOUTH);

        // to show the colour of panel
        topBar.setOpaque(false);
        form.setOpaque(false);
        bottomBar.setOpaque(false);

        return settingsPanel;
    }

    private static void SetButtonStyle(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Nunito", Font.PLAIN, 14));

        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBackground(new Color(255,182,185,255));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
    }
}
