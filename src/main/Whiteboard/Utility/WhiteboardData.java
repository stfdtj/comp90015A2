package Whiteboard.Utility;

import java.io.FileWriter;
import java.util.List;
import org.json.*;

public class WhiteboardData {

    private String boardName;
    private int canvasWidth;
    private int canvasHeight;
    private int offSetX;
    private int offSetY;
    private List<TextInfo> texts;
    private List<DrawingInfo> drawings;


    public WhiteboardData(){}

    // should be read in file
    // should be create new file
    // should decide whethere load everything in memory?


    public void SaveData() {
        try {
            StringBuilder write = new StringBuilder();
            JSONWriter jsonWriter = new JSONWriter(write);
            jsonWriter.object();

            jsonWriter.key("boardName").value(boardName);
            jsonWriter.key("canvasWidth").value(canvasWidth);
            jsonWriter.key("canvasHeight").value(canvasHeight);
            jsonWriter.key("offSetX").value(offSetX);
            jsonWriter.key("offSetY").value(offSetY);

            jsonWriter.endObject();

            JSONObject data = new JSONObject(write.toString());
            FileWriter file = new FileWriter(boardName + ".json");
            file.write(data.toString());
            file.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }








    public void setBoardName(String boardName){
        this.boardName = boardName;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public void setOffSetX(int offSetX) {
        this.offSetX = offSetX;
    }

    public void setOffSetY(int offSetY) {
        this.offSetY = offSetY;
    }

    public void setTexts(List<TextInfo> texts) {
        this.texts = texts;
    }

    public void setDrawings(List<DrawingInfo> drawings) {
        this.drawings = drawings;
    }
}
