package me.ghostcodes.rendering;

import lombok.Getter;
import lombok.Setter;

import static org.lwjgl.opengl.GL46.*;

public class VoxelBatch {

    private static int[] getIndices() {
        int[] indices = new int[MAX_INDICES];
        for(int i = 0; i < MAX_QUADS; i++){
            indices[i*6 + 0] = 0 + 4 * i;
            indices[i*6 + 1] = 1 + 4 * i;
            indices[i*6 + 2] = 2 + 4 * i;
            indices[i*6 + 3] = 2 + 4 * i;
            indices[i*6 + 4] = 3 + 4 * i;
            indices[i*6 + 5] = 0 + 4 * i;
        }

        return indices;
    }

    private static int[] indices = getIndices();

    public static final int WIDTH = 16, HEIGHT = 32, LENGTH = 16, MAX_VOXELS = WIDTH * HEIGHT * LENGTH, MAX_QUADS = MAX_VOXELS * 6, MAX_INDICES = MAX_QUADS * 6, MAX_VERTICES = MAX_QUADS * 8;

    private final int vao, vbo, ebo;
    @Getter @Setter private boolean render;

    @Getter private int x, y, z;

    public VoxelBatch(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, Integer.BYTES * MAX_VERTICES, GL_DYNAMIC_DRAW);
        glVertexAttribIPointer(0,2,GL_INT, 0,0);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);

        unbind();
    }

    public void setData(int[] data){
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void unbind() {
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }

    public void bind(){
        glBindVertexArray(vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glEnableVertexAttribArray(0);
    }
}
