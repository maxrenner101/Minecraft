package me.ghostcodes.minecraft.world.blocks;

public enum BlockType {
    AIR(0),
    STONE(1),
    GRASS(2),
    DIRT(3);

    private final int type;

    int getAsInt(){
        return type;
    }
    BlockType(int type){
        this.type = type;
    }
}
