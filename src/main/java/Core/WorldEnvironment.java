package Core;

import org.joml.Vector3f;

import static org.joml.Math.*;

public class WorldEnvironment {
    // Fog Parameters
    public static final Vector3f SURFACE_DEFAULT_COLOR = new Vector3f(0.53f, 0.81f, 0.92f);
    public static final Vector3f DEPTHS_DEFAULT_COLOR = new Vector3f(0);

    public static final float DEFAULT_FOG_DISTANCE = 7.5f;
    public static final float WATER_FOG_DISTANCE = 1.0f;
    public static float fogDistance = DEFAULT_FOG_DISTANCE;

    public static final int FOG_DISTANCE_ACCELERATION = 20;

    public static boolean isUnderWater = false;

    public static Vector3f interpolateFogColor(double playerY) {
        float startTransition = -100;
        float endTransition = 0;

        float t = clamp(((float) playerY - startTransition) / (endTransition - startTransition), 0, 1);
        return new Vector3f(DEPTHS_DEFAULT_COLOR).lerp(SURFACE_DEFAULT_COLOR, t);
    }
}
