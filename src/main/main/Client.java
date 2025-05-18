package main;

import Whiteboard.*;
import Whiteboard.Utility.Log;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.rmi.Naming;

public class Client {

    // true-server; false-client
    private static final boolean identity = false;


    public static void main(String[] args) {
        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        String userName = args[2];
        String boardName = ip + "'s whiteboard";
        ConfigUIManager();
        try {

            String name = "rmi://"+ ip +":"+ port +"/Whiteboard";
            WhiteboardFunctions server = (WhiteboardFunctions) Naming.lookup(name);

            WhiteboardGUI gui = new WhiteboardGUI(identity, userName, boardName, server, null);

            // retry one time, if refused, exit

            UpdateHandler stub = new UpdateListener(gui.canvas);
            stub.SetWhiteboardGUI(gui);
            gui.SetStub(stub);
            if (!server.RegisterClient(stub)) {
                if (stub.NotifyRefuse()) {
                    gui.canvas.TryAddRemoteUser();
                    if (server.RegisterClient(stub)) {
                        System.exit(0);
                    }
                }
            }

        } catch (Exception e) {
            Log.error(e.getMessage());
        }
        Log.info("Client started");
    }

    public static void ConfigUIManager() {
        UIManager.put("MenuBar.background", new Color(255, 182, 185));
        UIManager.put("Menu.background", new Color(255, 182, 185));
        UIManager.put("MenuBar.Foreground", Color.WHITE);
        UIManager.put("Menu.Foreground", Color.WHITE);


        UIManager.put("MenuItem.background", Color.WHITE);

        UIManager.put("MenuItem.selectionForeground", Color.BLACK);
        FontUIResource uiFont = new FontUIResource("Segoe UI", Font.PLAIN, 14);
        for (Object key : UIManager.getLookAndFeelDefaults().keySet()) {
            if (key.toString().toLowerCase().contains("font")) {
                UIManager.put(key, uiFont);
            }
        }
    }
}
