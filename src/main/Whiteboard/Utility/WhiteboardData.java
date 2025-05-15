package Whiteboard.Utility;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import Whiteboard.DrawingMode;
import org.json.*;

public class WhiteboardData {

    private String boardName;
    private int canvasWidth;
    private int canvasHeight;
    private int offSetX;
    private int offSetY;
    private File notDefault;
    private List<Drawings> drawings;
    private static final Properties props = new Properties();
    private static String dir;

    static {
        try (FileReader reader = new FileReader("src/main/main/resources/config.properties")) {
            props.load(reader);
            dir = props.getProperty("app.data");
        } catch (IOException ex) {
            Log.error(ex.getMessage());
        }
    }


    public WhiteboardData(){
        boardName = "";
        canvasWidth = 1600;
        canvasHeight = 900;
        offSetX = 0;
        offSetY = 0;
        drawings = new ArrayList<>();

    }



    public void SaveData(File path) {

        try {
            StringBuilder write = new StringBuilder();
            JSONWriter jsonWriter = new JSONWriter(write);
            jsonWriter.object();

            jsonWriter.key("boardName").value(boardName);
            jsonWriter.key("canvasWidth").value(canvasWidth);
            jsonWriter.key("canvasHeight").value(canvasHeight);
            jsonWriter.key("offSetX").value(offSetX);
            jsonWriter.key("offSetY").value(offSetY);
            jsonWriter.key("drawings").value(drawingsToJsonArray(drawings));

            jsonWriter.endObject();

            JSONObject data = new JSONObject(write.toString());
            File file;
            if (path == null && notDefault == null) {
                file = new File(dir + boardName + ".json");
            } else file = Objects.requireNonNullElse(notDefault, path);

            if (!file.exists()) {
                boolean ok = file.createNewFile();
                if (!ok) {
                    Log.error("Could not create file");
                }
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            fw.write(data.toString());
            fw.close();

        } catch (Exception e) {
            Log.error(e.getMessage());
        }
    }

    public static WhiteboardData LoadData(String filePath) {
        Log.info("Loading data from file " + filePath);
        JSONObject root = null;
        try (FileReader fr = new FileReader(filePath)) {
            root = new JSONObject(new JSONTokener(fr));
        } catch (IOException e) {
            Log.error(e.getMessage());
        }
        Log.info("Load canvas data");
        WhiteboardData data = null;
        try {
            data = new WhiteboardData();
            assert root != null;
            data.boardName = root.get("boardName").toString();
            data.canvasWidth = root.getInt("canvasWidth");
            data.canvasHeight = root.getInt("canvasHeight");
            data.offSetX = root.getInt("offSetX");
            data.offSetY = root.getInt("offSetY");
        } catch (RuntimeException e) {
            Log.error(e.getMessage());
        }
        Log.info("Canvas data loaded");

        Log.info("Loading drawings");
        assert root != null;
        JSONArray drawings = root.optJSONArray("drawings");
        if (drawings != null) {
            List<Drawings> drawingsList = new ArrayList<>();
            for (int i = 0; i < drawings.length(); i++) {
                JSONObject obj = drawings.getJSONObject(i);
                if (obj.has("text")) {
                    String text = obj.getString("text");
                    Color color = Color.decode(obj.getString("color"));

                    // mode, size, style flags
                    DrawingMode dm = DrawingMode.valueOf(obj.getString("drawingMode"));
                    float size = (float)obj.getDouble("size");
                    boolean bold = obj.optBoolean("bold", false);
                    boolean italic = obj.optBoolean("italic", false);

                    // font object
                    JSONObject f = obj.getJSONObject("font");
                    String family = f.getString("family");
                    int style = f.getInt("style");
                    int fsize = f.getInt("size");
                    Font font = new Font(family, style, fsize);

                    // location
                    JSONObject loc = obj.getJSONObject("location");
                    Point pt = new Point(loc.getInt("x"), loc.getInt("y"));

                    drawingsList.add(new TextInfo(text, color, dm, size, bold, italic, font, pt));
                } else {
                    JSONObject s = obj.getJSONObject("start");
                    Point start = new Point(s.getInt("x"), s.getInt("y"));
                    JSONObject e = obj.getJSONObject("end");
                    Point end   = new Point(e.getInt("x"), e.getInt("y"));

                    DrawingMode dm = DrawingMode.valueOf(obj.getString("drawingMode"));
                    float thickness = (float)obj.getDouble("thickness");
                    Color color = Color.decode(obj.getString("color"));

                    drawingsList.add(new DrawingInfo(start, end, color, dm, thickness));
                }
            }
            assert data != null;
            data.drawings = drawingsList;
        }

        Log.info("Drawings loaded");
        return data;
    }

    public static JSONArray drawingsToJsonArray(List<Drawings> drawings) {
        JSONArray array = new JSONArray();

        for (Drawings d : drawings) {
            if (d instanceof DrawingInfo di) {
                JSONObject obj = new JSONObject();
                // start point
                Point s = di.getStart();
                JSONObject startJson = new JSONObject()
                        .put("x", s.x)
                        .put("y", s.y);
                obj.put("start", startJson);

                // end point
                Point e = di.getEnd();
                JSONObject endJson = new JSONObject()
                        .put("x", e.x)
                        .put("y", e.y);
                obj.put("end", endJson);

                obj.put("drawingMode", di.getDrawingMode().name());

                obj.put("thickness", di.getThickness());

                Color c = di.getColor();
                String hex = String.format("#%02x%02x%02x",
                        c.getRed(), c.getGreen(), c.getBlue());
                obj.put("color", hex);

                array.put(obj);
            } else if (d instanceof TextInfo ti) {
                JSONObject obj = new JSONObject();

                obj.put("text", ti.getText());
                obj.put("drawingMode", ti.getDrawingMode().name());
                obj.put("size", ti.getSize());
                obj.put("bold", ti.isBold());
                obj.put("italic", ti.isItalic());

                Color c = ti.getColor();
                String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
                obj.put("color", hex);

                Font f = ti.getFont();
                JSONObject fontJson = new JSONObject()
                        .put("family", f.getFamily())
                        .put("style", f.getStyle())
                        .put("size", f.getSize());
                obj.put("font", fontJson);

                Point p = ti.getLocation();
                JSONObject locJson = new JSONObject()
                        .put("x", p.x)
                        .put("y", p.y);
                obj.put("location", locJson);

                array.put(obj);
            }
        }


        return array;
    }









    public void setBoardName(String boardName){
        this.boardName = boardName;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
        Log.info(" Set canvas width in data: " + canvasWidth);
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
        Log.info(" Set canvas height in data: " + canvasHeight);
    }

    public void setOffSetX(int offSetX) {
        this.offSetX = offSetX;
        Log.info(" Set offSet x in data: " + offSetX);
    }

    public void setOffSetY(int offSetY) {
        this.offSetY = offSetY;
        Log.info(" Set offSet y in data: " + offSetY);
    }


    public void setDrawings(List<Drawings> drawings) {
        this.drawings = drawings;
        Log.info(" Set drawings in data: " + drawings);
    }
    public List<Drawings> getDrawings() {
        return drawings;
    }


    public int getOffSetY() {
        return offSetY;
    }

    public int getOffSetX() {
        return offSetX;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setNotDefault(File p) {
        this.notDefault = p;
    }
}
