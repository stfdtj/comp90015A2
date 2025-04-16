package Whiteboard;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RemoteService extends UnicastRemoteObject implements WhiteboardFunctions {

    private ArrayList<UpdateHandler> clients = new ArrayList<>();

    public RemoteService() throws RemoteException {
    }

    @Override
    public void RegisterClient(UpdateHandler client) throws RemoteException {
        clients.add(client);
        System.out.println("Client registered.");
        System.out.println("Client class: " + client.getClass().getName());

    }

    @Override
    public void BroadcastDrawing(DrawingInfo info) throws RemoteException {
        for (UpdateHandler client : clients) {
            try {
                System.out.println("Broadcast drawing: " + info);
                client.receiveDrawing(info);
                System.out.println("Broadcast drawing finished.");
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
