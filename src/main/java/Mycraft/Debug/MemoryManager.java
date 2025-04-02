package Mycraft.Debug;

public class MemoryManager {
    private static long FreeMemory;
    private static long MaxMemory;
    private static long TotalMemory;

    private static double MemPercentage = 0.0;
    private static double AllocPercentage = 0.0;

    public static void Update() {

        // From bytes to MegaBytes
        FreeMemory = (Runtime.getRuntime().freeMemory() / 1024) / 1024;
        MaxMemory = (Runtime.getRuntime().maxMemory() / 1024) / 1024;
        TotalMemory = (Runtime.getRuntime().totalMemory() / 1024) / 1024;

        // Get the frequency
        MemPercentage = (double) FreeMemory/MaxMemory;
        AllocPercentage = (double) TotalMemory/MaxMemory;
    }

    public static long getFreeMemory() { return FreeMemory; }
    public static long getMaxMemory() { return MaxMemory; }
    public static long getTotalMemory() { return TotalMemory; }

    public static double getMemPercentage() { return MemPercentage; }
    public static double getAllocPercentage() { return AllocPercentage; }
}
