package me.ghostcodes.core.engine.rendering;

import lombok.Getter;
import me.ghostcodes.core.engine.Camera;
import me.ghostcodes.core.engine.Engine;
import me.ghostcodes.core.engine.util.Function;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Renderer implements Function {

    @Getter private final ArrayList<ChunkBatch> chunkBatches = new ArrayList<>();
    public Renderer() {
    }

    @Override
    public void apply() {
        Engine.get().getProgram().bind();


        Engine.get().getProgram().setUniform("u_Proj", Camera.getProj());
        Engine.get().getProgram().setUniform("u_Camera", Camera.getCamera());
        chunkBatches.forEach((batch) -> {
            Vector3f v = new Vector3f(batch.getChunk().getX(), batch.getChunk().getY(), batch.getChunk().getZ());
            Engine.get().getProgram().setUniform("u_ChunkLocation", v);

            batch.render();
        });

        Engine.get().getProgram().unbind();
    }
}
