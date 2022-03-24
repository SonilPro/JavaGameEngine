package core.entity;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private int id;
    private int vertexCount;
    private Texture texture;
    private List<Vector2f> texCoordinates;

    public Model(int id, int vertexCount) {
        this.id = id;
        this.vertexCount = vertexCount;
        texCoordinates = new ArrayList<>();
        texCoordinates.add(new Vector2f(16, 32));
        texCoordinates.add(new Vector2f(0, 0));
        texCoordinates.add(new Vector2f(0, 0));
        texCoordinates.add(new Vector2f(250, 25));
        texCoordinates.add(new Vector2f(0, 0));
        texCoordinates.add(new Vector2f(5, 5));
    }

    public Model(int id, int vertexCount, Texture texture) {
        this.id = id;
        this.vertexCount = vertexCount;
        this.texture = texture;
    }

    public Model(Model model, Texture texture) {
        this.id = model.getId();
        this.vertexCount = model.getVertexCount();
        this.texture = texture;
    }

    public int getId() {
        return id;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public List<Vector2f> getTexCoordinates() {
        return texCoordinates;
    }
}
