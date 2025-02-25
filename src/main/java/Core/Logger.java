package Core;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.lwjgl.opengl.GL43.*;

public class Logger {

    private static final String LOG_FILE = "Mycraft.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public enum Level {
        INFO, WARNING, ERROR
    }

    public static void log(Level level, String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format("[%s] [%s]: %s", timestamp, level, message);

        System.out.println(logMessage);

        if(level == Level.ERROR) {
            writeToFile(logMessage);
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

    public static void glDebugOutput(int source, int type, int id, int severity, int length, long message, long userParam) {
        if (id == 131169 || id == 131185 || id == 131218 || id == 131204
                || id == 131222
                || id == 131140) return;
        if (type == GL_DEBUG_TYPE_PERFORMANCE) return;

        System.out.println("---------------");
        //System.out.println("Debug message (" + id + "): " + glGetDebugMessageLog(1));

        switch (source) {
            case GL_DEBUG_SOURCE_API -> System.out.println("Source: API");
            case GL_DEBUG_SOURCE_WINDOW_SYSTEM -> System.out.println("Source: Window System");
            case GL_DEBUG_SOURCE_SHADER_COMPILER -> System.out.println("Source: Shader Compiler");
            case GL_DEBUG_SOURCE_THIRD_PARTY -> System.out.println("Source: Third Party");
            case GL_DEBUG_SOURCE_APPLICATION -> System.out.println("Source: Application");
            case GL_DEBUG_SOURCE_OTHER -> System.out.println("Source: Other");
        }

        switch (type) {
            case GL_DEBUG_TYPE_ERROR -> System.out.println("Type: Error");
            case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> System.out.println("Type: Deprecated Behaviour");
            case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> System.out.println("Type: Undefined Behaviour");
            case GL_DEBUG_TYPE_PORTABILITY -> System.out.println("Type: Portability");
            case GL_DEBUG_TYPE_PERFORMANCE -> System.out.println("Type: Performance");
            case GL_DEBUG_TYPE_MARKER -> System.out.println("Type: Marker");
            case GL_DEBUG_TYPE_PUSH_GROUP -> System.out.println("Type: Push Group");
            case GL_DEBUG_TYPE_POP_GROUP -> System.out.println("Type: Pop Group");
            case GL_DEBUG_TYPE_OTHER -> System.out.println("Type: Other");
        }

        switch (severity) {
            case GL_DEBUG_SEVERITY_HIGH -> System.out.println("Severity: high");
            case GL_DEBUG_SEVERITY_MEDIUM -> System.out.println("Severity: medium");
            case GL_DEBUG_SEVERITY_LOW -> System.out.println("Severity: low");
            case GL_DEBUG_SEVERITY_NOTIFICATION -> System.out.println("Severity: notification");
        }
    }
}
