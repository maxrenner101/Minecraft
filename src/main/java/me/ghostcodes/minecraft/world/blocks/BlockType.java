package me.ghostcodes.minecraft.world.blocks;

public enum BlockType {
    AIR(0),
    STONE(1),
    GRASS(2),
    DIRT(3),
    WATER(4),
    CLAY(5);

    private final int type;

    public int getAsInt(){
        return type;
    }
    BlockType(int type){
        this.type = type;
    }
}
