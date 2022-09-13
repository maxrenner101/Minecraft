package me.ghostcodes.minecraft.game.world;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class World {
    @Getter private List<Chunklet> chunks = new ArrayList<>();

    public World(){

    }
}
