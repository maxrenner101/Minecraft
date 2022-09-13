package me.ghostcodes.minecraft.game.world.blocks;

import me.ghostcodes.minecraft.engine.rendering.Cube;
import org.joml.Vector3f;

public class Block extends Cube {

    public Block(int x, int y, int z){
        super(new Vector3f(x,y,z));
    }
}
