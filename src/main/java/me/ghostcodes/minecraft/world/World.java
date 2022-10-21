package me.ghostcodes.minecraft.world;

import lombok.Getter;
import lombok.Setter;
import me.ghostcodes.math.PerlinNoiseGenerator;

import java.util.ArrayList;
import java.util.List;

public class World {

    @Getter private final WorldType type;
    @Getter private final List<Chunk> loadedChunks = new ArrayList<>();
    @Getter private int seed;
    @Getter @Setter private boolean updatedChunks;
    private PerlinNoiseGenerator perlinNoiseGenerator;

    public World(WorldType type){
        this.type = type;
        if(type == WorldType.NORMAL)
            seed = seed();
        perlinNoiseGenerator = new PerlinNoiseGenerator();
    }

    public void loadChunksAroundPos(int x, int z){

        List<Chunk> newLoaded = new ArrayList<>();
        for(int i = x - 4; i < x + 4; i++){
            for(int j = z - 4; j < z + 4; j++){
                boolean load = true;
                for(Chunk c : loadedChunks) {
                    if (c.getX() == i && c.getZ() == j) {
                        newLoaded.add(c);
                        load = false;
                        break;
                    }
                }
                if(load){
                    Chunk c = new Chunk(i,j,type, perlinNoiseGenerator);
                    if(!updatedChunks) updatedChunks = true;
                    newLoaded.add(c);
                }
            }
        }

        loadedChunks.clear();
        for(Chunk chunk : newLoaded){
//            for(Chunk lChunk : loadedChunks){
//                boolean unload = true;
//                if(lChunk.getX() == chunk.getX() && lChunk.getZ() == chunk.getZ())
//                    unload = false;
//            }

            loadChunk(chunk);
        }
    }

    public World(WorldType type, int seed) {
        this.type = type;
        this.seed = seed;
        perlinNoiseGenerator = new PerlinNoiseGenerator();
    }

    private int seed(){
        return 0;
    }

    private void loadChunk(Chunk chunk){
        loadedChunks.add(chunk);
    }

    private void unloadChunk(Chunk chunk){
        loadedChunks.remove(chunk);
        if(!updatedChunks) updatedChunks = true;
    }
}
