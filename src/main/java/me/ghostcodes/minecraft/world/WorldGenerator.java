package me.ghostcodes.minecraft.world;

public class WorldGenerator {
    public static World createWorld(WorldType type){
        return new World(type);
    }
}
