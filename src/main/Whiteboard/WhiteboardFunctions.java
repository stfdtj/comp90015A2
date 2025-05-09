package Whiteboard;

import Whiteboard.Utility.ChatRoom;
import Whiteboard.Utility.DrawingInfo;
import Whiteboard.Utility.RemoteUser;
import Whiteboard.Utility.TextInfo;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface WhiteboardFunctions extends Remote {

    void RegisterClient(UpdateHandler client) throws RemoteException;
    void BroadcastDrawing(DrawingInfo info) throws RemoteException;
    void SendDrawings(DrawingInfo info) throws RemoteException;
    void BroadCastText(TextInfo info) throws RemoteException;
    void SendText(TextInfo info) throws RemoteException;
    void AddRemoteUser(RemoteUser user) throws RemoteException;
    ArrayList<RemoteUser> getUsers() throws RemoteException;
    void UpdateCursor(Point p, int id) throws RemoteException;
    int GetNumUsers() throws RemoteException;
    void BroadCastMessage(String m) throws RemoteException;
    ChatRoom GetChatRoom() throws RemoteException;
}
