package Core;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;

public class Logger {

    private static final String LOG_FILE = "Mycraft.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public enum Level {
        INFO, WARNING, DEBUG, ERROR
    }

    public static void log(Level level, String message) {
        if(!Debugger.debug)
            return;
        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format("[%s] [%s]: %s", timestamp, level, message);

        System.out.println(logMessage);

        if(level == Level.ERROR) {
            writeToFile(logMessage);
        }
    }

    public static void catchOpenGLErrors() {
        int error;
        while ((error = glGetError()) != GL_NO_ERROR) {
            Logger.log(Logger.Level.WARNING, "OpenGL error: " + error);
        }
    }
    private static void writeToFile(String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(message);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to write log: " + e.getMessage());
        }
    }

}
