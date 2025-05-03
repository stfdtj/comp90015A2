package Whiteboard;

import Whiteboard.Utility.DrawingInfo;
import Whiteboard.Utility.TextInfo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RemoteService extends UnicastRemoteObject implements WhiteboardFunctions {

    private ArrayList<UpdateHandler> clients = new ArrayList<>();
    private Canvas canvas;

    public RemoteService() throws RemoteException {

    }

    @Override
    public void RegisterClient(UpdateHandler client) throws RemoteException {
        clients.add(client);

    }


    @Override
    public void BroadCastText(TextInfo info) throws RemoteException {
        for (UpdateHandler client : clients) {
            try {
                client.receiveDrawing(null, info);
            } catch (RemoteException e) {
                e.printStackTrace();
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

    @Override
    public void BroadcastDrawing(DrawingInfo info) throws RemoteException {
        for (UpdateHandler client : clients) {
            try {
                client.receiveDrawing(info, null);
            } catch (RemoteException e) {
                e.printStackTrace();
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

}
