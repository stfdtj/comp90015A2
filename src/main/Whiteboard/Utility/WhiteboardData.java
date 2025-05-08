package Whiteboard.Utility;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import Whiteboard.DrawingMode;
import org.json.*;

public class WhiteboardData {

    private String boardName;
    private int canvasWidth;
    private int canvasHeight;
    private int offSetX;
    private int offSetY;
    private List<TextInfo> texts;
    private List<DrawingInfo> drawings;
    private File notDefault;


    public WhiteboardData(){
        boardName = "";
        canvasWidth = 1600;
        canvasHeight = 900;
        offSetX = 0;
        offSetY = 0;
        texts = new ArrayList<>();
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
            jsonWriter.key("texts").value(textInfosToJsonArray(texts));
            jsonWriter.key("shapes").value(drawingInfosToJsonArray(drawings));

            jsonWriter.endObject();

            JSONObject data = new JSONObject(write.toString());
            File file;
            if (path == null && notDefault == null) {
                file = new File("src/main/main/resources/SavedWhiteBoards/"+boardName + ".json");
            } else if (notDefault != null) {
                file = notDefault;
            }else {
                file = path;
            }

            // if it doesn't already exist, this creates an empty file
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
        } catch (FileNotFoundException e) {
            Log.error(e.getMessage());
        } catch (IOException e) {
            Log.error(e.getMessage());
        }
        Log.info("Load canvas data");
        WhiteboardData data = null;
        try {
            data = new WhiteboardData();
            data.boardName = root.get("boardName").toString();
            data.canvasWidth = root.getInt("canvasWidth");
            data.canvasHeight = root.getInt("canvasHeight");
            data.offSetX = root.getInt("offSetX");
            data.offSetY = root.getInt("offSetY");
        } catch (RuntimeException e) {
            Log.error(e.getMessage());
        }
        Log.info("Canvas data loaded");

        Log.info("Loading texts");
        JSONArray textsArr = root.optJSONArray("texts");
        if (textsArr != null) {
            List<TextInfo> texts = new ArrayList<>(textsArr.length());
            for (int i = 0; i < textsArr.length(); i++) {
                JSONObject t = textsArr.getJSONObject(i);
                String text = t.getString("text");
                Color color = Color.decode(t.getString("color"));

                // mode, size, style flags
                DrawingMode dm = DrawingMode.valueOf(t.getString("drawingMode"));
                float size = (float)t.getDouble("size");
                boolean bold = t.optBoolean("bold", false);
                boolean italic = t.optBoolean("italic", false);

                // font object
                JSONObject f = t.getJSONObject("font");
                String family = f.getString("family");
                int style = f.getInt("style");
                int fsize = f.getInt("size");
                Font font = new Font(family, style, fsize);

                // location
                JSONObject loc = t.getJSONObject("location");
                Point pt = new Point(loc.getInt("x"), loc.getInt("y"));

                texts.add(new TextInfo(text, color, dm, size, bold, italic, font, pt));
            }
            data.texts = texts;
        }
        Log.info("Texts loaded");

        Log.info("Loading drawings");
        JSONArray drawArr = root.optJSONArray("shapes");
        //Log.info(drawArr.isEmpty() ? "no drawings" : "drawings");
        if (drawArr != null) {
            List<DrawingInfo> draws = new ArrayList<>(drawArr.length());
            for (int i = 0; i < drawArr.length(); i++) {
                JSONObject d = drawArr.getJSONObject(i);

                JSONObject s = d.getJSONObject("start");
                Point start = new Point(s.getInt("x"), s.getInt("y"));
                JSONObject e = d.getJSONObject("end");
                Point end   = new Point(e.getInt("x"), e.getInt("y"));

                DrawingMode dm = DrawingMode.valueOf(d.getString("drawingMode"));
                float thickness = (float)d.getDouble("thickness");
                Color color = Color.decode(d.getString("color"));

                draws.add(new DrawingInfo(start, end, color, dm, thickness));
            }
            data.drawings = draws;
            Log.info(data.drawings.size() + " drawings loaded");
        }
        Log.info("Drawings loaded");
        return data;
    }


    public static JSONArray textInfosToJsonArray(List<TextInfo> texts) {
        JSONArray array = new JSONArray();

        for (TextInfo ti : texts) {
            JSONObject obj = new JSONObject();

            // basic fields
            obj.put("text", ti.getText());
            obj.put("drawingMode", ti.getDrawingMode().name());
            obj.put("size", ti.getSize());
            obj.put("bold", ti.isBold());
            obj.put("italic", ti.isItalic());

            // color as hex string
            Color c = ti.getColor();
            String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            obj.put("color", hex);

            // font details
            Font f = ti.getFont();
            JSONObject fontJson = new JSONObject()
                    .put("family", f.getFamily())
                    .put("style", f.getStyle())
                    .put("size", f.getSize());
            obj.put("font", fontJson);

            // location point
            Point p = ti.getLocation();
            JSONObject locJson = new JSONObject()
                    .put("x", p.x)
                    .put("y", p.y);
            obj.put("location", locJson);

            array.put(obj);
        }

        return array;
    }

    public static JSONArray drawingInfosToJsonArray(List<DrawingInfo> drawings) {
        JSONArray array = new JSONArray();

        for (DrawingInfo di : drawings) {
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

            // drawing mode (e.g. LINE, OVAL, etc.)
            obj.put("drawingMode", di.getDrawingMode().name());

            // thickness
            obj.put("thickness", di.getThickness());

            // color as hex string
            Color c = di.getColor();
            String hex = String.format("#%02x%02x%02x",
                    c.getRed(), c.getGreen(), c.getBlue());
            obj.put("color", hex);

            array.put(obj);
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

    public void setTexts(List<TextInfo> texts) {

        this.texts = texts;
        Log.info(" Set texts in data: " + texts);
    }

    public void setDrawings(List<DrawingInfo> drawings) {
        this.drawings = drawings;
        Log.info(" Set drawings in data: " + drawings);
    }
    public List<DrawingInfo> getDrawings() {
        return drawings;
    }

    public List<TextInfo> getTexts() {
        return texts;
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
