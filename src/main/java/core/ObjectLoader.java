package core;

import core.entity.Model;
import core.entity.Texture;
import core.utils.Utils;
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
        int result = switch (type) {
            case GL_FLOAT:
                yield 4;
            case GL_UNSIGNED_BYTE:
                yield 1;
            default:
                yield 4;
        };
        return result;
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

    public Model loadModel(float[] vertices, int[] indices) {
        int id = createVAO();
        storeIndexBuffer(indices);
        storeVertexBuffer(vertices);
        VertexBufferLayout layout = new VertexBufferLayout();
        layout.push(3, GL_FLOAT);
        layout.push(2, GL_FLOAT);
        addBuffer(layout);
        unbind();
        return new Model(id, indices.length);
    }

    public Texture loadTexture(String filename) throws Exception {
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
        return new Texture(id);
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

    private void addBuffer(VertexBufferLayout layout) {
        int offset = 0;
        int i = 0;
        for (VertexBufferElement element : layout.elements) {
            glVertexAttribPointer(i, element.count, element.type, false, layout.stride, offset);
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
    }

}
