package Whiteboard;


import Whiteboard.Utility.Log;
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
import java.util.Properties;


public class WhiteboardGUI extends JFrame {

    public Canvas canvas;
    public WhiteboardFunctions remoteService;
    private JMenuBar menuBar = new JMenuBar();
    private WhiteboardData data;
    private Properties props;



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
            e.printStackTrace();
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
            props = new Properties();
            try (FileReader reader = new FileReader(Main.getPath())) {
                props.load(reader);
            } catch (IOException ex) {
                Log.error(ex.getMessage());
            }

            ServerFuncListener();
        }
        Log.info("Property Loaded");
        JMenu exit = new JMenu("Exit");
        JMenuItem exitItem = new JMenuItem("Exit");
        exit.add(exitItem);
        exitItem.addActionListener(e -> {
            Form form = new Form(this, "Exit");
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
        menuBar.add(exit);

        JMenu settings = new JMenu("Settings");
        menuBar.add(settings);
        JMenu chat = new JMenu("Chat");
        menuBar.add(chat);
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);


        add(menuBar, BorderLayout.NORTH);

        Log.action("Creating Canvas");

        canvas = new Canvas(remoteService, identity, userName, boardName, data);

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


    // functions for server
    // save, manage clients
    private void ServerFuncListener() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem newFile = new JMenuItem("New");
        newFile.addActionListener(e -> {
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
        open.addActionListener(e -> {
            Main.OpenNewProgram(this, props);
        });
        fileMenu.add(open);



        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(e -> {
            // should check
            canvas.Saving(null);
        });
        fileMenu.add(save);



        JMenuItem saveAs = new JMenuItem("Save As...");
        saveAs.addActionListener(e -> {
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

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> {
            Form form = new Form(this, "Exit");
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
        fileMenu.add(exit);
        menuBar.add(fileMenu);


        JMenu invite = new JMenu("Invite");
        menuBar.add(invite);



        JMenu manageClients = new JMenu("Manage Clients");
        menuBar.add(manageClients);
    }

}
