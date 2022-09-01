package me.ghostcodes.core.minecraft;

import me.ghostcodes.core.engine.rendering.ChunkBatch;
import me.ghostcodes.core.engine.rendering.Renderer;
import me.ghostcodes.core.minecraft.world.Chunk;

public class WorldGenerator {

    public WorldGenerator(Renderer renderer){
        for(int x = -5; x < 5; x++){
            for(int z = -5; z < 5; z++){
                Chunk chunk = new Chunk(x,-2,z);
                renderer.getChunkBatches().add(new ChunkBatch(chunk));
            }
        }
    }
}
