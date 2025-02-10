package GameLayer;

public class FPSCounter {
    private long lastTime = System.nanoTime();
    private int frameCount = 0;
    private float fps = 0;

    public void update() {
        long currentTime = System.nanoTime();
        frameCount++;

        if (currentTime - lastTime >= 1_000_000_000) {
            fps = frameCount;
            frameCount = 0;
            lastTime = currentTime;
        }
    }

    public float getFPS() {
        return fps;
    }
}
