package Whiteboard;

import Whiteboard.Utility.Drawings;
import Whiteboard.Utility.RemoteUser;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface UpdateHandler extends Remote {
    void receiveDrawing(Drawings d) throws RemoteException;
    void receiveCursorUpdate(ArrayList<RemoteUser> users)throws RemoteException;
    void receiveMessage(String m) throws RemoteException;
    boolean NotifyRefuse() throws RemoteException;
    void SetWhiteboardGUI(WhiteboardGUI whiteboardGUI) throws RemoteException;
    void NotifyKicked() throws RemoteException;
    void NotifyServerShutDown() throws RemoteException;
}
