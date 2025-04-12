package main;

import Whiteboard.Canvas;
import Whiteboard.WhiteboardFunctions;
import Whiteboard.WhiteboardGUI;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends Canvas {

    // true-server; false-client
    private static boolean identity;
    private static String userName;
    private static String boardName;
    private static WhiteboardGUI gui;

    public Server(boolean identity, String userName, String boardName) {
        this.identity = identity;
        this.userName = userName;
        this.boardName = boardName;

        SwingUtilities.invokeLater(() -> {
            gui = new WhiteboardGUI(identity, userName, boardName);
        });
    }

    public static void main(String args[]) {
        try {
            // Instantiating the implementation class
            Canvas canvas = new Canvas();

            // Exporting the object of implementation class
            // (here we are exporting the remote object to the stub)
            WhiteboardFunctions stub = (WhiteboardFunctions) UnicastRemoteObject.exportObject(canvas, 0);

            // Binding the remote object (stub) in the registry
            Registry registry = LocateRegistry.getRegistry();

            registry.bind("Hello", stub);
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
