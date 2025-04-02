package Mycraft.Debug;

import java.util.ArrayList;
import java.util.List;

public class FPSMonitor {
    private long lastTime = System.nanoTime();
    private int frameCount = 0;
    private float currentFPS = 0;
    private List<Float> fpsHistory = new ArrayList<>();

    public void update() {
        long currentTime = System.nanoTime();
        frameCount++;

        if (currentTime - lastTime >= 1_000_000_000) { // 1 seconde écoulée
            currentFPS = frameCount;
            fpsHistory.add(currentFPS);
            frameCount = 0;
            lastTime = currentTime;
        }
    }

    public float getFPS() {
        return currentFPS;
    }

    public float getAverageFPS() {
        if (fpsHistory.isEmpty()) return 0;
        float sum = 0;
        for (float fps : fpsHistory) {
            sum += fps;
        }
        return sum / fpsHistory.size();
    }

    public float getMinFPS() {
        if (fpsHistory.isEmpty()) return 0;
        return fpsHistory.stream().min(Float::compare).orElse(0f);
    }

    public float getMaxFPS() {
        if (fpsHistory.isEmpty()) return 0;
        return fpsHistory.stream().max(Float::compare).orElse(0f);
    }
}
