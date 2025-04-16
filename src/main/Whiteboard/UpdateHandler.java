package Whiteboard;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UpdateHandler extends Remote {
    void receiveDrawing(DrawingInfo info) throws RemoteException;
    void SayHi(String message) throws RemoteException;
}
