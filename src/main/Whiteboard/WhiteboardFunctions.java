package Whiteboard;

import Whiteboard.Utility.Drawings;
import Whiteboard.Utility.RemoteUser;
import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface WhiteboardFunctions extends Remote {

    boolean RegisterClient(UpdateHandler client) throws RemoteException;
    void BroadcastDrawing(Drawings d) throws RemoteException;
    void AddRemoteUser(RemoteUser user) throws RemoteException;
    ArrayList<RemoteUser> getUsers() throws RemoteException;
    void UpdateCursor(Point p, int id) throws RemoteException;
    int GetNumUsers() throws RemoteException;
    void BroadCastMessage(String m) throws RemoteException;
    void SetWhiteboardGUI(WhiteboardGUI gui) throws RemoteException;
    void RemoveLastUser() throws RemoteException;
    void KickUser(int id) throws RemoteException;
    void NotifyServerShutDown() throws RemoteException;
    void UserExit(UpdateHandler stub) throws RemoteException;
    void ClientSendDrawing(Drawings d) throws RemoteException;
    void BroadCastRemoving(String id) throws RemoteException;
    void ClientSendRemoving(String id) throws RemoteException;
}
