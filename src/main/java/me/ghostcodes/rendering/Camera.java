package me.ghostcodes.rendering;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class Camera {
    private static Camera instance;
    @Getter @Setter private float FOV = 100f;
    @Getter @Setter private float x = 0, y = 140, z = 0, vx = 0, vz = 0, lastX = 0, lastY = 0, lastZ = 0;
    @Getter private final Quaternionf rotation = new Quaternionf();
    private Matrix4f projection = null;

    private Camera(){}

    public static Camera get(){
        return (instance == null) ? instance = new Camera() : instance;
    }

    public Matrix4f getProjection(double aspectRatio) {
        return (projection == null) ? projection = new Matrix4f().perspective((float)Math.toRadians(FOV), (float) aspectRatio,0.01f, 1000) : projection;
    }

    public Matrix4f getView(){
        return new Matrix4f().rotate(rotation).translate(-1*(vx+x),-y,vz+z);
    }
}
