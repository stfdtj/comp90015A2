package main;

import Whiteboard.RemoteService;
import Whiteboard.Utility.Log;
import Whiteboard.WhiteboardFunctions;
import Whiteboard.WhiteboardGUI;

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


    public static void main(String args[]) throws RemoteException {
        identity = Boolean.parseBoolean(args[0]);
        userName = args[1];
        boardName = args[2];

        // try create new one
        // else use existing one
        try {
            Registry reg = LocateRegistry.createRegistry(1099);
            Log.info("Try creating registry");
        } catch (ExportException e) {
            // registry already exists on 1099 → just look it up
            Registry reg = LocateRegistry.getRegistry(1099);
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

        Log.info("Server started");
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        Log.info("Server started on " + ip);
        gui = new WhiteboardGUI(identity, userName, boardName, service);
        ((RemoteService) service).SetCanvas(gui.canvas);

    }
}
