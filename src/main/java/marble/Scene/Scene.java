package marble.scene;

import java.util.List;
import java.util.ArrayList;

import org.joml.Vector4f;

import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImFloat;
import marble.Window;
import marble.imgui.ImGuiLayer;
import marble.util.Time;
import marble.camera.Camera;
import marble.renderer.Renderer;
import marble.gameobject.Entity;
import marble.gameobject.components.light.Light;
import marble.gameobject.components.light.SpotLight;
import marble.gameobject.components.light.PointLight;
import marble.gameobject.components.light.DirectionalLight;

public abstract class Scene {

    private ImFloat[] xTrans;
    private ImFloat[] yTrans;
    private ImFloat[] zTrans;
    private ImFloat[] xRot;
    private ImFloat[] yRot;
    private ImFloat[] zRot;
    private ImFloat[] xScale;
    private ImFloat[] yScale;
    private ImFloat[] zScale;
    private float sceneStartedTime;
    private boolean isRunning = false;
    private boolean debugMode = false;
    private final Renderer renderer = new Renderer();
    private final Vector4f ambientLight = new Vector4f(0f, 0f, 0f, 0f);

    protected Camera mainCamera;
    protected final List<Light> lights = new ArrayList<>();
    protected final List<Entity> entities = new ArrayList<>();

    public abstract void init();
    public abstract void update(float dt);

    public void start()
    {
        sceneStartedTime = Time.getTime();
        for (Entity entity : entities) {
            entity.start();
            renderer.add(entity);
        }
        isRunning = true;
        if (debugMode)
            InitImGuiDebugLayer();
    }

    public void updateScene(float dt)
    {
        for (Entity entity : entities)
            entity.update(dt);
        update(dt);
        if (debugMode) {
            updateImGuiLayer();
            ImGuiLayer.updateDiagnosticLayer(dt);
        }
        renderer.render(mainCamera, lights);
    }

    public void cleanUp()
    {
        for (Entity entity : entities)
            entity.cleanUp();
    }

    public Vector4f getAmbientLight()
    {
        return ambientLight;
    }

    protected Entity createEntity()
    {
        return new Entity();
    }

    protected Entity createEntity(String tag)
    {
        return new Entity(tag);
    }

    protected void addGameObjectToScene(Entity entity)
    {
        if (!isRunning) {
            entities.add(entity);
        } else {
            entities.add(entity);
            entity.start();
            renderer.add(entity);
        }

        if (entity.hasComponent(SpotLight.class))
            lights.add(entity.getComponent(SpotLight.class));
        if (entity.hasComponent(PointLight.class))
            lights.add(entity.getComponent(PointLight.class));
        if (entity.hasComponent(DirectionalLight.class))
            lights.add(entity.getComponent(DirectionalLight.class));
    }

    protected void changeScene(Scene newScene)
    {
        Window.nextScene = newScene;
        Window.shouldChangeScene = true;
    }

    protected float timeSinceSceneStarted()
    {
        return Time.getTime() - sceneStartedTime;
    }

    protected Camera getMainCamera()
    {
        return mainCamera;
    }

    protected void enableDebugging()
    {
        debugMode = true;
    }

    protected void disableDebugging()
    {
        debugMode = false;
    }

    protected void setAmbientLight(float r, float g, float b, float a)
    {
        ambientLight.x = r;
        ambientLight.y = g;
        ambientLight.z = b;
        ambientLight.w = a;
    }

    private void InitImGuiDebugLayer()
    {
        xTrans = new ImFloat[entities.size()];
        yTrans = new ImFloat[entities.size()];
        zTrans = new ImFloat[entities.size()];
        xRot = new ImFloat[entities.size()];
        yRot = new ImFloat[entities.size()];
        zRot = new ImFloat[entities.size()];
        xScale = new ImFloat[entities.size()];
        yScale = new ImFloat[entities.size()];
        zScale = new ImFloat[entities.size()];

        for (int i = 0; i < entities.size(); i++) {
            xTrans[i] = new ImFloat(entities.get(i).transform.position.x);
            yTrans[i] = new ImFloat(entities.get(i).transform.position.y);
            zTrans[i] = new ImFloat(entities.get(i).transform.position.z);
            xRot[i] = new ImFloat(entities.get(i).transform.rotation.x);
            yRot[i] = new ImFloat(entities.get(i).transform.rotation.y);
            zRot[i] = new ImFloat(entities.get(i).transform.rotation.z);
            xScale[i] = new ImFloat(entities.get(i).transform.scale.x);
            yScale[i] = new ImFloat(entities.get(i).transform.scale.y);
            zScale[i] = new ImFloat(entities.get(i).transform.scale.z);
        }
    }

    private void updateImGuiLayer()
    {
        ImGui.begin("Game Objects");
        ImGui.text("Camera");
        ImGui.text(String.format("Position: %f %f %f", mainCamera.getPosition().x, mainCamera.getPosition().y, mainCamera.getPosition().z));
        ImGui.text(String.format("Rotation: %f %f %f", mainCamera.getRotation().x, mainCamera.getRotation().y, mainCamera.getRotation().z));
        ImGui.spacing();
        ImGui.spacing();
        for (int i = 0; i < entities.size(); i++) {
            ImGui.text(entities.get(i).name);
            ImGui.text("Translation");
            ImGui.sliderScalar("Tx" + i, ImGuiDataType.Float, xTrans[i], -30, 30);
            ImGui.sliderScalar("Ty" + i, ImGuiDataType.Float, yTrans[i], -30, 30);
            ImGui.sliderScalar("Tz" + i, ImGuiDataType.Float, zTrans[i], -30, 30);
            ImGui.text("Rotation");
            ImGui.sliderScalar("Rx" + i, ImGuiDataType.Float, xRot[i], 0, 360);
            ImGui.sliderScalar("Ry" + i, ImGuiDataType.Float, yRot[i], 0, 360);
            ImGui.sliderScalar("Rz" + i, ImGuiDataType.Float, zRot[i], 0, 360);
            ImGui.text("Scale");
            ImGui.sliderScalar("Sx" + i, ImGuiDataType.Float, xScale[i], 0.1f, 10);
            ImGui.sliderScalar("Sy" + i, ImGuiDataType.Float, yScale[i], 0.1f, 10);
            ImGui.sliderScalar("Sz" + i, ImGuiDataType.Float, zScale[i], 0.1f, 10);
            ImGui.spacing();
            ImGui.spacing();

            entities.get(i).transform.setRotation(xRot[i].get(), yRot[i].get(), zRot[i].get());
            entities.get(i).transform.setScale(xScale[i].get(), yScale[i].get(), zScale[i].get());
            entities.get(i).transform.setPosition(xTrans[i].get(), yTrans[i].get(), zTrans[i].get());
        }
        ImGui.end();
    }

}
