package me.ghostcodes.core;

import me.ghostcodes.core.engine.Engine;
import me.ghostcodes.core.minecraft.Minecraft;

public class Main {
    public static void main(String[] args) {

        Minecraft minecraft = new Minecraft();
        Engine.get().run(minecraft);

    }
}
