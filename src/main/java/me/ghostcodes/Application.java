package me.ghostcodes;

import lombok.Getter;
import me.ghostcodes.io.KeyListener;
import me.ghostcodes.io.MouseListener;
import me.ghostcodes.minecraft.Minecraft;
import me.ghostcodes.rendering.Renderer;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class Application extends Thread {
    private long window;
    private int width = 1920, height = 1080;
    @Getter private double FPS = 240, UPS = 60, aspectRatio = (double)width/height;
    private boolean wireFrame = false;
    private Minecraft minecraft;

    @Override
    public void run(){
        Map<Integer, Integer> windowHints = new HashMap<>();
        windowHints.put(GLFW_SAMPLES, 4);
        window = createWindow(windowHints);
        doWindowSettings();
        minecraft = new Minecraft(this::fullscreenWindow);
        loop(window, new Renderer(Main.class.getClassLoader().getResource("voxelShaders").getPath(), minecraft), minecraft);
    }

    private void loop(long window, Renderer renderer, Minecraft minecraft){
        double lastUpdate = 0, lastFrame = 0, lastPrint = 0;
        int frames = 0, updates = 0;

        glClearColor(0.5f,1,1,1);

        while(!glfwWindowShouldClose(window)){
            double currentTime = glfwGetTime();
            double timeBetweenUpdate = 1/UPS, timeBetweenFrame = 1/FPS;
            double deltaTime = currentTime-lastUpdate;
            if(deltaTime > timeBetweenUpdate){
                glfwPollEvents();
                MouseListener.pollInput();
                minecraft.apply(deltaTime);
                lastUpdate = currentTime;
                updates++;
            }

            if(currentTime - lastFrame > timeBetweenFrame){
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                renderer.render(aspectRatio);
                glfwSwapBuffers(window);
                lastFrame = currentTime;
                frames++;

            }

            if(currentTime - lastPrint > 1){
                System.out.printf("FPS: %d UPS: %d%n", frames, updates);
                frames = 0;
                updates = 0;
                lastPrint = currentTime;
            }
        }

        renderer.clean();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private long createWindow(Map<Integer, Integer> windowHints){
        if(!glfwInit()) throw new IllegalStateException("Error initializing glfw.");
        glfwDefaultWindowHints();
        windowHints.put(GLFW_VISIBLE, GLFW_FALSE);
        windowHints.forEach(org.lwjgl.glfw.GLFW::glfwWindowHint);

        return glfwCreateWindow(width,height, "Minecraft",0,0);
    }

    private void doWindowSettings(){
        glfwMakeContextCurrent(window);
        centerWindow();
        glfwSwapInterval(0);
        glfwSetCursorPos(window,0,0);
        glfwSetInputMode(window,GLFW_CURSOR,GLFW_CURSOR_DISABLED);

        glfwSetWindowSizeCallback(window, this::resizeWindow);
        glfwSetCursorPosCallback(window, MouseListener::cursor_position_callback);
        glfwSetMouseButtonCallback(window, MouseListener::mouse_button_callback);
        glfwSetKeyCallback(window, KeyListener::key_callback);

        glfwShowWindow(window);
        GL.createCapabilities();

        glEnable(GL_NORMALIZE);
        glEnable(GL_COLOR_MATERIAL);
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);
    }

    private void centerWindow(){
        GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
        assert vid != null;
        glfwSetWindowPos(window,(vid.width() - width) / 2, (vid.height() - height) / 2);
    }

    private void resizeWindow(long window, int width, int height){
        this.width = width;
        this.height = height;
        this.aspectRatio = (double) width/height;
        glfwSetWindowSize(window,width,height);
        centerWindow();
    }

    private Void fullscreenWindow(Boolean fullscreen){
        if(!fullscreen) {
            GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
            assert vid != null;
            glfwSetWindowMonitor(window,0,0,0, width, height, -1);
            centerWindow();
            return null;
        }
        glfwSetWindowMonitor(window,glfwGetPrimaryMonitor(),0,0,width,height, -1);
        return null;
    }
}
