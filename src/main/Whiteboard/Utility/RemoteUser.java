package Whiteboard.Utility;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

public class RemoteUser implements Serializable {

    public int id;
    public String username;
    public Point cusorPosition;
    public String status = "OFFLINE";
    public Color color;

    public RemoteUser(String username, Point cursorPosition) {
        this.username = username;
        this.cusorPosition = cursorPosition;
        Random rnd = new Random();
        int r = rnd.nextInt(210);
        int g = rnd.nextInt(210);
        int b = rnd.nextInt(210);
        color = new Color(r, g, b);
    }
}
