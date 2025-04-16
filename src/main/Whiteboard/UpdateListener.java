package Whiteboard;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UpdateListener extends UnicastRemoteObject implements UpdateHandler {

    private Canvas canvas;

    @Override
    public void receiveDrawing(DrawingInfo info) throws RemoteException {
        System.out.println("UpdateListener: receiveDrawing() called with info: " + info);
        SwingUtilities.invokeLater(() -> {
            canvas.ReceiveRemoteShape(info);
            System.out.println("UpdateListener: canvas updated with remote shape.");
        });
    }

    @Override
    public void SayHi(String message) throws RemoteException {
        System.out.println("UpdateListener: SayHi() called with message: " + message);
    }

    public UpdateListener(Canvas canvas) throws RemoteException {
        super();
        this.canvas = canvas;
    }
}
