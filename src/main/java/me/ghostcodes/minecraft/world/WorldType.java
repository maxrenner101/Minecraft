package me.ghostcodes.minecraft.world;

public enum WorldType {
    VOID(0),
    FLAT(1),
    NORMAL(2);

    private int type;
    public int getAsInt(){
        return type;
    }

    WorldType(int type){
        this.type = type;
    }
}
