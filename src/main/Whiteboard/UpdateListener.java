package Whiteboard;

import Whiteboard.Utility.DrawingInfo;
import Whiteboard.Utility.Log;
import Whiteboard.Utility.RemoteUser;
import Whiteboard.Utility.TextInfo;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class UpdateListener extends UnicastRemoteObject implements UpdateHandler {

    private Canvas canvas;
    private WhiteboardGUI whiteboardGUI;

    @Override
    public void receiveDrawing(DrawingInfo info, TextInfo textInfo) throws RemoteException {

        SwingUtilities.invokeLater(() -> {
            canvas.ReceiveRemoteShape(info, textInfo);
        });
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


    // not working?
    @Override
    public void NotifyServerShutDown() throws RemoteException {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(whiteboardGUI, "The server has been shut down.");
            System.exit(0);
        });
    }

}
