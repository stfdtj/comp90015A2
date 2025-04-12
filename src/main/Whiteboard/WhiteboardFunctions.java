package Whiteboard;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WhiteboardFunctions extends Remote {
    void drawLine() throws RemoteException;
}
