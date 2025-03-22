package net.Core;

import net.Core.Physics.CharacterBody;
import net.Core.Physics.Raycast;
import org.joml.Vector3d;

public class Entity {

    public Vector3d position;
    public Vector3d rotation;
    public Vector3d scale;

    public Vector3d velocity = new Vector3d();
    public Vector3d direction = new Vector3d(); // The direction the entity is looking at

    // Collisions
    public CharacterBody body;
    public Raycast raycast;

    public Entity() {
        this.position = new Vector3d();
        this.rotation = new Vector3d();
        this.scale = new Vector3d(1.0);

        this.body = new CharacterBody();
        this.raycast = new Raycast(position, direction);
    }

    public Entity(Vector3d position) {
        this.position = position;
        this.rotation = new Vector3d();
        this.scale = new Vector3d(1.0);

        this.body = new CharacterBody();
        this.raycast = new Raycast(position, direction);
    }

    public Entity(Vector3d position, Vector3d rotation) {
        this.position = position;
        this.rotation = rotation;
        this.scale = new Vector3d(1.0);

        this.body = new CharacterBody();
        this.raycast = new Raycast(position, direction);
    }

    public Entity(Vector3d position, Vector3d rotation, Vector3d scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;

        this.body = new CharacterBody();
        this.raycast = new Raycast(position, direction);
    }
}
