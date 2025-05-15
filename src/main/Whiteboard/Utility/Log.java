package Whiteboard.Utility;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Log {

    private static final Properties props = new Properties();


    private static final String LOG_BASENAME = "board";
    private static final DateTimeFormatter FILE_TS_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter LINE_TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private static final Path logPath;
    private static final PrintWriter writer;


    static {
        try {
            try (FileReader reader = new FileReader("src/main/main/resources/config.properties")) {
                props.load(reader);
            } catch (IOException ex) {
                Log.error(ex.getMessage());
            }
            String LOG_DIR = props.getProperty("app.log.path");
            Files.createDirectories(Paths.get(LOG_DIR));

            String ts = LocalDateTime.now().format(FILE_TS_FMT);
            logPath = Paths.get(LOG_DIR, LOG_BASENAME + "-" + ts + ".log");

            Files.createFile(logPath);

            writer = new PrintWriter(
                    Files.newBufferedWriter(
                            logPath,
                            StandardCharsets.UTF_8,
                            StandardOpenOption.APPEND),
                    true
            );
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to init Log: " + e);
        }
    }

    public static void action(String msg) { log("ACTION", msg); }
    public static void info(  String msg) { log("INFO",   msg); }
    public static void error( String msg) { log("ERROR",  msg); }

    private static synchronized void log(String level, String msg) {
        String now = LocalDateTime.now().format(LINE_TS_FMT);
        String line = String.format("[%s] [%s] %s", level, now, msg);

        System.out.println(line);

        // then write to file
        writer.println(line);
    }
}
