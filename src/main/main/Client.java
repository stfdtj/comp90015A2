package main;

import Whiteboard.UpdateHandler;
import Whiteboard.UpdateListener;
import Whiteboard.WhiteboardFunctions;
import Whiteboard.WhiteboardGUI;

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
        gui = new WhiteboardGUI(identity, userName, boardName, null);
        try {
            // 1. Connect to RMI server
            String name = "rmi://"+ip+":"+port+"/Whiteboard";
            WhiteboardFunctions server = (WhiteboardFunctions) Naming.lookup(name);

            // 2. Create and register your callback
            UpdateListener listener = new UpdateListener(gui.canvas);
            // If UpdateListener already extends UnicastRemoteObject, do not call exportObject again:
            UpdateHandler stub = listener;  // No extra export call needed
            server.RegisterClient(stub);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
