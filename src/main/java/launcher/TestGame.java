package launcher;

import core.*;
import core.entity.Entity;
import core.entity.Model;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

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
    Vector2f rotVec;

    ImGuiLayer imGuiLayer;

    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        rotVec = new Vector2f(0, 0);
        imGuiLayer = new ImGuiLayer();
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
                //LEFT SIDE
                8, 9, 10,
                9, 11, 10,
                //FRONT SIDE
                0, 1, 2,
                1, 3, 2,
                //RIGHT SIDE
                12, 13, 14,
                13, 15, 14,
                //BACK SIDE
                4, 5, 6,
                5, 7, 6,
                //TOP SIDE
                16, 17, 18,
                17, 19, 18,
                //BOTTOM SIDE
                20, 21, 22,
                21, 23, 22
        };

        Model model = loader.loadModel(vertices, indices);
        //Model model = loader.loadOBJModel("/models/human.obj");
        model.setTexture(loader.loadTexture("textures/blocks.png", 16));
        entity = new Entity(model, new Vector3f(0f, 0, -5), new Vector3f(0, 0, 0), 1);
    }

    @Override
    public void input(MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        rotVec.set(0, 0);

        if (ImGui.getIO().getWantCaptureMouse()) {
            return;
        }

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


        if (mouseInput.isLeftButtonPress()) {
            rotVec = mouseInput.getDisplacementVec();
        }
    }

    @Override
    public void update() {

        camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED, cameraInc.y * CAMERA_MOVE_SPEED, cameraInc.z * CAMERA_MOVE_SPEED);

        camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

        /*entity.incRotation(0.0f, 0.5f, 0.0f);*/

    }

    @Override
    public void render() {
        if (window.isResize()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(false);
        }
        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        renderer.render(entity, camera);

        GL46.glPolygonMode(GL46.GL_FRONT_AND_BACK, GL46.GL_FILL);

        window.getImGuiGlfw().newFrame();
        ImGui.newFrame();

        imGuiLayer.imguiEntity(entity);

        ImGui.render();
        window.getImGuiGl3().renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}
