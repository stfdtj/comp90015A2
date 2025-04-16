package main;

import Whiteboard.RemoteService;
import Whiteboard.WhiteboardGUI;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server  {

    // true-server; false-client
    private static boolean identity;
    private static String userName;
    private static String boardName;
    private static WhiteboardGUI gui;
    private static int port;


    public static void main(String args[]) {
        identity = Boolean.parseBoolean(args[0]);
        userName = args[1];
        boardName = args[2];
        port = Integer.parseInt(args[3]);


        try {
            // 1. Start registry
            LocateRegistry.createRegistry(1099);

            // 2. Create remote object
            RemoteService service = new RemoteService();

            // 3. Bind to registry
            Naming.rebind("Whiteboard", service);

            System.out.println("Server is running...");
            String ip = InetAddress.getLocalHost().getHostAddress();
            System.out.println("My IP: " + ip);
            gui = new WhiteboardGUI(identity, userName, boardName, service);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
