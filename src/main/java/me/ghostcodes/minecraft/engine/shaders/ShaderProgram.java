package me.ghostcodes.minecraft.engine.shaders;

import me.ghostcodes.Main;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private int programId;

    private Map<String, Integer> uniforms;

    public ShaderProgram(List<String> shaderPaths){
        uniforms = new HashMap<>();
        try {
            programId = glCreateProgram();
            if(programId == 0){
                throw new IllegalStateException("Program failed to create.");
            }
        } catch(IllegalStateException e){
            e.printStackTrace();
            System.exit(-1);
        }

        glUseProgram(programId);

        List<Shader> shaders = new ArrayList<>();

        shaderPaths.forEach(path -> {
            try {
                Scanner s = new Scanner(new File(Objects.requireNonNull(Main.class.getClassLoader().getResource(path)).getPath()));
                StringBuilder shaderCode = new StringBuilder();
                ShaderType shaderType = null;
                while (s.hasNextLine()) {
                    String line = s.nextLine();
                    if (line.startsWith("#type")) {
                        if (line.contains("vertex")) {
                            shaderType = ShaderType.VERTEX;
                        } else if(line.contains("fragment")){
                            shaderType = ShaderType.FRAGMENT;
                        }
                    } else {
                        shaderCode.append(line).append("\n");
                    }
                }

                if(shaderType != null) {
                    shaders.add(new Shader(shaderType, shaderCode.toString()));
                }

            } catch(IOException e){
                e.printStackTrace();
            }
        });

        try {
            shaders.forEach(s -> glAttachShader(programId, s.getShaderId()));

            glLinkProgram(programId);
            if(glGetProgrami(programId, GL_LINK_STATUS) == 0){
                throw new IllegalStateException("Error linking shader program: " + glGetProgramInfoLog(programId, 1024));
            }

            shaders.forEach(s -> glDetachShader(programId, s.getShaderId()));

            glValidateProgram(programId);
            if(glGetProgrami(programId, GL_VALIDATE_STATUS) == 0){
                throw new IllegalStateException("Error validating shader program: " + glGetProgramInfoLog(programId, 1024));
            }
        } catch(IllegalStateException e){
            e.printStackTrace();
            System.exit(-1);
        }

        glUseProgram(0);

    }

    public void createUniform(String uniform){
        int loc = glGetUniformLocation(programId, uniform);
        if(!(loc < 0))
            uniforms.put(uniform, loc);
        else
            System.out.println("Uniform " + uniform + " was not created.");
    }

    public void setUniform(String uniform, Matrix4f value){
        float[] fb = new float[16];
        value.get(fb);
        glUniformMatrix4fv(uniforms.get(uniform),false, fb);
    }

    public void setUniform(String uniform, Vector3f value){
        glUniform3fv(uniforms.get(uniform), new float[]{value.x,value.y,value.z});
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void clean() {
        unbind();
        if(programId != 0){
            glDeleteProgram(programId);
        }
    }
}
