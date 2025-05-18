package Whiteboard;

import Whiteboard.Utility.ChatWindow;
import Whiteboard.Utility.Log;
import Whiteboard.Utility.RemoteUser;
import Whiteboard.Utility.WhiteboardData;
import main.Form;
import main.Main;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;


public class WhiteboardGUI extends JFrame {

    public Canvas canvas;
    public WhiteboardFunctions remoteService;
    private final JMenuBar menuBar = new JMenuBar();
    private final WhiteboardData data;
    private final Properties props;
    public ChatWindow chatWindow;
    private UpdateHandler stub = null;
    private final String EXPORT_PATH;



    public WhiteboardGUI(Boolean identity, String userName, String boardName, WhiteboardFunctions remoteService,
                         WhiteboardData d) {

        Log.action("Creating WhiteboardGUI");

        setTitle(boardName);
        setSize(1920, 1080);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (identity) {
                        remoteService.NotifyServerShutDown();
                    } else {
                        // check
                        remoteService.UserExit(stub);
                    }
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
                System.exit(0);
            }
        });
        setLayout(new BorderLayout());

        // load or create new board
        if (d != null) {
            this.data = d;
        } else {
            this.data = new WhiteboardData();
            this.data.setBoardName(boardName);

        }

        this.remoteService = remoteService;

        // load property
        props = new Properties();
        try (FileReader reader = new FileReader(Main.getPath())) {
            props.load(reader);
        } catch (IOException ex) {
            Log.error(ex.getMessage());
        }

        // set icon
        try {
            Image icon = ImageIO.read(new File(props.getProperty("app.icon")));
            this.setIconImage(icon);
        } catch (IOException e) {
            Log.error(e.getMessage());
        }

        EXPORT_PATH = props.getProperty("user.export");

        // creating..
        JPanel titleBar = new JPanel();
        titleBar.setBackground(new Color(255, 245, 190));
        titleBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("WHITE BOARD NAME");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleBar.add(titleLabel);
        add(titleBar, BorderLayout.NORTH);



        menuBar.setBackground(new Color(255,182,185,255));
        // some options should not be visible to clients
        if (identity) {
            // component1, server options
            ServerFuncListener();
        }

        // server can quit from file menu
        if (!identity) {
            // component2, client exit
            JMenu exit = new JMenu("Exit");
            JMenuItem exitItem = new JMenuItem("Exit");
            exit.add(exitItem);
            exitItem.addActionListener(_ -> {
                Form form = new Form(this, "Exit");
                form.AddRow("Are you sure you want to exit?", new JLabel());
                form.AddButton("OK", JOptionPane.OK_OPTION)
                        .AddButton("Cancel", JOptionPane.CANCEL_OPTION);
                int result = form.ShowDialog();
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        remoteService.UserExit(stub);
                    } catch (RemoteException ex) {
                        Log.error(ex.getMessage());
                    }
                    System.exit(0);
                }

            });
            menuBar.add(exit);
        }

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

        add(menuBar, BorderLayout.NORTH);

        Log.action("Creating Canvas");

        canvas = new Canvas(remoteService, userName, boardName, data, chatWindow, props, identity);

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


    public boolean NewJoinApplication() {
        Form form = new Form(this, "Application of Join in");
        form.AddRow("A new user wants to join your whiteboard", new JLabel());
        form.AddButton("OK", JOptionPane.OK_OPTION);
        form.AddButton("Cancel", JOptionPane.CANCEL_OPTION);
        int result = form.ShowDialog();
        return result == JOptionPane.OK_OPTION;
    }

    public boolean RefuseNotice() {
        Form form = new Form(this, "Connection refused");
        form.AddRow("The server refused you application, you have one time to resend application, retry?", new JLabel());
        form.AddButton("OK", JOptionPane.OK_OPTION);
        form.AddButton("Cancel", JOptionPane.CANCEL_OPTION);
        int result = form.ShowDialog();
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
            form.AddRow("Save current Whiteboard before creating new one?", new JLabel());
            form.AddButton("OK", JOptionPane.OK_OPTION)
                    .AddButton("Cancel", JOptionPane.CANCEL_OPTION);
            int result = form.ShowDialog();
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


        JMenuItem saveAs = getSaveAs();


        JMenuItem close = new JMenuItem("Close");
        close.addActionListener(_ -> {
            Form form = new Form(this, "Close");
            form.AddRow("Save current Whiteboard before quitting?", new JLabel());
            form.AddButton("OK", JOptionPane.OK_OPTION)
                    .AddButton("Cancel", JOptionPane.CANCEL_OPTION);
            int result = form.ShowDialog();
            if (result == JOptionPane.OK_OPTION) {
                // should check
                canvas.Saving(null);
                // notify all users the server is shutdown
                try {
                    remoteService.NotifyServerShutDown();
                } catch (RemoteException e) {
                    Log.error(e.getMessage());
                }
                System.exit(0);
            } else {
                System.exit(0);
            }
        });

        fileMenu.add(saveAs);
        fileMenu. add(new JToolBar.Separator());
        JMenu exportMenu = new JMenu("Export as..");
        JMenuItem PNG = new JMenuItem("PNG");
        PNG.addActionListener(_ -> ExportImage());
        exportMenu.add(PNG);
        JMenuItem PDF = new JMenuItem("PDF");
        PDF.addActionListener(_ -> SaveAsPDF());
        exportMenu.add(PDF);
        fileMenu.add(exportMenu);
        fileMenu.add(new JToolBar.Separator());
        fileMenu.add(close);
        menuBar.add(fileMenu);

        JMenu manageClients = getManageClients();
        menuBar.add(manageClients);
    }

    private JMenuItem getSaveAs() {
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
            if (result != APPROVE_OPTION) return;

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
                    INFORMATION_MESSAGE
            );
        });
        return saveAs;
    }

    private JMenu getManageClients() {
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
            JTextField ipField = new JTextField(ip);
            JTextField portField = new JTextField(props.getProperty("rmi.port"));
            ipField.setEditable(false);
            ipField.setBorder(null);
            ipField.setOpaque(false);
            portField.setEditable(false);
            portField.setBorder(null);
            portField.setOpaque(false);
            form.AddRow("IP address:", ipField);
            form.AddRow("Port:", portField);
            form.ShowDialog();
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
                    grid.add(new JLabel(user.status, SwingConstants.CENTER));
                    if (user.id != 1) {
                        JButton kick = new JButton("Kick");
                        kick.addActionListener(_ -> {
                            try {
                                remoteService.KickUser(user.id);
                                Log.info("User " + user.id + " kicked");
                                table.dispose();
                            } catch(RemoteException ex) {
                                JOptionPane.showMessageDialog(
                                        table,
                                        "Failed to kick user:\n" + ex.getMessage(),
                                        "Error",
                                        ERROR_MESSAGE
                                );
                            }
                        });
                        grid.add(kick);
                    } else {
                        grid.add(new JSeparator());
                    }
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
        return manageClients;
    }

    public void SetStub(UpdateHandler stub) {
        this.stub = stub;
    }


    private void ExportImage() {
        JFileChooser chooser = new JFileChooser(EXPORT_PATH);
        chooser.setFileFilter(new FileNameExtensionFilter("png".toUpperCase() + " image", "png"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith("." + "png")) {
            f = new File(f.getParentFile(), f.getName() + "." + "png");
        }
        try {
            BufferedImage img = SnapshotCanvas(canvas);
            ImageIO.write(img, "png", f);
            JOptionPane.showMessageDialog(this,
                    "Exported to:\n" + f.getAbsolutePath(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Export failed:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public BufferedImage SnapshotCanvas(Canvas canvas) {
        int w = canvas.getWidth() + canvas.offsetX;
        int h = canvas.getHeight() + canvas.offsetY;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        canvas.paintAll(g2);
        g2.dispose();
        return img;
    }


    public void SaveAsPDF() {
        JFileChooser chooser = new JFileChooser(EXPORT_PATH);
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Document", "pdf"));
        if (chooser.showSaveDialog(this) != APPROVE_OPTION) return;

        File out = chooser.getSelectedFile();
        if (!out.getName().toLowerCase().endsWith(".pdf")) {
            out = new File(out.getParentFile(), out.getName() + ".pdf");
        }

        BufferedImage img = SnapshotCanvas(canvas);
        try (PDDocument doc = new PDDocument()) {
            // create a page sized to your canvas
            PDRectangle rect = new PDRectangle(img.getWidth(), img.getHeight());
            PDPage page = new PDPage(rect);
            doc.addPage(page);

            // embed the image
            PDImageXObject pdImg = LosslessFactory.createFromImage(doc, img);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                // place at (0,0) lower-left
                cs.drawImage(pdImg, 0, 0, img.getWidth(), img.getHeight());
            }

            doc.save(out);
            JOptionPane.showMessageDialog(this,
                    "Exported PDF to:\n" + out.getAbsolutePath(),
                    "Export Successful",
                    INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to write PDF:\n" + ex.getMessage(),
                    "Export Error",
                    ERROR_MESSAGE);
        }
    }

}
