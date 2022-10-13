package me.ghostcodes.minecraft.world;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class World {

    @Getter private final WorldType type;
    @Getter private final List<Chunk> loadedChunks = new ArrayList<>();
    @Getter private int seed;
    @Getter @Setter private boolean updatedChunks;

    public World(WorldType type){
        this.type = type;
        if(type == WorldType.NORMAL)
            seed = seed();
    }

    public void loadChunksAroundPos(int x, int z){
        for(int i = x - 1; i < x + 1; i++){
            for(int j = z - 1; j < z + 1; j++){
                System.out.println(i);
                System.out.println(j);
                loadChunk(new Chunk(i,j,type));
            }
        }
    }

    public World(WorldType type, int seed) {
        this.type = type;
        this.seed = seed;
    }

    private int seed(){
        return 0;
    }

    private void loadChunk(Chunk chunk){
        loadedChunks.add(chunk);
        if(!updatedChunks) updatedChunks = true;
    }

    private void unloadChunk(Chunk chunk){
        loadedChunks.remove(chunk);
        if(!updatedChunks) updatedChunks = true;
    }
}
