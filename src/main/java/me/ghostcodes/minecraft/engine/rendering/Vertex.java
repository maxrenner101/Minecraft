package me.ghostcodes.minecraft.engine.rendering;

import org.joml.Vector3f;

public class Vertex {
    public Vector3f position, normal;
    public Vector3f localLocation;

    public Vertex(Vector3f position, Vector3f normal, Vector3f localLocation){
        this.position = position;
        this.normal = normal;
        this.localLocation = localLocation;
    }
}
