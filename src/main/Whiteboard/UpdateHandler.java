package Whiteboard;

import Whiteboard.Utility.DrawingInfo;
import Whiteboard.Utility.TextInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UpdateHandler extends Remote {
    void receiveDrawing(DrawingInfo info, TextInfo textInfo) throws RemoteException;
}
