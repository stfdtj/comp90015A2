package Whiteboard;

import Whiteboard.Utility.ChatWindow;
import Whiteboard.Utility.Log;
import Whiteboard.Utility.RemoteUser;
import Whiteboard.Utility.WhiteboardData;
import main.Form;
import main.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;


public class WhiteboardGUI extends JFrame {

    public Canvas canvas;
    public WhiteboardFunctions remoteService;
    private final JMenuBar menuBar = new JMenuBar();
    private final WhiteboardData data;
    private final Properties props;
    public ChatWindow chatWindow;



    public WhiteboardGUI(Boolean identity, String userName, String boardName, WhiteboardFunctions remoteService,
                         WhiteboardData d) {

        Log.action("Creating WhiteboardGUI");

        setTitle(boardName);
        setSize(1920, 1080);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        if (d != null) {
            this.data = d;
        } else {
            this.data = new WhiteboardData();
            this.data.setBoardName(boardName);

        }

        this.remoteService = remoteService;


        try {
            Image icon = ImageIO.read(new File("src/main/Whiteboard/resources/whiteboard_icon.png"));
            this.setIconImage(icon);
        } catch (IOException e) {
            Log.error(e.getMessage());
        }

        props = new Properties();
        try (FileReader reader = new FileReader(Main.getPath())) {
            props.load(reader);
        } catch (IOException ex) {
            Log.error(ex.getMessage());
        }


        JPanel titleBar = new JPanel();
        titleBar.setBackground(new Color(255, 245, 190));
        titleBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("WHITE BOARD NAME");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleBar.add(titleLabel);
        add(titleBar, BorderLayout.NORTH);



        menuBar.setBackground(new Color(255,182,185,255));
        // some options should not be visible to clients
        Log.info("Try loading property");
        if (identity) {
            // component1, server options
            ServerFuncListener();
        }
        Log.info("Property Loaded");
        // server can quit from file menu
        if (!identity) {
            // component2, client exit
            JMenu exit = new JMenu("Exit");
            JMenuItem exitItem = new JMenuItem("Exit");
            exit.add(exitItem);
            exitItem.addActionListener(_ -> {
                Form form = new Form(this, "Exit");
                form.addRow("Are you sure you want to exit?", new JLabel());
                form.addButton("OK", JOptionPane.OK_OPTION)
                        .addButton("Cancel", JOptionPane.CANCEL_OPTION);
                int result = form.showDialog();
                if (result == JOptionPane.OK_OPTION) {
                    System.exit(0);
                }

            });
            menuBar.add(exit);
        }


        JMenu settings = new JMenu("Settings");
        menuBar.add(settings);
        JMenu chat = new JMenu("Chat");
        JMenuItem openChat = new JMenuItem("Open Chat");
        openChat.addActionListener(_ -> {
            if (chatWindow == null) {
                chatWindow = new ChatWindow(userName,this);
                canvas.setChatWindow(chatWindow);
                chatWindow.frame.setVisible(true);
            } else {
                chatWindow.frame.setVisible(true);
            }
        });
        chat.add(openChat);
        menuBar.add(chat);



        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);


        add(menuBar, BorderLayout.NORTH);

        Log.action("Creating Canvas");

        canvas = new Canvas(remoteService, identity, userName, boardName, data, chatWindow);

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

