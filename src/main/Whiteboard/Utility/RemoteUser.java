package Whiteboard.Utility;

import Whiteboard.UpdateHandler;
import java.awt.*;
import java.io.Serializable;
import java.util.Random;

public class RemoteUser implements Serializable {

    public int id;
    public String username;
    public Point cursorPosition;
    public String status = "ONLINE";
    public Color color;
    private UpdateHandler updateHandler;
    public String ip;

    public RemoteUser(String username, Point cursorPosition) {
        this.username = username;
        this.cursorPosition = cursorPosition;
        Random rnd = new Random();
        int r = rnd.nextInt(210);
        int g = rnd.nextInt(210);
        int b = rnd.nextInt(210);
        color = new Color(r, g, b);
    }
    public void SetUpdateHandler(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    public UpdateHandler getUpdateHandler() {
        return updateHandler;
    }
}
