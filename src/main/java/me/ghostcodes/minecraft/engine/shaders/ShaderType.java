package me.ghostcodes.minecraft.engine.shaders;

import lombok.Getter;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public enum ShaderType {
    VERTEX(GL_VERTEX_SHADER),
    FRAGMENT(GL_FRAGMENT_SHADER);

    @Getter private final int type;

    ShaderType(int type){
        this.type = type;
    }
}
