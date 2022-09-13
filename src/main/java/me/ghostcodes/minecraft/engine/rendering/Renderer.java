package me.ghostcodes.minecraft.engine.rendering;

import me.ghostcodes.minecraft.engine.shaders.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

    private static Renderer instance;
    private ShaderProgram shaderProgram;

    private List<ChunkBatch> chunkBatchs = new ArrayList<>();

    public void addChunkBatch(ChunkBatch chunkBatch){
        chunkBatchs.add(chunkBatch);
    }

    private Renderer(){
    }

    public static Renderer get(){
        if(instance == null)
            instance = new Renderer();

        return instance;
    }

    public void setShaderProgram(ShaderProgram shaderProgram){
        this.shaderProgram = shaderProgram;
    }

    public void render(){
        shaderProgram.bind();

        chunkBatchs.forEach(b -> b.render(shaderProgram));

        shaderProgram.unbind();
    }
}
