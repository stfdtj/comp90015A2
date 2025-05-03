package Whiteboard;

import Whiteboard.Utility.DrawingInfo;
import Whiteboard.Utility.TextInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WhiteboardFunctions extends Remote {

    void RegisterClient(UpdateHandler client) throws RemoteException;
    void BroadcastDrawing(DrawingInfo info) throws RemoteException;
    void SendDrawings(DrawingInfo info) throws RemoteException;
    void BroadCastText(TextInfo info) throws RemoteException;
    void SendText(TextInfo info) throws RemoteException;
}
