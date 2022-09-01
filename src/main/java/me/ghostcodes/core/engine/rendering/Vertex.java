package me.ghostcodes.core.engine.rendering;

import lombok.Getter;

public class Vertex {
    @Getter
    private final float x,y,z;

    public Vertex(float x,float y,float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
