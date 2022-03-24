package core;

import core.entity.Entity;
import core.utils.Transformation;
import launcher.Launcher;

import static org.lwjgl.opengl.GL46.*;

public class RenderManager {

    private final WindowManager window;
    private ShaderManager shader;

    public RenderManager() {
        window = Launcher.getWindow();
    }


    public void init() throws Exception {
        shader = new ShaderManager();
        shader.createShader("/shaders/simpleShader.shader");
        shader.link();
        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("numberOfRows");
        shader.createUniform("locationOffset");
    }

    public void render(Entity entity, Camera camera) {
        clear();
        shader.bind();
        shader.setUniform("textureSampler", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(entity));
        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
        shader.setUniform("numberOfRows", entity.getModel().getTexture().getNumberOfRows());

        glBindVertexArray(entity.getModel().getId());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, entity.getModel().getTexture().getId());

        for (int i = 0; i < entity.getModel().getTexCoordinates().size(); i++) {
            shader.setUniform("locationOffset", entity.getModel().getTexCoordinates().get(i));
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (long) i * 6 * VertexBufferElement.getSizeOfType(GL_UNSIGNED_INT));
        }

        //Unbind
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
        shader.unbind();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        shader.cleanup();
    }
}
