package me.ghostcodes.core.minecraft.world;

import lombok.Getter;
import me.ghostcodes.core.engine.rendering.Quad;
import me.ghostcodes.core.engine.rendering.Vertex;
import me.ghostcodes.core.minecraft.world.blocks.Block;

import java.util.ArrayList;
import java.util.Arrays;

public class Chunk {

    public final static int LENGTH = 1, WIDTH = 1, HEIGHT = 1, MAX_CUBES = LENGTH * WIDTH * HEIGHT;
    @Getter
    private final int x, y, z;
    @Getter private final Block[] blocks = new Block[MAX_CUBES];

    public Chunk(int x, int y, int z){
        this.x = x*WIDTH;
        this.y = y*HEIGHT;
        this.z = z*LENGTH;
        ArrayList<Block> blocksTemp = new ArrayList<>();
        for(int xA = 0; xA < WIDTH; xA++){
            for(int yA = 0; yA < HEIGHT; yA++){
                for(int zA = 0; zA < LENGTH; zA++){
                    Block block = new Block(xA,yA,zA, this);
                    blocksTemp.add(block);
                }
            }
        }

        for(int i = 0; i < blocks.length; i++){
            blocks[i] = blocksTemp.get(i);
            blocks[i].setCubeOffset(i);
        }

    }
}
