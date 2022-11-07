package me.ghostcodes;

import lombok.Getter;
import me.ghostcodes.io.KeyListener;
import me.ghostcodes.io.MouseListener;
import me.ghostcodes.minecraft.Minecraft;
import me.ghostcodes.rendering.Renderer;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;

public class Application extends Thread {
    private long window;
    private int width = 1280, height = 720;
    private boolean fullscreen = false, wireframe = false;
    @Getter private double FPS = 240, UPS = 60, aspectRatio = (double)width/height;

    @Override
    public void run(){
        Map<Integer, Integer> windowHints = new HashMap<>();
        windowHints.put(GLFW_SAMPLES, 4);
        window = createWindow(windowHints);
        doWindowSettings();
        Minecraft minecraft = new Minecraft();
        loop(window, new Renderer("voxelShaders", minecraft), minecraft);
    }

    private void loop(long window, Renderer renderer, Minecraft minecraft){
        double lastUpdate = 0, lastFrame = 0, lastPrint = 0;
        int frames = 0, updates = 0;
        boolean fullscreenToggled = false, wireframeToggled = false;

        while(!glfwWindowShouldClose(window)){
            double currentTime = glfwGetTime();
            double timeBetweenUpdate = 1/UPS, timeBetweenFrame = 1/FPS;
            double deltaTime = currentTime-lastUpdate;
            if(deltaTime > timeBetweenUpdate){
                glfwPollEvents();
                MouseListener.pollInput();
                if(KeyListener.isKeyPressed(GLFW_KEY_F11)){
                    if(!fullscreenToggled){
                        toggleFullscreen();
                        fullscreenToggled = true;
                    }
                } else {
                    fullscreenToggled = false;
                }

                if(KeyListener.isKeyPressed(GLFW_KEY_F3)){
                    if(!wireframeToggled){
                        toggleWireframe();
                        wireframeToggled = true;
                    }
                } else {
                    wireframeToggled = false;
                }
                minecraft.apply(deltaTime);
                lastUpdate = currentTime;
                updates++;
            }

            if(currentTime - lastFrame > timeBetweenFrame){
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glDisable(GL_CULL_FACE);
                glEnable(GL_BLEND);
                glEnable(GL_LINE_SMOOTH);
                glEnable(GL_COLOR_MATERIAL);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_MULTISAMPLE);
                renderer.render(aspectRatio);
                glDisable(GL_MULTISAMPLE);
                glDisable(GL_DEPTH_TEST);
                glDisable(GL_BLEND);
                glDisable(GL_LINE_SMOOTH);
                glDisable(GL_COLOR_MATERIAL);
                glEnable(GL_CULL_FACE);
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
        glCullFace(GL_BACK);
        glDepthFunc(GL_LESS);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_FLAT);
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

    private void toggleFullscreen(){
        fullscreen = !fullscreen;
        if(fullscreen) {
            glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, width, height, -1);
        } else {
            glfwSetWindowMonitor(window,0,0,0, width, height, -1);
            centerWindow();
        }
    }

    private void toggleWireframe(){
        wireframe = !wireframe;
        if(wireframe){
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
    }
}
