package me.ghostcodes.minecraft.game.world;

import lombok.Getter;
import me.ghostcodes.minecraft.game.world.blocks.Block;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Chunklet {
    @Getter private static final int WIDTH = 16, HEIGHT = 16, LENGTH = 16, SIZE = WIDTH*HEIGHT*LENGTH;

    @Getter private final List<Block> blocks = new ArrayList<>();

    @Getter private final int x, y, z;

    public Chunklet(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;

        for(int xL = 0; xL < WIDTH; xL++){
            for(int yL = 0; yL < HEIGHT; yL++){
                for(int zL = 0; zL < LENGTH; zL++){
                    blocks.add(new Block(xL, yL, zL));
                }
            }
        }
    }

    public Vector3f getLocationAsVector() {
        return new Vector3f(x*WIDTH,y*HEIGHT,z*LENGTH);
    }
}
