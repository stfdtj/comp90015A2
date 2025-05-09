package main;

import Whiteboard.*;
import Whiteboard.Utility.Log;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.rmi.Naming;

public class Client {

    // true-server; false-client
    private static boolean identity = false;
    private static String userName;
    private static String boardName;
    private static WhiteboardGUI gui;
    private static String ip;
    private static int port;


    public static void main(String[] args) {
        ip = args[0];
        port = Integer.parseInt(args[1]);
        userName = args[2];
        boardName = ip + "'s whiteboard";
        UIManager.put("MenuBar.background",        new Color(255, 182, 185));
        UIManager.put("Menu.background",          new Color(255, 182, 185));
        UIManager.put("MenuBar.Foreground", Color.WHITE);
        UIManager.put("Menu.Foreground", Color.WHITE);

//        UIManager.put("Menu.selectionBackground",  new Color(255, 255, 255));
//        UIManager.put("Menu.selectionForeground",  Color.WHITE);

        UIManager.put("MenuItem.background", Color.WHITE);

        UIManager.put("MenuItem.selectionForeground", Color.BLACK);
        FontUIResource uiFont = new FontUIResource("Segoe UI", Font.PLAIN, 14);
        for (Object key : UIManager.getLookAndFeelDefaults().keySet()) {
            if (key.toString().toLowerCase().contains("font")) {
                UIManager.put(key, uiFont);
            }
        }
        try {

            String name = "rmi://"+ip+":"+port+"/Whiteboard";
            WhiteboardFunctions server = (WhiteboardFunctions) Naming.lookup(name);

            gui = new WhiteboardGUI(identity, userName, boardName, server, null);

            // retry one time
            UpdateListener listener = new UpdateListener(gui.canvas);

            UpdateHandler stub = listener;
            stub.SetWhiteboardGUI(gui);
            if (!server.RegisterClient(stub)) {
                if (stub.NotifyRefuse()) {
                    gui.canvas.TryAddRemoteUser();
                    if (server.RegisterClient(stub)) {
                        System.exit(0);
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.info("Client started");
    }
}
