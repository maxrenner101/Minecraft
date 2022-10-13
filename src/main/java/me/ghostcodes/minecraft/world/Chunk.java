package me.ghostcodes.minecraft.world;

import lombok.Getter;
import lombok.Setter;
import me.ghostcodes.minecraft.world.blocks.Block;
import me.ghostcodes.minecraft.world.blocks.BlockType;

import java.util.ArrayList;
import java.util.List;

public class Chunk {
    public static final int WIDTH = 16, HEIGHT = 256, LENGTH = 16;
    @Getter @Setter private boolean loaded = false;
    @Getter private final List<Block> blocks = new ArrayList<>();

    @Getter private final int x, y = 0, z;

    public Chunk(int x, int z, WorldType worldType){
        this.x = x;
        this.z = z;

        if(worldType == WorldType.VOID){}

        if(worldType == WorldType.FLAT){
            for(int lY = 0; lY < Chunk.HEIGHT; lY++){
                for(int lX = 0; lX < Chunk.WIDTH; lX++){
                    for(int lZ = 0; lZ < Chunk.LENGTH; lZ++){
                        blocks.add(new Block(lX,lY,lZ, (lY > 15 && lY <= 20) ? BlockType.DIRT : (lY == 21) ? BlockType.GRASS : (lY <= 15) ? BlockType.STONE : BlockType.AIR));
                    }
                }
            }
        }

        if(worldType == WorldType.NORMAL){}
    }
}
