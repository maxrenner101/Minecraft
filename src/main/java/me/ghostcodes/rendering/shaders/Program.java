package me.ghostcodes.rendering.shaders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.lwjgl.opengl.GL20.*;

public class Program {

    public static int createShaderProgram(File[] shaders) {
        int id = glCreateProgram();
        if(id == 0) throw new IllegalStateException("Error creating shader program");

        for(File shaderPath : shaders){
            int shaderId;
            int shaderType = -1;
            StringBuilder code = new StringBuilder();
            try {
                String shaderCode = Files.readString(shaderPath.toPath());
                String[] splitShaderCode = shaderCode.split("\n");
                for(String line : splitShaderCode){
                    if(line.contains("#type")){
                        if(line.contains("vertex")){
                            shaderType = GL_VERTEX_SHADER;
                        } else if(line.contains("fragment")){
                            shaderType = GL_FRAGMENT_SHADER;
                        } else {
                            throw new IllegalStateException("Error finding type of shader: " + shaderPath);
                        }
                    } else {
                        code.append(line).append("\n");
                    }
                }
                if(shaderType == -1) throw new IllegalStateException("Error finding type of shader: " + shaderPath);

                shaderId = glCreateShader(shaderType);
                if(shaderId == 0) throw new IllegalStateException("Error creating shader: " + shaderPath);
                glShaderSource(shaderId,code.toString());
                glCompileShader(shaderId);
                if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) throw new IllegalStateException("Error compiling shader: " + shaderPath + ": " + glGetShaderInfoLog(shaderId, 1024));
                glAttachShader(id,shaderId);

            } catch (IOException e){
                e.printStackTrace();
                throw new IllegalStateException("Error finding shader code: " + shaderPath);
            }
        }

        glLinkProgram(id);
        if(glGetProgrami(id,GL_LINK_STATUS) == 0) throw new IllegalStateException("Error linking program: " + glGetProgramInfoLog(id, 1024));

        glValidateProgram(id);
        if(glGetProgrami(id,GL_VALIDATE_STATUS) == 0) throw new IllegalStateException("Error validating program: " + glGetProgramInfoLog(id, 1024));

        return id;
    }

}
