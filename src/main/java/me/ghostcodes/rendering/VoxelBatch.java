package me.ghostcodes.rendering;

import lombok.Getter;
import org.joml.Vector3f;

import java.util.Arrays;

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

    public static final int WIDTH = 16, HEIGHT = 16, LENGTH = 16, MAX_VOXELS = WIDTH * HEIGHT * LENGTH, MAX_QUADS = MAX_VOXELS * 6, MAX_INDICES = MAX_QUADS * 6, MAX_VERTICES = MAX_QUADS * 8;

    private final int vao, vbo, ebo, tVao, tVbo, tEbo;
    @Getter private boolean render;

    public void setRender(boolean render){
        this.render = render;
    }
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

        tVao = glGenVertexArrays();
        glBindVertexArray(tVao);

        tVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, tVbo);
        glBufferData(GL_ARRAY_BUFFER, Integer.BYTES * MAX_VERTICES, GL_DYNAMIC_DRAW);
        glVertexAttribIPointer(0,2,GL_INT, 0,0);

        tEbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, tEbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);

        unbind();
    }

    public boolean doesRayIntersect(Vector3f point, Vector3f direction){
        float tmin, tmax, tymin, tymax, tzmin, tzmax;

        tmin = (x - point.x) * direction.x;
        tmax = ((x+VoxelBatch.WIDTH) - point.x) * direction.x;
        tymin = (y - point.y) * direction.y;
        tymax = ((y + VoxelBatch.HEIGHT) - point.y) * direction.y;

        if ((tmin > tymax) || (tymin > tmax))
            return true;

        if (tymin > tmin)
            tmin = tymin;
        if (tymax < tmax)
            tmax = tymax;

        tzmin = (z - point.z) * direction.z;
        tzmax = ((z + VoxelBatch.LENGTH) - point.z) * direction.z;

        if ((tmin > tzmax) || (tzmin > tmax))
            return true;

        if (tzmin > tmin)
            tmin = tzmin;
        if (tzmax < tmax)
            tmax = tzmax;

        return false;
    }
    public void setData(int[] data, int[] tData){
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);

        glBindVertexArray(tVao);
        glBindBuffer(GL_ARRAY_BUFFER, tVbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, tData);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void unbind() {
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void tDraw(){
        glBindVertexArray(tVao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, tEbo);
        glEnableVertexAttribArray(0);
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        unbind();
    }

    public void draw() {
        glBindVertexArray(vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glEnableVertexAttribArray(0);
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
        unbind();
    }
}
