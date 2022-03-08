package launcher;

import core.*;
import core.entity.Entity;
import core.entity.Model;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL46.glViewport;

public class TestGame implements ILogic {

    private static final float CAMERA_MOVE_SPEED = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Entity entity;
    private Camera camera;

    Vector3f cameraInc;

    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init() throws Exception {

        renderer.init();

        float[] vertices = {
                //FRONT SIDE
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,//0
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                //BACK SIDE
                0.5f, -0.5f, -0.5f, 0.0f, 0.0f,//4
                -0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                //LEFT SIDE
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, //8
                -0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                //RIGHT SIDE
                0.5f, -0.5f, 0.5f, 0.0f, 0.0f, //12
                0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                //TOP SIDE
                -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,//16
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                //BOTTOM SIDE
                -0.5f, -0.5f, 0.5f, 1.0f, 0.0f,//20
                0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
        };

        int[] indices = {
                //FRONT SIDE
                0, 1, 2,
                1, 3, 2,
                //BACK SIDE
                4, 5, 6,
                5, 7, 6,
                //LEFT SIDE
                8, 9, 10,
                9, 11, 10,
                //RIGHT SIDE
                12, 13, 14,
                13, 15, 14,
                //TOP SIDE
                16, 17, 18,
                17, 19, 18,
                //BOTTOM SIDE
                20, 21, 22,
                21, 23, 22
        };

        Model model = loader.loadModel(vertices, indices);
        model.setTexture(loader.loadTexture("textures/R.png"));
        entity = new Entity(model, new Vector3f(0f, 0, -5), new Vector3f(0, 0, 0), 1);
    }

    @Override
    public void input() {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W))
            cameraInc.z = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_S))
            cameraInc.z = 1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_A))
            cameraInc.x = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_D))
            cameraInc.x = 1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT))
            cameraInc.y = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE))
            cameraInc.y = 1;
    }

    @Override
    public void update(MouseInput mouseInput) {

        camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED, cameraInc.y * CAMERA_MOVE_SPEED, cameraInc.z * CAMERA_MOVE_SPEED);

        if (mouseInput.isLeftButtonPress()) {
            Vector2f rotVec = mouseInput.getDisplacementVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        entity.incRotation(0.0f, 0.5f, 0.0f);

    }

    @Override
    public void render() {
        if (window.isResize()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(false);
        }
        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        renderer.render(entity, camera);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
