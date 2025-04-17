package main;

import Whiteboard.*;

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
        try {

            String name = "rmi://"+ip+":"+port+"/Whiteboard";
            WhiteboardFunctions server = (WhiteboardFunctions) Naming.lookup(name);

            gui = new WhiteboardGUI(identity, userName, boardName, server);

            UpdateListener listener = new UpdateListener(gui.canvas);

            UpdateHandler stub = listener;
            server.RegisterClient(stub);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
