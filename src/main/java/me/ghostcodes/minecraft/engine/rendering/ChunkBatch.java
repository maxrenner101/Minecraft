package me.ghostcodes.minecraft.engine.rendering;

import me.ghostcodes.minecraft.engine.shaders.ShaderProgram;
import me.ghostcodes.minecraft.game.world.Chunklet;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

public class ChunkBatch {
    private final int MAX_CUBES = Chunklet.getSIZE(), MAX_QUADS = MAX_CUBES * 6;
    private final int vao, ebo;

    private final Chunklet chunklet;
    public ChunkBatch(Chunklet chunklet) {
        this.chunklet = chunklet;
        int[] indices = new int[MAX_QUADS * 6];
        for(int i = 0; i < MAX_QUADS; i++){
            indices[i * 6] = i * 4;
            indices[1 + i * 6] = 1 + i*4;
            indices[2 + i * 6] = 2 + i*4;
            indices[3 + i * 6] = 2 + i*4;
            indices[4 + i * 6] = 3 + i*4;
            indices[5 + i * 6] = i * 4;
        }

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        List<Float> vertices = new ArrayList<>();

        for(int i = 0; i < chunklet.getBlocks().size(); i++){
            Cube cube = chunklet.getBlocks().get(i);
            for(int j = 0; j < cube.getVertices().size(); j++){
                Vertex vertex = cube.getVertices().get(j);
                vertices.add(vertex.position.x);
                vertices.add(vertex.position.y);
                vertices.add(vertex.position.z);
                vertices.add(vertex.normal.x);
                vertices.add(vertex.normal.y);
                vertices.add(vertex.normal.z);
                vertices.add(vertex.localLocation.x);
                vertices.add(vertex.localLocation.y);
                vertices.add(vertex.localLocation.z);
            }
        }

        float[] finalVertices = new float[MAX_QUADS * 4 * 9];

        for(int i = 0; i < vertices.size(); i++){
            finalVertices[i] = vertices.get(i);
        }

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, finalVertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0,3,GL_FLOAT,false,9*Float.BYTES,0);
        glVertexAttribPointer(1,3,GL_FLOAT,false,9*Float.BYTES,3*Float.BYTES);
        glVertexAttribPointer(2,3,GL_FLOAT,false,9*Float.BYTES,6*Float.BYTES);

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render(ShaderProgram shaderProgram){
        shaderProgram.setUniform("chunkLocation", chunklet.getLocationAsVector());
        shaderProgram.setUniform("proj", Camera.getProj());
        shaderProgram.setUniform("camera", Camera.getCamera());
        glBindVertexArray(vao);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glDrawElements(GL_TRIANGLES, MAX_QUADS * 6, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

    }
}
