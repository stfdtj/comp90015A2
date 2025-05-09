package main;

import Whiteboard.RemoteService;
import Whiteboard.Utility.Log;
import Whiteboard.Utility.WhiteboardData;
import Whiteboard.WhiteboardFunctions;
import Whiteboard.WhiteboardGUI;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

public class Server  {

    // true-server; false-client
    private static boolean identity;
    private static String userName;
    private static String boardName;
    private static WhiteboardGUI gui;
    private static int port;
    public static WhiteboardFunctions service;
    private static WhiteboardData data;


    public static void main(String args[]) throws RemoteException {
        Log.info("Starting Server");
        identity = Boolean.parseBoolean(args[0]);
        userName = args[1];
        boardName = args[2];
        port = Integer.parseInt(args[3]);
        String filePath = null;
        try {
            filePath = args[4];
        } catch (NullPointerException e) {
            Log.info("File path is null");
        } catch (RuntimeException e) {
            Log.error(e.getMessage());
        }

        if (filePath != null) {
            data = WhiteboardData.LoadData(filePath);
        }
        Log.info("load data called");


        // try create new one
        // else use existing one
        try {
            Registry reg = LocateRegistry.createRegistry(port);
            Log.info("Try creating registry");
        } catch (ExportException e) {
            // registry already exists on 1099 → just look it up
            Registry reg = LocateRegistry.getRegistry(port);
            Log.info("Use existing registry");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        service = new RemoteService();

        try {
            Naming.rebind("Whiteboard", service);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }



        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        Log.info("Server " + boardName + " started at " + ip);

        UIManager.put("MenuBar.background", new Color(255, 182, 185));
        UIManager.put("Menu.background", new Color(255, 182, 185));
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


        UIManager.put("MenuBar.opaque", true);
        UIManager.put("Menu.opaque", true);
        UIManager.put("MenuItem.opaque", true);
        if (data != null) {
            gui = new WhiteboardGUI(identity, userName, boardName, service, data);
        } else {
            gui = new WhiteboardGUI(identity, userName, boardName, service, null);
        }

        ((RemoteService) service).SetCanvas(gui.canvas);


    }
}
