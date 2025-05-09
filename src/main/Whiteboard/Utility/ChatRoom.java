package Whiteboard.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom {

    private ArrayList<RemoteUser> users;
    private static ArrayList<String> messages;
    private static int total = 0;



    public ChatRoom(ArrayList<RemoteUser> users) {
        this.users = users;
        messages = new ArrayList<>();
    }


    public ArrayList<RemoteUser> getUsers() {
        return users;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }


    public void appendMessage(String message) {
        messages.add(message);
    }
}
