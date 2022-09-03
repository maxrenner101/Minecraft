package me.ghostcodes.core.engine.rendering;

import lombok.Getter;

public class Quad {
    @Getter
    private final Vertex[] vertices = new Vertex[4];
    @Getter private final int[] indices;

    public Quad(int offset, float normX, float normY, float normZ, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        vertices[0] = new Vertex(x1,y1,z1, normX, normY, normZ, 0.06f,0.55f,0.03f);
        vertices[1] = new Vertex(x2,y2,z2, normX, normY, normZ, 0.06f,0.55f,0.03f);
        vertices[2] = new Vertex(x3,y3,z3, normX, normY, normZ, 0.06f,0.55f,0.03f);
        vertices[3] = new Vertex(x4,y4,z4, normX, normY, normZ, 0.06f,0.55f,0.03f);
        indices = new int[]{0, 1, 2, 2, 3, 0};
        addToIndices(offset);
    }

    public void addToIndices(int x){
        for(int i = 0; i < indices.length; i++){
            indices[i] += x;
        }
    }
}
