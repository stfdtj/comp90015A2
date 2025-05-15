package main;



import Whiteboard.Utility.Log;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final ArrayList<JButton> buttons = new ArrayList<>();
    private static final Properties props = new Properties();
    private static final String path = "src/main/main/resources/config.properties";



    public static void main(String[] args) {
        try (FileReader reader = new FileReader(path)) {
            props.load(reader);
        } catch (IOException ex) {
            Log.error(ex.getMessage());
        }
        SwingUtilities.invokeLater(Main::MainGUI);
        Log.info("Main started");
    }

    // set up panel
    private static void MainGUI () {

        frame = new JFrame("Welcome");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        try {
            Image icon = ImageIO.read(new File(props.getProperty("app.icon")));
            frame.setIconImage(icon);
        } catch (IOException e) {
            Log.error(e.getMessage());
        }

        JPanel cards = new JPanel(new CardLayout());

        JPanel launcher = CreateLauncherPanel();

        cards.add(launcher, "main");

        JPanel settings = CreateSettingsPanel(cards);
        cards.add(settings, "settings");

        buttons.get(3).addActionListener(_ -> {
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
        createBtn.addActionListener(_ -> CreateNewProgram(frame, props));


        // open existing whiteboard
        openBtn.addActionListener(_ -> OpenNewProgram(frame, props));


        // join someone as client
        joinBtn.addActionListener(_ -> {
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
                String username = props.getProperty("user.name");
                String classpath = System.getProperty("java.class.path");

                String[] cmd = {
                        "java", "-cp", classpath,
                        "main.Client",
                        ip, port, username,
                };
                System.out.println("cmd: " + Arrays.toString(cmd));
                try {
                    Runtime.getRuntime().exec(cmd);
                    frame.setState(Frame.ICONIFIED);
                    frame.dispose();
                } catch (IOException ex) {
                    Log.error(ex.getMessage());
                }

                frame.setState(Frame.ICONIFIED);
                frame.dispose();
            }
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
        back.addActionListener(_ -> {
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
        save.addActionListener(_ -> {
            String user = usernameField.getText();
            String pNum = portField.getText();
            props.setProperty("user.name", user);
            props.setProperty("rmi.port", pNum);

            FileOutputStream out;
            try {
                out = new FileOutputStream(path);
                props.store(out, "saved");
            } catch (IOException ex) {
                Log.error(ex.getMessage());
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

    public static void CreateNewProgram(Frame frame, Properties props) {
        // identity, username, boardname, port

        String boardName = "blank";
        String port = props.getProperty("rmi.port");
        // dialog window
        Form form = new Form(frame, "Create New Whiteboard");
        JTextField board = new JTextField(25);
        form = form.addRow("Enter board name:", board);
        form = form.addButton("OK", JOptionPane.OK_OPTION);
        int result = form.showDialog();
        if (result == JOptionPane.OK_OPTION) {
            if (!board.getText().isEmpty()) {
                boardName = board.getText();
            }
            String name = props.getProperty("user.name");
            // try to create a new process
            String classpath = System.getProperty("java.class.path");
            System.out.println(classpath);
            String[] cmd = {
                    "java", "-cp", classpath,
                    "main.Server",
                    "true", name, boardName, port
            };
            System.out.println("cmd: " + Arrays.toString(cmd));
            try {
                Runtime.getRuntime().exec(cmd);
                frame.setState(Frame.ICONIFIED);
                frame.dispose();
            } catch (IOException ex) {
                Log.error(ex.getMessage());
            }

        }

    }

    public static String getPath() {
        return path;
    }

    public static void OpenNewProgram(Frame frame, Properties props) {
        File savedDir = new File("src/main/main/resources/SavedWhiteBoards");

        String[] jsons = savedDir.isDirectory()
                ? savedDir.list((_, n) -> n.toLowerCase().endsWith(".json"))
                : null;
        if (jsons == null || jsons.length == 0) {
            JOptionPane.showMessageDialog(
                    frame,
                    "No saved whiteboards found.",
                    "Open Whiteboard",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // file selector
        JFileChooser chooser = new JFileChooser(savedDir);
        chooser.setDialogTitle("Select a saved whiteboard");
        chooser.setFileFilter(new FileNameExtensionFilter("Whiteboard JSON Files", "json"));
        Log.info("Main: opened file chooser");
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File chosen = chooser.getSelectedFile();
            // get rid of .json
            String fileName = chosen.getName();
            String boardName = fileName.replaceFirst("\\.json$", "");
            String name = props.getProperty("user.name");
            // try to create a new process
            String classpath = System.getProperty("java.class.path");
            String port = props.getProperty("rmi.port");
            String dataPath = chosen.getParentFile().getAbsolutePath() + "\\" + fileName;
            String[] cmd = {
                    "java", "-cp", classpath,
                    "main.Server",
                    "true", name, boardName, port, dataPath
            };

            try {
                Runtime.getRuntime().exec(cmd);
                frame.setState(Frame.ICONIFIED);
                frame.dispose();
            } catch (IOException ex) {
                Log.error(ex.getMessage());
            }
        }
    }
}
