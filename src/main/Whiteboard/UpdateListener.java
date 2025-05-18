package Whiteboard;

import Whiteboard.Utility.*;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class UpdateListener extends UnicastRemoteObject implements UpdateHandler {

    private final Canvas canvas;
    private WhiteboardGUI whiteboardGUI;

    @Override
    public void receiveDrawing(Drawings d) throws RemoteException {

        SwingUtilities.invokeLater(() -> canvas.ReceiveRemoteShape(d));
    }

    @Override
    public void receiveCursorUpdate(ArrayList<RemoteUser> users) throws RemoteException {
        canvas.setClients(users);
    }

    @Override
    public void receiveMessage(String m) throws RemoteException {
        canvas.ReceiveMessage(m);
    }

    @Override
    public boolean NotifyRefuse() throws RemoteException {
        return whiteboardGUI.RefuseNotice();
    }

    @Override
    public void SetWhiteboardGUI(WhiteboardGUI whiteboardGUI) throws RemoteException {
        this.whiteboardGUI = whiteboardGUI;
    }


    public UpdateListener(Canvas canvas) throws RemoteException {
        super();
        this.canvas = canvas;
    }

    @Override
    public void NotifyKicked() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(whiteboardGUI, "You have been kicked by the server.");
            System.exit(0);
        });
    }

    @Override
    public void NotifyServerShutDown() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(whiteboardGUI, "The server has been shut down.");
            System.exit(0);
        });
    }

    @Override
    public void receiveRemoving(String id) throws RemoteException {
        if (canvas == null) {
            Log.error("Canvas is null in receiveRemoving");
            return;
        }
        try {
            canvas.ReceiveRemoving(id);
        } catch (Exception e) {
            Log.error("Exception inside canvas.ReceiveRemoving: " + e.getMessage());
        }
    }

}
