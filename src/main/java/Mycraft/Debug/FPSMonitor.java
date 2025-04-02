package Mycraft.Debug;

import java.util.ArrayList;
import java.util.List;

import static org.joml.Math.clamp;
import static org.joml.Math.max;

public class FPSMonitor {
    private static long lastTime = System.nanoTime();
    private static double deltaTime = 0.0;
    private static int frameCount = 0;
    private static float currentFPS = 0;
    private static List<Float> fpsHistory = new ArrayList<>();

    public static void update() {

        long currentTime = System.nanoTime();

        frameCount++;

        if (currentTime - lastTime >= 1_000_000_000) {
            currentFPS = frameCount;
            fpsHistory.add(currentFPS);
            frameCount = 0;

            deltaTime = 1 / currentFPS; // Convert to seconds
            lastTime = currentTime;
        }
    }

    public static float getFPS() {
        return currentFPS;
    }
    public static double getDeltaTime() { return deltaTime; }

    public static float getAverageFPS() {
        if (fpsHistory.isEmpty()) return 0;
        float sum = 0;
        for (float fps : fpsHistory) {
            sum += fps;
        }
        return sum / fpsHistory.size();
    }

    public static float getMinFPS() {
        if (fpsHistory.isEmpty()) return 0;
        return fpsHistory.stream().min(Float::compare).orElse(0f);
    }

    public static float getMaxFPS() {
        if (fpsHistory.isEmpty()) return 0;
        return fpsHistory.stream().max(Float::compare).orElse(0f);
    }
}
