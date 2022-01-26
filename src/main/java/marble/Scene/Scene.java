package marble.scene;

import java.util.List;
import java.util.ArrayList;

import org.joml.Vector3f;

import imgui.ImGui;
import imgui.flag.ImGuiDataType;
import imgui.type.ImFloat;

import marble.Window;
import marble.util.Time;
import marble.camera.Camera;
import marble.entity.Entity;
import marble.imgui.ImGuiLayer;
import marble.renderer.Renderer;
import marble.entity.components.Mesh;
import marble.entity.components.light.Light;
import marble.entity.components.light.SpotLight;
import marble.entity.components.light.PointLight;
import marble.entity.components.light.DirectionalLight;

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
    private ImFloat[] reflectance;
    private float sceneStartedTime;
    private boolean isRunning = false;
    private final Renderer renderer = new Renderer();

    protected Camera mainCamera = new Camera();
    protected float specularPower = 10;
    protected final List<Light> lights = new ArrayList<>();
    protected final List<Entity> entities = new ArrayList<>();
    protected final Vector3f ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

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
        InitImGuiDebugLayer(); // TODO: (Temp) Should be handled in ImGuiLayer
    }

    public void updateScene(float dt)
    {
        for (Entity entity : entities)
            entity.update(dt);
        update(dt);
        updateImGuiLayer(); // TODO: (Temp) Should be handled in ImGuiLayer
        renderer.render(mainCamera, lights);
    }

    public void cleanUp()
    {
        for (Entity entity : entities)
            entity.cleanUp();
    }

    public Vector3f getAmbientLight()
    {
        return ambientLight;
    }

    public float getSpecularPower()
    {
        return specularPower;
    }

    protected Entity createEntity()
    {
        return new Entity();
    }

    protected Entity createEntity(String tag)
    {
        return new Entity(tag);
    }

    protected void addEntityToScene(Entity entity)
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

    protected void setSpecularPower(float specularPower)
    {
        this.specularPower = specularPower;
    }

    protected void setAmbientLight(float r, float g, float b, float a)
    {
        ambientLight.x = r;
        ambientLight.y = g;
        ambientLight.z = b;
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
        reflectance = new ImFloat[entities.size()];
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
            reflectance[i] = new ImFloat(entities.get(i).material.getReflectance());
        }
    }

    private void updateImGuiLayer()
    {
        ImGui.begin("Game Objects");
        ImGui.text("Camera");
        ImGui.text(String.format("Position: %.3f %.3f %.3f", mainCamera.getPosition().x, mainCamera.getPosition().y, mainCamera.getPosition().z));
        ImGui.text(String.format("Rotation: %.3f %.3f %.3f", mainCamera.getRotation().x, mainCamera.getRotation().y, mainCamera.getRotation().z));
        ImGui.spacing();
        ImGui.spacing();
        int totalVertexCount = 0;
        for (int i = 0; i < entities.size(); i++) {
            totalVertexCount += entities.get(i).getComponent(Mesh.class).getVertexCount();
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
            ImGui.text("Material");
            ImGui.sliderScalar("Reflect" + i, ImGuiDataType.Float, reflectance[i], 0f, 10f);

            ImGui.spacing();
            ImGui.spacing();

            entities.get(i).transform.setRotation(xRot[i].get(), yRot[i].get(), zRot[i].get());
            entities.get(i).transform.setScale(xScale[i].get(), yScale[i].get(), zScale[i].get());
            entities.get(i).transform.setPosition(xTrans[i].get(), yTrans[i].get(), zTrans[i].get());
            entities.get(i).material.setReflectance(reflectance[i].get());
        }
        ImGuiLayer.totalVertexCount = totalVertexCount;
        ImGui.end();
    }

}
