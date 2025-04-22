package Whiteboard;

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
    public void BroadcastDrawing(DrawingInfo info) throws RemoteException {
        for (UpdateHandler client : clients) {
            try {
                client.receiveDrawing(info);
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
        canvas.SendRemoteShape(info);
        BroadcastDrawing(info);
    }

    public void SetCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

}
