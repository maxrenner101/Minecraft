package me.ghostcodes.minecraft.world;

import lombok.Getter;
import lombok.Setter;
import me.ghostcodes.math.PerlinNoiseGenerator;
import me.ghostcodes.minecraft.world.blocks.Block;

import java.util.Arrays;
import java.util.Random;

public class World {

    @Getter private final WorldType type;
    private final int loadSize = 512;
    private final int chunkRange = (int) Math.sqrt(loadSize)/2;
    @Getter private final Chunk[] loadedChunks = new Chunk[loadSize];
    @Getter private long seed;
    @Getter @Setter private boolean updatedChunks;
    private final PerlinNoiseGenerator perlinNoiseGenerator;

    public World(WorldType type){
        this.type = type;
        if(type == WorldType.NORMAL)
            seed = seed();
        perlinNoiseGenerator = new PerlinNoiseGenerator(seed);
    }

    private boolean loading;
    private ChunkLoader loader;

    public void loadChunksAroundPos(int x, int z){
        if(!loading){
            loading = true;
            loader = new ChunkLoader(x,z);
            loader.start();
        } else {
            if(!loader.isAlive()){
                loading = false;
            }
        }
    }

    private class ChunkLoader extends Thread {

        private final int x, z;

        public ChunkLoader(int x, int z){
            this.x = x;
            this.z = z;
        }

        @Override
        public void run(){
            Chunk[] newLoaded = new Chunk[loadSize];
            for(int i = x - chunkRange; i < x + chunkRange; i++){
                for(int j = z - chunkRange; j < z + chunkRange; j++){
                    boolean load = true;
                    for(Chunk c : loadedChunks) {
                        if(c == null)
                            continue;
                        if (c.getX() == i && c.getZ() == j) {
                            load = false;
                            for(int k = 0; k < newLoaded.length; k++){
                                if (newLoaded[k] == null) {
                                    newLoaded[k] = c;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if(load){
                        if(!updatedChunks) updatedChunks = true;
                        Chunk c = new Chunk(i,j,type, perlinNoiseGenerator);
                        for(int k = 0; k < newLoaded.length; k++){
                            if(newLoaded[k] == null){
                                newLoaded[k] = c;
                                break;
                            }
                        }
                    }
                }
            }

            Arrays.fill(loadedChunks, null);
            for (Chunk loadedChunk : loadedChunks) {
                if (loadedChunk == null) continue;
                boolean unload = true;
                for (Chunk newLoadedChunk : newLoaded) {
                    if (loadedChunk.getX() == newLoadedChunk.getX() && loadedChunk.getZ() == newLoadedChunk.getZ()) {
                        unload = false;
                        break;
                    }
                }
                if (unload)
                    unloadChunk(loadedChunk);
            }

            for(Chunk chunk : newLoaded){
                loadChunk(chunk);
            }
        }
    }

    public World(WorldType type, long seed) {
        this.type = type;
        this.seed = seed;
        perlinNoiseGenerator = new PerlinNoiseGenerator(seed);
    }

    private long seed(){
        return new Random().nextLong();
    }

    private void loadChunk(Chunk chunk){
        for(int k = 0; k < loadedChunks.length; k++){
            if(loadedChunks[k] == null){
                loadedChunks[k] = chunk;
                break;
            }
        }
    }

    public Block getBlock(int x, int y, int z){
        for(Chunk chunk : loadedChunks){
            if(chunk == null) continue;
            if(chunk.containsBlockAt(x, y, z)){
                return chunk.getBlock(x,y,z);
            }
        }
        return null;
    }

    private void unloadChunk(Chunk chunk){
        for(int i = 0; i < loadedChunks.length; i++){
            if(loadedChunks[i] == chunk)
                loadedChunks[i] = null;
        }
    }
}
