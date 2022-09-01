package me.ghostcodes.core.engine;

import lombok.Getter;
import me.ghostcodes.core.engine.io.KeyboardListener;
import me.ghostcodes.core.engine.io.MouseListener;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private Window(){}

    private static Window instance;
    @Getter private String title = "Minecraft";
    @Getter private int width = 1280, height = 720;
    @Getter private boolean fullscreen;

    @Getter private long window;

    public static Window get(){
        if(instance == null){
            instance = new Window();
        }

        return instance;
    }

    public void build(){
        if(!glfwInit()) {
            throw new IllegalStateException("Error initializing glfw.");
        }

        GLFWErrorCallback.createPrint(System.err).set();

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if(window == NULL){
            throw new IllegalStateException("Error creating window.");
        }

        try (MemoryStack stack = stackPush()){
            IntBuffer tWidth = stack.mallocInt(1), tHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, tWidth, tHeight);

            GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert vid != null;
            glfwSetWindowPos(window, (vid.width()-tWidth.get(0))/2, (vid.height()-tHeight.get(0))/2);

        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();

        glfwSetKeyCallback(window, KeyboardListener::keyCallback);
        glfwSetCursorPosCallback(window, MouseListener::cursor_position_callback);
        glfwSetMouseButtonCallback(window, MouseListener::mouse_button_callback);
        glfwSetCursorPos(window, 0,0);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

    }

    public void setSize(int width, int height){
        this.width = width;
        this.height = height;

        glfwSetWindowSize(window, this.width, this.height);
        try (MemoryStack stack = stackPush()){
            IntBuffer tWidth = stack.mallocInt(1), tHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, tWidth, tHeight);

            GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert vid != null;
            glfwSetWindowPos(window, (vid.width()-tWidth.get(0))/2, (vid.height()-tHeight.get(0))/2);
            GL11.glViewport(0,0,width,height);

        }
    }

    public void setTitle(String title) {
        this.title = title;

        glfwSetWindowTitle(window, this.title);
    }

    public void toggleFullscreen(){

        if(fullscreen){
            fullscreen= false;
            glfwSetWindowMonitor(window, NULL, 0,0,width,height,-1);
            try (MemoryStack stack = stackPush()){
                IntBuffer tWidth = stack.mallocInt(1), tHeight = stack.mallocInt(1);

                GL11.glViewport(0,0,width,height);
                glfwGetWindowSize(window, tWidth, tHeight);

                GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());

                assert vid != null;
                glfwSetWindowPos(window, (vid.width()-tWidth.get(0))/2, (vid.height()-tHeight.get(0))/2);
            }
            setSize(1280,720);
        } else {
            fullscreen = true;
            GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
            assert vid != null;
            this.height = vid.height();
            this.width = vid.width();
            GL11.glViewport(0,0,width,height);

            glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0,0,width,height, -1);
        }
    }

}
