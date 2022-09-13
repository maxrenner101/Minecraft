package me.ghostcodes.minecraft.engine.rendering;

import lombok.Getter;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Cube {

    @Getter private final List<Vertex> vertices = new ArrayList<>();

    public Cube(Vector3f localLocation) {

        Vector3f[] vertexs = {
                new Vector3f(0,0,0),
                new Vector3f(0,1,0),
                new Vector3f(1,1,0), // 1
                new Vector3f(1,0,0), //2
                new Vector3f(0,0,1),
                new Vector3f(0,1,1),
                new Vector3f(1,1,1), // 4
                new Vector3f(1,0,1)//3
        };

        createQuad(vertexs[0],vertexs[1],vertexs[2],vertexs[3],new Vector3f(0,0,-1),localLocation);
        createQuad(vertexs[4],vertexs[5],vertexs[6],vertexs[7],new Vector3f(0,0,1),localLocation);
        createQuad(vertexs[4],vertexs[5],vertexs[1],vertexs[0],new Vector3f(-1,0,0),localLocation);
        createQuad(vertexs[2],vertexs[3],vertexs[7],vertexs[6],new Vector3f(1,0,0),localLocation);
        createQuad(vertexs[0],vertexs[3],vertexs[7],vertexs[4],new Vector3f(0,-1,0),localLocation);
        createQuad(vertexs[1],vertexs[2],vertexs[6],vertexs[5],new Vector3f(0,1,0),localLocation);

    }

    private void createQuad(Vector3f v1,Vector3f v2,Vector3f v3,Vector3f v4, Vector3f normal, Vector3f localLocation) {
        vertices.add(new Vertex(v1,normal,localLocation));
        vertices.add(new Vertex(v2,normal,localLocation));
        vertices.add(new Vertex(v3,normal,localLocation));
        vertices.add(new Vertex(v4,normal,localLocation));
    }
}
