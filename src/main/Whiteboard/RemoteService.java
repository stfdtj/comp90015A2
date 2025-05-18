package Whiteboard;

import Whiteboard.Utility.*;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;

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
            users.get(numUsers - 1).SetUpdateHandler(client);
            try {
                users.get(numUsers - 1).ip = RemoteServer.getClientHost();
            } catch (ServerNotActiveException e) {
                throw new RuntimeException(e);
            }
        } else {
            RemoveLastUser();
            client.NotifyRefuse();
            return false;
        }
        for (Drawings drawing: canvas.getDrawingInfo()) {
            BroadcastDrawing(drawing);
        }
        return true;
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
        users.get(id - 1).cursorPosition = p;

        for (UpdateHandler client : clients) {
            client.receiveCursorUpdate(users);
        }

        canvas.setClients(users);

        SwingUtilities.invokeLater(canvas::repaint);
    }

    @Override
    public void BroadcastDrawing(Drawings d) throws RemoteException {
        for (UpdateHandler client : clients) {
            try {
                client.receiveDrawing(d);
            } catch (RemoteException e) {
                Log.error(e.getMessage());
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        }
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
        whiteboardGUI = gui;
    }

    @Override
    public void RemoveLastUser() throws RemoteException {
        users.remove(users.get(numUsers - 1));
        numUsers--;
    }

    @Override
    public void KickUser(int id) throws RemoteException {

        RemoteUser kicked = null;
        for (RemoteUser u : users) {
            if (u.id == id) {
                kicked = u;
                break;
            }
        }
        if (kicked == null) {
            Log.error("KickUser: no user with id=" + id);
            return;
        }

        Log.info("Kicking user " + id + " (" + kicked.username + ")");


        UpdateHandler handler = kicked.getUpdateHandler();
        try {
            handler.NotifyKicked();
        } catch (RemoteException ex) {
            Log.error("Failed to notify kicked user: " + ex.getMessage());
        }

        clients.removeIf(c -> c.equals(handler));

        kicked.status = "KICKED";
        kicked.cursorPosition = null;

    }

    @Override
    public void NotifyServerShutDown() throws RemoteException {
        for (UpdateHandler client : clients) {
            client.NotifyServerShutDown();
        }
    }

    @Override
    public void UserExit(UpdateHandler stub) throws RemoteException {
        Log.info("remote service called");
        for (RemoteUser u : users) {
            if (stub.equals(u.getUpdateHandler())) {
                u.status = "OFFLINE";
                u.cursorPosition  = null;
                u.SetUpdateHandler(null);
                break;
            }
        }

        Iterator<UpdateHandler> it = clients.iterator();
        while (it.hasNext()) {
            if (it.next().equals(stub)) {
                it.remove();
                break;
            }
        }
        Log.info("remote service finished");
    }

    @Override
    public void ClientSendDrawing(Drawings d) throws RemoteException {
        canvas.ReceiveRemoteShape(d);
        BroadcastDrawing(d);
    }

    @Override
    public void BroadCastRemoving(String id) throws RemoteException {
        Log.info("remote service broadcast removing called");
        for (UpdateHandler client : clients) {
            try {
                Log.info("Sending removal to client: " + client);
                client.receiveRemoving(id);
            } catch (RemoteException e) {
                Log.error("RemoteException while sending to client: " + client);
            } catch (RuntimeException e) {
                Log.error("RuntimeException while sending to client: " + client);
            }
        }
    }

    @Override
    public void ClientSendRemoving(String id) throws RemoteException {
        canvas.ReceiveRemoving(id);
    }


}
