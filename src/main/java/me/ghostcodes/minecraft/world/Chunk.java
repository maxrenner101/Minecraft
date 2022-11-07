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

    public Chunk(int x, int z, WorldType worldType, PerlinNoiseGenerator perlinNoiseGenerator) {
        this.x = x;
        this.z = z;

        if (worldType == WorldType.VOID) {
            int i = 0;
            for (int lY = 0; lY < Chunk.HEIGHT; lY++) {
                for (int lX = 0; lX < Chunk.WIDTH; lX++) {
                    for (int lZ = 0; lZ < Chunk.LENGTH; lZ++) {
                        blocks[i] = new Block(lX, lY, lZ, BlockType.AIR, x,z);
                        i++;
                    }
                }
            }
        }

        if (worldType == WorldType.FLAT) {
            int i = 0;
            for (int lY = 0; lY < Chunk.HEIGHT; lY++) {
                for (int lX = 0; lX < Chunk.WIDTH; lX++) {
                    for (int lZ = 0; lZ < Chunk.LENGTH; lZ++) {
                        blocks[i] = new Block(lX, lY, lZ, (lY > 15 && lY <= 20) ? BlockType.DIRT : (lY == 21) ? BlockType.GRASS : (lY <= 15) ? BlockType.STONE : BlockType.AIR, x * Chunk.WIDTH, z * Chunk.LENGTH);
                        i++;
                    }
                }
            }
        }

        if (worldType == WorldType.NORMAL) {
            int i = 0;
            for (int lY = 0; lY < Chunk.HEIGHT; lY++) {
                int maxWaterHeight = 120;
                for (int lX = 0; lX < Chunk.WIDTH; lX++) {
                    for (int lZ = 0; lZ < Chunk.LENGTH; lZ++) {
                        double nNoise = (((perlinNoiseGenerator.noise(((this.x * Chunk.WIDTH) + lX) / 64d, ((this.z * Chunk.LENGTH) + lZ) / 64d) + 1d) * (0.5d)) / 2d) + 0.25d;
                        int top = (int) (nNoise * 255);
                        boolean water = top < 120;
                        Block block;
                        if(!water)
                            block = new Block(lX, lY, lZ, (lY == top) ? BlockType.GRASS : (lY < top - 4) ? BlockType.STONE : (lY < top && lY >= top - 4) ? BlockType.DIRT : BlockType.AIR, x, z);
                        else {
                            block = new Block(lX, lY, lZ, (lY > maxWaterHeight) ? BlockType.AIR : (lY > top) ? BlockType.WATER : (lY > top-4) ? BlockType.DIRT : (lY <= top-4) ? BlockType.STONE : BlockType.AIR, x, z);
                        }
                        blocks[i] = block;
                        i++;
                    }
                }
            }
        }
    }

    public Block getBlock(int x, int y, int z){
        for(Block block : blocks){
            if(block.getX() == x && block.getY() == y && block.getZ() == z)
                return block;
        }

        return null;
    }

    public boolean containsBlockAt(int x, int y, int z){
        return (x >= this.x*Chunk.WIDTH && x <= this.x*Chunk.WIDTH+Chunk.WIDTH && z >= this.z*Chunk.LENGTH && z <= this.z*Chunk.LENGTH+Chunk.LENGTH && y <= Chunk.HEIGHT+this.y && y >= this.y);
    }
}
