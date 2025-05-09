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

    @Override
    public void receiveDrawing(DrawingInfo info, TextInfo textInfo) throws RemoteException {

        SwingUtilities.invokeLater(() -> {
            canvas.ReceiveRemoteShape(info, textInfo);
        });
    }

    @Override
    public void receiveCursorUpdate(ArrayList<RemoteUser> users) throws RemoteException {
        canvas.setClients(users);
        Log.info("receive cursor called");
    }


    public UpdateListener(Canvas canvas) throws RemoteException {
        super();
        this.canvas = canvas;
    }
}
