package Whiteboard;

import Whiteboard.Utility.*;
import main.Form;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RemoteService extends UnicastRemoteObject implements WhiteboardFunctions {

    private final ArrayList<UpdateHandler> clients = new ArrayList<>();
    private Canvas canvas;
    private static WhiteboardGUI whiteboardGUI;
    private final ArrayList<RemoteUser> users = new ArrayList<>();
    private int numUsers = 0;

    public RemoteService() throws RemoteException {

    }

    @Override
    public boolean RegisterClient(UpdateHandler client) throws RemoteException {
        if (whiteboardGUI.NewJoinApplication()) {
            clients.add(client);
        } else {
            RemoveLastUser();
            client.NotifyRefuse();
            return false;
        }


        for (DrawingInfo info: canvas.getDrawingInfo()) {
            BroadcastDrawing(info);
        }

        for (TextInfo info: canvas.getTextInfo()) {
            BroadCastText(info);
        }
        return true;
    }


    @Override
    public void BroadCastText(TextInfo info) throws RemoteException {
        for (UpdateHandler client : clients) {
            try {
                client.receiveDrawing(null, info);
            } catch (RemoteException e) {
                Log.error(e.getMessage());
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void SendText(TextInfo info) throws RemoteException {
        canvas.SendRemoteShape(null, info);
        BroadCastText(info);
    }


    // very simple id
    @Override
    public void AddRemoteUser(RemoteUser user) throws RemoteException {
        numUsers++;
        users.add(user);
        users.get(numUsers - 1).id = numUsers;

        Log.info("User " + user.id + " added to user list");
    }

    @Override
    public ArrayList<RemoteUser> getUsers() throws RemoteException {
        return users;
    }

    @Override
    public void UpdateCursor(Point p, int id) throws RemoteException {
        users.get(id - 1).cusorPosition = p;
        for (UpdateHandler client : clients) {
            client.receiveCursorUpdate(users);
        }
    }

    @Override
    public void BroadcastDrawing(DrawingInfo info) throws RemoteException {
        for (UpdateHandler client : clients) {
            try {
                client.receiveDrawing(info, null);
            } catch (RemoteException e) {
                Log.error(e.getMessage());
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // after send drawings to server should send drawings to every client
    @Override
    public void SendDrawings(DrawingInfo info) throws RemoteException {
        canvas.SendRemoteShape(info, null);
        BroadcastDrawing(info);
    }

    public void SetCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public int GetNumUsers() {
        return numUsers;
    }

    @Override
    public void BroadCastMessage(String m) throws RemoteException {
        canvas.ReceiveMessage(m);
        for (UpdateHandler client : clients) {
            client.receiveMessage(m);
        }
    }

    @Override
    public void SetWhiteboardGUI(WhiteboardGUI gui) throws RemoteException {
        this.whiteboardGUI = gui;
    }

    @Override
    public void RemoveLastUser() throws RemoteException {
        users.remove(users.get(numUsers - 1));
        numUsers--;
    }


}
