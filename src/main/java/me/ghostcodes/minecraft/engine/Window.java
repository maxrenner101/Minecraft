package me.ghostcodes.minecraft.engine;

import lombok.Getter;
import lombok.Setter;
import me.ghostcodes.minecraft.engine.io.KeyListener;
import me.ghostcodes.minecraft.engine.io.MouseListener;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private static Window instance;
    @Getter private String title = "Minecraft";
    @Getter private int width = 1920, height = 1080;
    @Getter private float aspectRatio = (float) width/height;
    @Getter private boolean fullscreen;
    @Setter @Getter private boolean toggled;
    @Getter private long window;

    private Window() {
        try {
            if(!glfwInit()){
                throw new IllegalStateException("GLFW failed to initialize.");
            }

            GLFWErrorCallback.createPrint(System.err).set();

            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

            window = glfwCreateWindow(width, height, title, NULL, NULL);
            if(window == NULL){
                throw new IllegalStateException("Window failed to initialize.");
            }

            glfwMakeContextCurrent(window);
        } catch(IllegalStateException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(window,vidMode.width()/2 - this.width/2, vidMode.height()/2 - this.height/2);

        glfwSwapInterval(1);
        glfwSetCursorPosCallback(window, MouseListener::cursorPositionCallback);
        glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
        glfwSetKeyCallback(window, KeyListener::keyCallback);
        glfwShowWindow(window);
        GL.createCapabilities();
    }

    public void setSize(int width, int height){
        this.width = width;
        this.height = height;
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(window,vidMode.width()/2 - this.width/2, vidMode.height()/2 - this.height/2);
        glfwSetWindowSize(window, width, height);

        aspectRatio = (float)this.height/this.width;
    }

    public void toggleFullscreen(){
        this.fullscreen = !this.fullscreen;

        if(fullscreen){
            glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, width,height, glfwGetVideoMode(glfwGetPrimaryMonitor()).refreshRate());
        } else {

            glfwSetWindowMonitor(window, NULL, 0, 0, width, height, glfwGetVideoMode(glfwGetPrimaryMonitor()).refreshRate());

            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window,vidMode.width()/2 - this.width/2, vidMode.height()/2 - this.height/2);

        }
    }

    public void setTitle(String title){
        this.title = title;
        glfwSetWindowTitle(window, title);
    }

    public static Window get() {
        if(instance == null)
            instance = new Window();
        return instance;
    }
}
