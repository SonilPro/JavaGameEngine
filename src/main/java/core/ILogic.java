package core;

public interface ILogic {

    void init() throws Exception;

    void input(MouseInput mouseInput);

    void update();

    void render();

    void cleanup();
}
