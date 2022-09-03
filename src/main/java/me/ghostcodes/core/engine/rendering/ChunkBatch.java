package me.ghostcodes.core.engine.rendering;

import lombok.Getter;
import me.ghostcodes.core.minecraft.world.Chunk;
import org.lwjgl.system.MemoryUtil;

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
        float[] colors = new float[Chunk.MAX_CUBES * 6 * 4 * 3];
        float[] normals = new float[Chunk.MAX_CUBES * 6 * 4 * 3];
        ArrayList<Float> tempColors = new ArrayList<>();
        ArrayList<Integer> tempIndices = new ArrayList<>();
        ArrayList<Float> tempNormals = new ArrayList<>();
        int[] indices = new int[Chunk.MAX_CUBES * 6 * 6];
        for(int i = 0; i < chunk.getBlocks().length; i++){
            for(int j = 0; j < chunk.getBlocks()[i].getQuads().length; j++){
                for(int k = 0; k < chunk.getBlocks()[i].getQuads()[j].getVertices().length; k++){
                    tempVertices.add(chunk.getBlocks()[i].getQuads()[j].getVertices()[k].getX());
                    tempVertices.add(chunk.getBlocks()[i].getQuads()[j].getVertices()[k].getY());
                    tempVertices.add(chunk.getBlocks()[i].getQuads()[j].getVertices()[k].getZ());
                    tempColors.add(chunk.getBlocks()[i].getQuads()[j].getVertices()[k].getColorx());
                    tempColors.add(chunk.getBlocks()[i].getQuads()[j].getVertices()[k].getColory());
                    tempColors.add(chunk.getBlocks()[i].getQuads()[j].getVertices()[k].getColorz());
                    tempNormals.add(chunk.getBlocks()[i].getQuads()[j].getVertices()[k].getNormX());
                    tempNormals.add(chunk.getBlocks()[i].getQuads()[j].getVertices()[k].getNormY());
                    tempNormals.add(chunk.getBlocks()[i].getQuads()[j].getVertices()[k].getNormZ());
                }

                for(int k = 0; k < chunk.getBlocks()[i].getQuads()[j].getIndices().length; k++){
                    tempIndices.add(chunk.getBlocks()[i].getQuads()[j].getIndices()[k]);
                }
            }
        }

        System.out.println(tempNormals.size());
        System.out.println(tempVertices.size());
        System.out.println(tempIndices.size());

        for(int i  = 0; i < vertices.length; i++){
            vertices[i] = tempVertices.get(i);
        }

        for(int i  = 0; i < indices.length; i++){
            indices[i] = tempIndices.get(i);
        }

        for(int i = 0; i < colors.length; i++){
            colors[i] = tempColors.get(i);
        }

        for(int i = 0; i < normals.length; i++){
            normals[i] = tempNormals.get(i);
        }


        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        int[] localLocations = new int[chunk.getBlocks().length * 6 * 6];
        ArrayList<Integer> tempLocalLocations = new ArrayList<>();
        for(int i = 0; i < chunk.getBlocks().length; i++){
            for(int j = 0; j < 18; j++) {
                tempLocalLocations.add(chunk.getBlocks()[i].getLocalLocation());
            }
        }

        for(int i = 0; i < tempLocalLocations.size(); i++){
            localLocations[i] = tempLocalLocations.get(i);
        }

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, localLocations, GL_STATIC_DRAW);
        glVertexAttribIPointer(1, 1, GL_UNSIGNED_INT, 0, 0);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0,0);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, 0, 0);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render(){
        glBindVertexArray(vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);

        glDrawElements(GL_TRIANGLES, Chunk.MAX_CUBES * 6 * 6, GL_UNSIGNED_INT, MemoryUtil.NULL);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
}
