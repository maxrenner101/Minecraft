package me.ghostcodes.core.minecraft;

import me.ghostcodes.core.engine.rendering.ChunkBatch;
import me.ghostcodes.core.engine.rendering.Renderer;
import me.ghostcodes.core.minecraft.world.Chunk;

public class WorldGenerator {

    public WorldGenerator(Renderer renderer){
        Chunk chunk = new Chunk(0,0,0);
        renderer.getChunkBatches().add(new ChunkBatch(chunk));
    }
}
