package me.ghostcodes.minecraft.world;

import lombok.Getter;
import lombok.Setter;
import me.ghostcodes.math.PerlinNoiseGenerator;

import java.util.Arrays;
import java.util.Random;

public class World {

    @Getter private final WorldType type;
    private final int loadSize = 36;
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

    public void loadChunksAroundPos(int x, int z){

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

    private void unloadChunk(Chunk chunk){
        for(int i = 0; i < loadedChunks.length; i++){
            if(loadedChunks[i] == chunk)
                loadedChunks[i] = null;
        }
    }
}
