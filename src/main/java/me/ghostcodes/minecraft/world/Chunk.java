package me.ghostcodes.minecraft.world;

import lombok.Getter;
import lombok.Setter;
import me.ghostcodes.math.PerlinNoiseGenerator;
import me.ghostcodes.minecraft.world.blocks.Block;
import me.ghostcodes.minecraft.world.blocks.BlockType;

public class Chunk {
    public static final int WIDTH = 16, HEIGHT = 256, LENGTH = 16;
    @Getter @Setter private boolean loaded = false;
    @Getter private final Block[] blocks = new Block[WIDTH*HEIGHT*LENGTH];

    @Getter private final int x, y = 0, z;

    public Chunk(int x, int z, WorldType worldType, PerlinNoiseGenerator perlinNoiseGenerator){
        this.x = x;
        this.z = z;

        if(worldType == WorldType.VOID){}

        if(worldType == WorldType.FLAT){
            int i = 0;
            for(int lY = 0; lY < Chunk.HEIGHT; lY++){
                for(int lX = 0; lX < Chunk.WIDTH; lX++){
                    for(int lZ = 0; lZ < Chunk.LENGTH; lZ++){
                        blocks[i] = new Block(lX,lY,lZ, (lX == 10 || lX == 11) ? BlockType.AIR : (lY > 15 && lY <= 20) ? BlockType.DIRT : (lY == 21) ? BlockType.GRASS : (lY <= 15) ? BlockType.STONE : BlockType.AIR, x,y,z);
                        i++;
                    }
                }
            }
        }

        if(worldType == WorldType.NORMAL){
            int i = 0;
            for(int lY = 0; lY < Chunk.HEIGHT; lY++) {
                for (int lX = 0; lX < Chunk.WIDTH; lX++) {
                    for (int lZ = 0; lZ < Chunk.LENGTH; lZ++) {
                        double nNoise = (((perlinNoiseGenerator.noise(((this.x*Chunk.WIDTH) + lX)/96d,((this.z*Chunk.LENGTH) + lZ)/96d) + 1d) * (0.5d)) / 2d) + 0.25d;
                        int top = (int) (nNoise * 255);
                        blocks[i] = new Block(lX,lY,lZ, (lY == top) ? BlockType.GRASS : (lY < top-4) ? BlockType.STONE : (lY < top && lY >= top-4) ? BlockType.DIRT : BlockType.AIR, x,y,z);
                        i++;
                    }
                }
            }
        }
    }

    public Chunk(int x, int z, WorldType type, int seed){
        this.x = x;
        this.z = z;
    }
}
