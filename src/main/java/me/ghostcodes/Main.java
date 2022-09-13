package me.ghostcodes;

import me.ghostcodes.minecraft.engine.rendering.ChunkBatch;
import me.ghostcodes.minecraft.game.Minecraft;
import me.ghostcodes.minecraft.engine.Engine;
import me.ghostcodes.minecraft.game.world.Chunklet;
import me.ghostcodes.minecraft.game.world.WorldGenerator;

public class Main {
    public static void main(String[] args) {
        Engine.get().init();
        Minecraft minecraft = new Minecraft();
        WorldGenerator.createWorld();
        Engine.get().loop(minecraft::apply);
    }
}
