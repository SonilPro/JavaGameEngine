package core;

import core.entity.Model;
import core.entity.Texture;
import core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.*;

class VertexBufferElement {
    int type;
    int count;

    public VertexBufferElement(int type, int count) {
        this.type = type;
        this.count = count;
    }

    static int getSizeOfType(int type) {
        return switch (type) {
            case GL_FLOAT:
                yield 4;
            case GL_UNSIGNED_BYTE:
                yield 1;
            default:
                yield 4;
        };
    }
}

class VertexBufferLayout {
    List<VertexBufferElement> elements = new ArrayList<>();
    int stride = 0;

    void push(int count, int type) {
        elements.add(new VertexBufferElement(type, count));
        stride += count * VertexBufferElement.getSizeOfType(type);
    }
}

public class ObjectLoader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    private VertexBufferLayout layout;

    public Model loadOBJModel(String filename) {
        List<String> lines = Utils.readAllLines(filename);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    //vertices
                    Vector3f verticesVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(verticesVec);
                    break;
                case "vt":
                    //vertex textures
                    Vector2f textureVec = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textures.add(textureVec);
                    break;
                case "vn":
                    //vertex  normals
                    Vector3f normalsVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normals.add(normalsVec);
                    break;
                case "f":
                    //faces
                    processFace(tokens[1], faces);
                    processFace(tokens[2], faces);
                    processFace(tokens[3], faces);
                    break;
                default:
                    break;
            }
        }
        List<Integer> indices = new ArrayList<>();
        float[] verticesArr = new float[vertices.size() * 5];
        int i = 0;
        for (Vector3f pos : vertices) {
            verticesArr[i * 5] = pos.x;
            verticesArr[i * 5 + 1] = pos.y;
            verticesArr[i * 5 + 2] = pos.z;
            i++;
        }
        float[] normalArr = new float[vertices.size() * 3];

        for (Vector3i face : faces) {
            processVertex(face.x, face.y, face.z, textures, normals, indices, normalArr, verticesArr);
        }

        int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesArr, indicesArr);
    }

    private static void processVertex(int pos, int texCoord, int normal, List<Vector2f> texCoordList, List<Vector3f> normalList,
                                      List<Integer> indicesList, float[] normalArr, float[] verticesArr) {
        indicesList.add(pos);

        if (texCoord >= 0) {
            Vector2f texCoordVec = texCoordList.get(texCoord);
            verticesArr[pos * 5 + 3] = texCoordVec.x;
            verticesArr[pos * 5 + 4] = texCoordVec.y;
        }

        if (normal >= 0) {
            Vector3f normalVec = normalList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;
        }

    }

    private static void processFace(String token, List<Vector3i> faces) {
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int pos = -1, coords = -1, normal = -1;
        pos = Integer.parseInt(lineToken[0]) - 1;
        if (length > 1) {
            String textCoord = lineToken[1];
            coords = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : -1;
            if (length > 2)
                normal = Integer.parseInt(lineToken[2]) - 1;
        }
        Vector3i facesVec = new Vector3i(pos, coords, normal);
        faces.add(facesVec);
    }

    public Model loadModel(float[] vertices, int[] indices) {
        int id = createVAO();
        storeIndexBuffer(indices);
        storeVertexBuffer(vertices);
        layout = new VertexBufferLayout();
        layout.push(3, GL_FLOAT);
        layout.push(2, GL_FLOAT);
        addBuffer();
        unbind();
        return new Model(id, indices.length);
    }

    public Texture loadTexture(String filename, float numberOfRows) throws Exception {
        int width, height;
        ByteBuffer buffer;
        STBImage.stbi_set_flip_vertically_on_load(true);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);
            buffer = STBImage.stbi_load(filename, w, h, c, 4);
            if (buffer == null) {
                throw new Exception("Image file " + filename + " not loaded " + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }
        int id = glGenTextures();
        textures.add(id);
        glBindTexture(GL_TEXTURE_2D, id);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
        STBImage.stbi_image_free(buffer);
        return new Texture(id, numberOfRows);
    }

    private int createVAO() {
        int id = glGenVertexArrays();
        vaos.add(id);
        glBindVertexArray(id);
        return id;
    }

    private void storeIndexBuffer(int[] indices) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private void storeVertexBuffer(float[] data) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private void addBuffer() {
        int offset = 0;
        int i = 0;
        for (VertexBufferElement element : layout.elements) {
            glVertexAttribPointer(i, element.count, element.type, false, layout.stride, offset);
            glEnableVertexAttribArray(i);
            i++;
            offset += element.count * VertexBufferElement.getSizeOfType(element.type);
        }
    }

    private void unbind() {
        glBindVertexArray(0);
    }

    public void cleanup() {
        for (int vao : vaos) {
            glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            glDeleteBuffers(vbo);
        }
        for (int texture : textures) {
            glDeleteTextures(texture);
        }
        int i = 0;
        for (VertexBufferElement element : layout.elements) {
            glDisableVertexAttribArray(i);
            i++;
        }
    }

}
