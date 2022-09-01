package me.ghostcodes.core.engine;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class Camera {

    @Getter @Setter private static int FOV = 90;

    @Getter @Setter private static float x = 0,y = 0,z = 0, vx = 0, vz = 0;
    @Getter private static final Quaternionf rotation = new Quaternionf().identity();


    public static Matrix4f getProj() {
        return new Matrix4f().identity().perspective((float) Math.toRadians(FOV),(float)Window.get().getWidth()/Window.get().getHeight(),0,1000);
    }

    public static Matrix4f getCamera() {
        return new Matrix4f().identity().rotate(rotation).translate((x+vx)*-1,y,z+vz);
    }
}
