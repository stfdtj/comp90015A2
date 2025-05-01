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
    public void BroadcastDrawing(DrawingInfo info, TextInfo textInfo) throws RemoteException {
        for (UpdateHandler client : clients) {
            try {
                client.receiveDrawing(info, textInfo);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // after send drawings to server should send drawings to every client
    @Override
    public void SendDrawings(DrawingInfo info, TextInfo textInfo) throws RemoteException {
        canvas.SendRemoteShape(info, textInfo);
        BroadcastDrawing(info, textInfo);
    }

    public void SetCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

}
