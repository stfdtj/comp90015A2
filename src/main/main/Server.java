package main;

import Whiteboard.*;
import Whiteboard.Utility.Log;
import Whiteboard.Utility.WhiteboardData;

import javax.swing.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

public class Server  {

    public static WhiteboardFunctions service;
    private static WhiteboardData data;


    public static void main(String[] args) throws RemoteException {
        Log.info("Starting Server");
        // true-server; false-client
        boolean identity = Boolean.parseBoolean(args[0]);
        String userName = args[1];
        String boardName = args[2];
        int port = Integer.parseInt(args[3]);
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


        // try to create new one
        // else use existing one
        try {
            LocateRegistry.createRegistry(port);
            Log.info("Try creating registry");
        } catch (ExportException e) {

            LocateRegistry.getRegistry(port);
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

        Client.ConfigUIManager();


        UIManager.put("MenuBar.opaque", true);
        UIManager.put("Menu.opaque", true);
        UIManager.put("MenuItem.opaque", true);
        WhiteboardGUI gui;
        if (data != null) {
            gui = new WhiteboardGUI(identity, userName, boardName, service, data);
        } else {
            gui = new WhiteboardGUI(identity, userName, boardName, service, null);
        }

        ((RemoteService) service).SetCanvas(gui.canvas);
        service.SetWhiteboardGUI(gui);

    }
}
