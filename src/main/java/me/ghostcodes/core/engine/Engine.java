package me.ghostcodes.core.engine;

import lombok.Getter;
import lombok.Setter;
import me.ghostcodes.core.engine.io.MouseListener;
import me.ghostcodes.core.engine.rendering.Renderer;
import me.ghostcodes.core.engine.shaders.ShaderProgram;
import me.ghostcodes.core.engine.util.Function;
import me.ghostcodes.core.engine.util.FunctionDouble;
import me.ghostcodes.core.minecraft.WorldGenerator;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;

public class Engine {

    private static Engine instance;
    @Getter private Window window;
    private Renderer renderer;
    @Getter private ShaderProgram program;

    @Getter @Setter private double FPS = 240, UPS = 60;

    private Engine() {}

    public static Engine get(){
        if(instance == null){
            instance = new Engine();
        }

        return instance;
    }

    public void run(FunctionDouble update) {
        window = Window.get();

        window.build();

        program = ShaderProgram.buildProgram("shaders/vertex.glsl", "shaders/fragment.glsl");

        renderer = new Renderer();

        new WorldGenerator(renderer);

        try {
            Engine.get().getProgram().createUniform("u_Proj");
            Engine.get().getProgram().createUniform("u_ChunkLocation");
            Engine.get().getProgram().createUniform("u_Camera");
        } catch (Exception e){
            e.printStackTrace();
        }

        mainLoop(update, renderer);
    }

    public void mainLoop(FunctionDouble update, Function render) {

        glClearColor(0,0,0,1);

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);


        double lastTime = glfwGetTime();
        int frames = 0;

        while(!glfwWindowShouldClose(window.getWindow())){
            MouseListener.handleInput();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            update.apply(0);
            render.apply();
            frames++;

            glfwSwapBuffers(window.getWindow());

            glfwPollEvents();

            if(glfwGetTime() - lastTime > 1.0){
                System.out.println("FPS: " + frames);
                frames = 0;
                lastTime += 1.0;
            }

        }
    }
}
