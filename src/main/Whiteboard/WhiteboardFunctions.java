package Whiteboard;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WhiteboardFunctions extends Remote {

    void RegisterClient(UpdateHandler client) throws RemoteException;
    void BroadcastDrawing(DrawingInfo info, TextInfo textInfo) throws RemoteException;
    void SendDrawings(DrawingInfo info, TextInfo textInfo) throws RemoteException;
}
