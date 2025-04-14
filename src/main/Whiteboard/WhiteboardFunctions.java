package Whiteboard;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WhiteboardFunctions extends Remote {
    void FreeDraw(DrawingInfo info) throws RemoteException;
    void DrawLine(DrawingInfo info) throws RemoteException;
    void DrawRectangle(DrawingInfo info) throws RemoteException;
    void DrawOval(DrawingInfo info) throws RemoteException;
    void DrawTriangle(DrawingInfo info) throws RemoteException;

}
