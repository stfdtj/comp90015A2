package main;

import Whiteboard.Canvas;
import Whiteboard.WhiteboardFunctions;
import Whiteboard.WhiteboardGUI;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    // true-server; false-client
    private static boolean identity;
    private static String userName;
    private static String boardName;
    private static WhiteboardGUI gui;

    public Client(boolean identity, String userName, String boardName) {
        this.identity = identity;
        this.userName = userName;
        this.boardName = boardName;

        SwingUtilities.invokeLater(() -> {
            gui = new WhiteboardGUI(identity, userName, boardName);
        });
    }

    public static void main(String[] args) {
        try {
            // Getting the registry
            Registry registry = LocateRegistry.getRegistry(null);

            // Looking up the registry for the remote object
            WhiteboardFunctions stub = (WhiteboardFunctions) registry.lookup("Hello");

            // Calling the remote method using the obtained object


            // System.out.println("Remote method invoked");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
