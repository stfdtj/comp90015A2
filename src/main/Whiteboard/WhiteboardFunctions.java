package Whiteboard;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WhiteboardFunctions extends Remote {

    void RegisterClient(UpdateHandler client) throws RemoteException;
    void BroadcastDrawing(DrawingInfo info) throws RemoteException;
}
