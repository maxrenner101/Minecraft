package me.ghostcodes.minecraft.engine.shaders;

import lombok.Getter;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    @Getter private final ShaderType type;
    @Getter private final String code;
    @Getter private int shaderId;
    public Shader(ShaderType type, String code){
        this.type = type;
        this.code = code;
        try {
            this.shaderId = glCreateShader(type.getType());
            if (this.shaderId == 0) {
                throw new IllegalStateException("Shader failed to create: " + type);
            }

            glShaderSource(shaderId, code);
            glCompileShader(shaderId);
            if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0){
                throw new IllegalStateException("Error compiling shader code: " + glGetShaderInfoLog(shaderId, 1024));
            }
        } catch(IllegalStateException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
