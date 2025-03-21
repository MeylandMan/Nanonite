package net.Core;

import org.joml.Vector3f;

import java.util.Vector;

import static org.joml.Math.*;

public class WorldEnvironment {
    public static final Vector3f SURFACE_DEFAULT_COLOR = new Vector3f(0.53f, 0.81f, 0.92f);
    public static final Vector3f DEPTHS_DEFAULT_COLOR = new Vector3f(0);

    public static Vector3f interpolateFogColor(double playerY) {
        float startTransition = 16;
        float endTransition = 32;

        float t = clamp(((float) playerY - startTransition) / (endTransition - startTransition), 0, 1);
        return new Vector3f(DEPTHS_DEFAULT_COLOR).lerp(SURFACE_DEFAULT_COLOR, t);
    }
}
