package Whiteboard;

import Whiteboard.Utility.DrawingInfo;
import Whiteboard.Utility.RemoteUser;
import Whiteboard.Utility.TextInfo;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface UpdateHandler extends Remote {
    void receiveDrawing(DrawingInfo info, TextInfo textInfo) throws RemoteException;
    void receiveCursorUpdate(ArrayList<RemoteUser> users)throws RemoteException;
}
