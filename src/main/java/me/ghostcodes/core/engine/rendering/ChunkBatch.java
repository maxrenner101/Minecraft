package me.ghostcodes.core.engine.rendering;

import lombok.Getter;
import me.ghostcodes.core.minecraft.world.Chunk;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class ChunkBatch {

    private final int vao;
    private final int ebo;
    @Getter private final Chunk chunk;

    public ChunkBatch(Chunk chunk){
        this.chunk = chunk;
        ArrayList<Float> tempVertices = new ArrayList<>();
        float[] vertices = new float[Chunk.MAX_CUBES * 6 * 4 * 3];
        ArrayList<Integer> tempIndices = new ArrayList<>();
        int[] indices = new int[Chunk.MAX_CUBES * 6 * 6];
        for(int i = 0; i < chunk.getBlocks().length; i++){
            for(int j = 0; j < chunk.getBlocks()[i].getQuads().length; j++){
                for(int k = 0; k < chunk.getBlocks()[i].getQuads()[j].getVertices().length; k++){
                    tempVertices.add(chunk.getBlocks()[i].getQuads()[j].getVertices()[k].getX());
                    tempVertices.add(chunk.getBlocks()[i].getQuads()[j].getVertices()[k].getY());
                    tempVertices.add(chunk.getBlocks()[i].getQuads()[j].getVertices()[k].getZ());
                }

                for(int k = 0; k < chunk.getBlocks()[i].getQuads()[j].getIndices().length; k++){
                    tempIndices.add(chunk.getBlocks()[i].getQuads()[j].getIndices()[k]);
                }
            }
        }

        for(int i  = 0; i < vertices.length; i++){
            vertices[i] = tempVertices.get(i);
            if(i < 50){
                System.out.print(vertices[i] + " ");
            }
        }

        for(int i  = 0; i < indices.length; i++){
            indices[i] = tempIndices.get(i);
        }


        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        int[] localLocations = new int[chunk.getBlocks().length];
        for(int i = 0; i < localLocations.length; i++){
            localLocations[i] = chunk.getBlocks()[i].getLocalLocation();
        }

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, localLocations, GL_STATIC_DRAW);
        glVertexAttribIPointer(1, 1, GL_UNSIGNED_INT, 0, 0);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public void render(){
        glBindVertexArray(vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, Chunk.MAX_CUBES * 6 * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
}
