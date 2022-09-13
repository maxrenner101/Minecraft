package me.ghostcodes.minecraft.engine;

import lombok.Getter;
import lombok.Setter;
import me.ghostcodes.minecraft.engine.io.MouseListener;
import me.ghostcodes.minecraft.engine.rendering.Renderer;
import me.ghostcodes.minecraft.engine.shaders.ShaderProgram;
import me.ghostcodes.util.Function;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Engine {

    private static Engine instance;
    private Window window;
    @Getter private Renderer renderer;
    private ShaderProgram program;
    @Setter private int FPS = 240, UPS = 60;

    private Engine() {}

    public static Engine get() {
        if(instance == null)
            instance = new Engine();
        return instance;
    }

    public void init(){
        window = Window.get();
        renderer = Renderer.get();
        List<String> shaders = new ArrayList<>();
        shaders.add("shaders/Vertex.glsl");
        shaders.add("shaders/Fragment.glsl");
        program = new ShaderProgram(shaders);
        renderer.setShaderProgram(program);

        program.createUniform("proj");
        program.createUniform("chunkLocation");
        program.createUniform("camera");

        glfwSetCursorPos(window.getWindow(), 0,0);
        glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    public void loop(Function<Float> update){
        double timeBetweenUpdate = 1000000000.0/UPS, lastUpdate = System.nanoTime();
        long time = System.currentTimeMillis();
        int frames = 0;

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glEnable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        while(!glfwWindowShouldClose(window.getWindow())){
            double currentTime = System.nanoTime();
            float currentDT = (float) ((currentTime - lastUpdate)/timeBetweenUpdate);
            lastUpdate = currentTime;

            update(update, currentDT);

            render();
            frames++;

            if(System.currentTimeMillis() - time > 1000){
                time = System.currentTimeMillis();
                System.out.println("FPS: " + frames);
                frames = 0;
            }

        }

        glfwDestroyWindow(window.getWindow());
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void update(Function<Float> update, float dt) {
        glfwPollEvents();
        MouseListener.handleInput();
        update.apply(dt);
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        renderer.render();

        glfwSwapBuffers(window.getWindow());
    }
}
