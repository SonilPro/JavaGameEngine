package core;

import core.entity.Entity;
import imgui.ImGui;

public class ImGuiLayer {

    private boolean showText = false;

    public void imgui() {

        ImGui.begin("Cool window");

        if (ImGui.button("I am a button")) {
            showText = true;
        }
        if (showText) {
            ImGui.text("You clicked a button");
            ImGui.sameLine();
            if (ImGui.button("Stop showing text")) {
                showText = false;
            }
        }

        ImGui.end();
    }

    public void imguiEntity(Entity entity) {
        ImGui.begin("Entity window");

        float[] positions = new float[3];
        float[] rotations = new float[3];
        positions[0] = entity.getPos().x;
        positions[1] = entity.getPos().y;
        positions[2] = entity.getPos().z;
        rotations[0] = entity.getRotation().x;
        rotations[1] = entity.getRotation().y;
        rotations[2] = entity.getRotation().z;


        ImGui.sliderFloat3("position", positions, -5f, 5);
        ImGui.sliderFloat3("rotation", rotations, 0, 360);

        entity.setPos(positions[0], positions[1], positions[2]);
        entity.setRotation(rotations[0], rotations[1], rotations[2]);
        ImGui.end();
    }
}
