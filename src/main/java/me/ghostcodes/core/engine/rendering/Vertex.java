package me.ghostcodes.core.engine.rendering;

import lombok.Getter;

public class Vertex {
    @Getter
    private final float x,y,z;
    @Getter
    private final float colorx,colory,colorz;

    public Vertex(float x,float y,float z, float colorx, float colory, float colorz){
        this.colorx = colorx;
        this.colory = colory;
        this.colorz = colorz;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
