package core.entity;

public class Texture {

    private final int id;
    private final float numberOfRows;

    public Texture(int id, float numberOfRows) {
        this.id = id;
        this.numberOfRows = numberOfRows;
    }

    public int getId() {
        return id;
    }

    public float getNumberOfRows() {
        return numberOfRows;
    }
}
