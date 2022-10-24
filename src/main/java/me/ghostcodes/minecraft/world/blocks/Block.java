package me.ghostcodes.minecraft.world.blocks;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Block {
    @Getter @Setter private BlockType type;
    @Getter private final int localX, localY, localZ, x,y,z;
    public Block(int x,int y,int z,BlockType type, int chunkLocX, int chunkLocY, int chunkLocZ){
        this.type = type;
        this.localX = x;
        this.localY = y;
        this.localZ = z;
        this.x = chunkLocX + localX;
        this.y = chunkLocY + localY;
        this.z = chunkLocZ + localZ;
    }

    public List<Integer> getData(Map<Integer, Block> blocks, int voxelBatchHeight){
        List<Integer> data = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            int render = 1;
            if(type != BlockType.AIR){
                int testBlockX = localX + ((i == 2) ? -1 : (i == 3) ? 1 : 0);
                int testBlockY = localY + ((i == 0) ? 1 : (i == 1) ? -1 : 0);
                int testBlockZ = localZ + ((i == 4) ? 1 : (i == 5) ? -1 : 0);
                int key = testBlockX | testBlockY << 6 | testBlockZ << 15;
                if(blocks.containsKey(key)) {
                    Block block = blocks.get(key);
                    if (block.getType() != BlockType.AIR) {
                        render = 0;
                    }
                }
                data.add(localX | (localY - voxelBatchHeight) << 5 | localZ << 10 | type.getAsInt() << 15 | i << 19);
                data.add(render);
                data.add(localX | (localY - voxelBatchHeight) << 5 | localZ << 10 | type.getAsInt() << 15 | i << 19 | 1 << 22);
                data.add(render);
                data.add(localX | (localY - voxelBatchHeight) << 5 | localZ << 10 | type.getAsInt() << 15 | i << 19 | 2 << 22);
                data.add(render);
                data.add(localX | (localY - voxelBatchHeight) << 5 | localZ << 10 | type.getAsInt() << 15 | i << 19 | 3 << 22);
                data.add(render);
            }
        }

        return data;
    }
}
