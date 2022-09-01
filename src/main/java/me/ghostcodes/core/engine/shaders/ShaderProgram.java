package me.ghostcodes.core.engine.shaders;

import me.ghostcodes.core.Main;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private final int programId;
    private int vertexShaderId, fragmentShaderId;
    private HashMap<String, Integer> uniforms = new HashMap<>();

    public ShaderProgram(String vertexShaderCode, String fragmentShaderCode){
        programId = glCreateProgram();
        if(programId == 0){
            throw new IllegalStateException("Could not create Shader Program");
        }

        String vertexCode = parseShaderCode(vertexShaderCode);
        String fragmentCode = parseShaderCode(fragmentShaderCode);


        try {
            createVertexShader(vertexCode);
            createFragmentShader(fragmentCode);

            link();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String parseShaderCode(String code){
        StringBuilder newCode = new StringBuilder();

        Scanner scanner = new Scanner(code);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            if(line.startsWith("#")){
                if(line.toLowerCase().contains("type")){
                    if(!line.toLowerCase().contains("vertex") && !line.toLowerCase().contains("fragment")){
                        throw new IllegalStateException("Shader does not contain a proper type.");
                    }
                } else {
                    newCode.append(line).append("\n");
                }
            } else {
                newCode.append(line).append("\n");
            }
        }

        return newCode.toString();
    }

    public void createUniform(String name) throws Exception {
        int uniformLocation = glGetUniformLocation(programId, name);
        if(uniformLocation < 0) throw new Exception("Couldn't find uniform in shaders: " + name);

        uniforms.put(name, uniformLocation);
    }

    public void setUniform(String name, Matrix4f value){
        try (MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer fb = stack.mallocFloat(16);
            value.get(fb);
            glUniformMatrix4fv(uniforms.get(name), false, fb);
        }
    }

    public void setUniform(String name, int value){
        glUniform1iv(uniforms.get(name), new int[]{value});
    }

    public void setUniform(String name, Vector3f value){
        glUniform3fv(uniforms.get(name), new float[]{value.x,value.y,value.z});
    }

    private void createVertexShader(String code) throws Exception {
        vertexShaderId = createShader(code, GL_VERTEX_SHADER);
    }

    private void createFragmentShader(String code) throws Exception {
        fragmentShaderId = createShader(code, GL_FRAGMENT_SHADER);
    }

    private int createShader(String code, int shaderType) throws Exception{
        int shaderId = glCreateShader(shaderType);
        if(shaderId == 0){
            throw new Exception("Error creating shader of type: " + shaderType);
        }

        glShaderSource(shaderId, code);
        glCompileShader(shaderId);

        if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0){
            throw new Exception("Error compiling shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    private void link() throws Exception {
        glLinkProgram(programId);
        if(glGetProgrami(programId, GL_LINK_STATUS) == 0){
            throw new Exception("Error linking shader program: " + glGetProgramInfoLog(programId, 1024));
        }

        glValidateProgram(programId);
        if(glGetProgrami(programId, GL_VALIDATE_STATUS) == 0){
            System.err.println("Warning validating shader code: " + glGetProgramInfoLog(programId, 1024));
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if(programId != 0){
            glDeleteProgram(programId);
        }
    }

    public static ShaderProgram buildProgram(String vertexFilePath, String fragmentFilePath){


        StringBuilder vertexCode = new StringBuilder();
        StringBuilder fragmentCode = new StringBuilder();

        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(vertexFilePath); InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8); BufferedReader reader = new BufferedReader(streamReader)){

            String line;
            while((line = reader.readLine()) != null){
                vertexCode.append(line).append("\n");
            }

        } catch(IOException e){
            e.printStackTrace();
        }

        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(fragmentFilePath); InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8); BufferedReader reader = new BufferedReader(streamReader)){

            String line;
            while((line = reader.readLine()) != null){
                fragmentCode.append(line).append("\n");
            }

        } catch(IOException e){
            e.printStackTrace();
        }

        return new ShaderProgram(vertexCode.toString(), fragmentCode.toString());
    }
}
