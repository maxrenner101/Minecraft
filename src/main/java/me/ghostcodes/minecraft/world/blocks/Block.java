package me.ghostcodes.minecraft.world.blocks;

import lombok.Getter;
import lombok.Setter;
import me.ghostcodes.rendering.VoxelBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Block {
    @Getter @Setter private BlockType type;
    @Getter @Setter private int x,y,z;

    public Block(int x,int y,int z,BlockType type){
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public List<Integer> getData(List<Block> blocks, int voxelBatchHeight){
        List<Integer> data = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            int render = 1;
            if(type == BlockType.AIR)
                render = 0;
            else {
                for (Block block : blocks) {
                    int testBlockX = x + ((i == 2) ? -1 : (i == 3) ? 1 : 0);
                    int testBlockY = y + ((i == 0) ? 1 : (i == 1) ? -1 : 0);
                    int testBlockZ = z + ((i == 4) ? 1 : (i == 5) ? -1 : 0);
                    if (block.x == testBlockX && block.y == testBlockY && block.z == testBlockZ && block.getType() != BlockType.AIR) {
                        render = 0;
                        break;
                    }
                }
            }
            data.add(x | (y - voxelBatchHeight) << 5 | z << 10 | type.getAsInt() << 15 | i << 19);
            data.add(render);
            data.add(x | (y - voxelBatchHeight) << 5 | z << 10 | type.getAsInt() << 15 | i << 19 | 1 << 22);
            data.add(render);
            data.add(x | (y - voxelBatchHeight) << 5 | z << 10 | type.getAsInt() << 15 | i << 19 | 2 << 22);
            data.add(render);
            data.add(x | (y - voxelBatchHeight) << 5 | z << 10 | type.getAsInt() << 15 | i << 19 | 3 << 22);
            data.add(render);
        }

        return data;
    }
}