    public boolean NewJoinApplication() {
        Form form = new Form(this, "Application of Join in");
        form.addRow("A new user wants to join your whiteboard", new JLabel());
        form.addButton("OK", JOptionPane.OK_OPTION);
        form.addButton("Cancel", JOptionPane.CANCEL_OPTION);
        int result = form.showDialog();
        if (result == JOptionPane.OK_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    public boolean RefuseNotice() {
        Form form = new Form(this, "Connection refused");
        form.addRow("The server refused you application, you have one time to resend application, retry?", new JLabel());
        form.addButton("OK", JOptionPane.OK_OPTION);
        form.addButton("Cancel", JOptionPane.CANCEL_OPTION);
        int result = form.showDialog();
        if (result == JOptionPane.OK_OPTION) {
            return true;
        }
        System.exit(0);
        return false;
    }

    // functions for server
    // save, manage clients
    private void ServerFuncListener() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem newFile = new JMenuItem("New");
        newFile.addActionListener(_ -> {
            Form form = new Form(this, "Exit");
            form.addRow("Save current Whiteboard before creating new one?", new JLabel());
            form.addButton("OK", JOptionPane.OK_OPTION)
                    .addButton("Cancel", JOptionPane.CANCEL_OPTION);
            int result = form.showDialog();
            if (result == JOptionPane.OK_OPTION) {
                // should check
                canvas.Saving(null);
                Main.CreateNewProgram(this, props);
                this.dispose();
            }
        });
        fileMenu.add(newFile);

        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(_ -> Main.OpenNewProgram(this, props));
        fileMenu.add(open);



        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(_ -> {
            // should check
            canvas.Saving(null);
        });
        fileMenu.add(save);



        JMenuItem saveAs = new JMenuItem("Save As...");
        saveAs.addActionListener(_ -> {
            File saveDir = new File("src/main/main/resources/SavedWhiteBoards");
            if (!saveDir.isDirectory()) {
                saveDir.mkdirs();
            }

            JFileChooser chooser = new JFileChooser(saveDir);
            chooser.setDialogTitle("Save Whiteboard As");
            chooser.setFileFilter(
                    new FileNameExtensionFilter("Whiteboard JSON (*.json)", "json")
            );

            int result = chooser.showSaveDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) return;

            File chosen = chooser.getSelectedFile();
            if (!chosen.getName().toLowerCase().endsWith(".json")) {
                chosen = new File(chosen.getParentFile(),
                        chosen.getName() + ".json");
            }
            data.setNotDefault(chosen);
            canvas.Saving(chosen);

            JOptionPane.showMessageDialog(
                    this,
                    "Saved to:\n" + chosen.getAbsolutePath(),
                    "Save As",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // notify user,
        JMenuItem close = new JMenuItem("Close");
        close.addActionListener(_ -> {
            Form form = new Form(this, "Close");
            form.addRow("Save current Whiteboard before quitting?", new JLabel());
            form.addButton("OK", JOptionPane.OK_OPTION)
                    .addButton("Cancel", JOptionPane.CANCEL_OPTION);
            int result = form.showDialog();
            if (result == JOptionPane.OK_OPTION) {
                // should check
                canvas.Saving(null);
                System.exit(0);
            }
        });

        fileMenu.add(saveAs);
        fileMenu.add(new JToolBar.Separator());
        fileMenu.add(close);
        menuBar.add(fileMenu);

        JMenu manageClients = new JMenu("Manage Clients");
        JMenuItem ipAndPort = new JMenuItem("IP and Port");
        ipAndPort.addActionListener(_ -> {
            Form form = new Form(this, "Server runs on");
            String ip = null;
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (IOException e1) {
                Log.error(e1.getMessage());
            }
            form.addRow("IP address:", new JLabel(ip));
            form.addRow("Port:", new JLabel(props.getProperty("rmi.port")));
            form.showDialog();
        });
        manageClients.add(ipAndPort);
        JMenuItem manage = new JMenuItem("Manage Online Clients");
        manage.addActionListener(_ -> {
            JDialog table = new JDialog(this, "Manage Online Clients", true);
            JPanel content = new JPanel(new BorderLayout(5,5));
            Log.info("Clicked");

            try {
                ArrayList<RemoteUser> users = remoteService.getUsers();
                JPanel grid = new JPanel(new GridLayout(users.size()+1, 5, 5, 5));
                grid.add(new JLabel("Username", SwingConstants.CENTER));
                grid.add(new JLabel("ID",       SwingConstants.CENTER));
                grid.add(new JLabel("IP",       SwingConstants.CENTER));
                grid.add(new JLabel("Status",   SwingConstants.CENTER));
                grid.add(new JLabel("Action",   SwingConstants.CENTER));
                for (RemoteUser user: users) {
                    grid.add(new JLabel(user.username, SwingConstants.CENTER));
                    grid.add(new JLabel(String.valueOf(user.id), SwingConstants.CENTER));
                    grid.add(new JLabel(user.ip, SwingConstants.CENTER));
                    grid.add(new JLabel(user.status.toString(), SwingConstants.CENTER));
                    JButton kick = new JButton("Kick");
                    kick.addActionListener(e2 -> {
                        try {
                            remoteService.KickUser(user.id);
                            table.dispose();
                        } catch(RemoteException ex) {
                            JOptionPane.showMessageDialog(
                                    table,
                                    "Failed to kick user:\n" + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    });
                    grid.add(kick);
                }
                content.add(grid, BorderLayout.CENTER);

                JButton c = new JButton("Close");
                c.addActionListener(_ -> table.dispose());
                JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                footer.add(c);
                content.add(footer, BorderLayout.SOUTH);

                table.setContentPane(content);
                table.pack();
                table.setLocationRelativeTo(this);
                table.setVisible(true);

            } catch (RemoteException e) {
                Log.error(e.getMessage());
            }

        });
        manageClients.add(manage);
        menuBar.add(manageClients);
    }

}
