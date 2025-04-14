package Whiteboard;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteService extends UnicastRemoteObject implements WhiteboardFunctions{

    protected RemoteService() throws RemoteException {
    }


    @Override
    public void FreeDraw(DrawingInfo info) throws RemoteException {

    }

    @Override
    public void DrawLine(DrawingInfo info) throws RemoteException {

    }

    @Override
    public void DrawRectangle(DrawingInfo info) throws RemoteException {

    }

    @Override
    public void DrawOval(DrawingInfo info) throws RemoteException {

    }

    @Override
    public void DrawTriangle(DrawingInfo info) throws RemoteException {

    }


}
