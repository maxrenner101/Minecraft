package me.ghostcodes.minecraft.game.world;

import me.ghostcodes.minecraft.engine.Engine;
import me.ghostcodes.minecraft.engine.rendering.ChunkBatch;

public class WorldGenerator {
    public static World createWorld(){
        World world = new World();
        for(int i = 0; i < 10; i++){
            for(int z = 0; z < 10; z++){
                Chunklet chunklet = new Chunklet(i,-2,z);
                world.getChunks().add(chunklet);
                Engine.get().getRenderer().addChunkBatch(new ChunkBatch(chunklet));
            }
        }
        return world;
    }
}
