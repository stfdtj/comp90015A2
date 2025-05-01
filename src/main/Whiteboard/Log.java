package Whiteboard;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static final String LOG_FILE = "src/main/Whiteboard/resources/board.log";
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void action(String msg) {
        log("ACTION", msg);
    }

    public static void error(String msg) {
        log("ERROR", msg);
    }


    public static void info(String msg) {
        log("INFO", msg);
    }

    private static void log(String level, String msg) {
        String timestamp = formatter.format(new Date());
        String formatted = String.format("[%s] [%s] %s", level, timestamp, msg);

        // if failed to create log file you can still get log from terminal
        System.out.println(formatted);



        Path path = Paths.get(LOG_FILE);

        // if file exist, write to it
        if (Files.exists(path)) {
            try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
                writer.write(formatted + "\n");
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to write log to file.");
            }
        }

        // if not create a new one
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                System.err.println("[ERROR] Failed to create log file.");
            }

        }
    }
}