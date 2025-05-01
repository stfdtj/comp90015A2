package Whiteboard;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UpdateListener extends UnicastRemoteObject implements UpdateHandler {

    private Canvas canvas;

    @Override
    public void receiveDrawing(DrawingInfo info, TextInfo textInfo) throws RemoteException {

        SwingUtilities.invokeLater(() -> {
            canvas.ReceiveRemoteShape(info, textInfo);
        });
    }


    public UpdateListener(Canvas canvas) throws RemoteException {
        super();
        this.canvas = canvas;
    }
}
