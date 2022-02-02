package game;

import org.joml.Vector3f;

import marble.scene.Scene;
import marble.util.Loader;
import marble.camera.Camera;
import marble.entity.Entity;
import marble.entity.components.light.LightType;
import marble.entity.components.light.LightFactory;

public class EditorScene extends Scene {

    @Override
    public void init()
    {
        mainCamera = new Camera(new Vector3f(0f, 0f, 30f));
        editorCamera = new Camera(new Vector3f(20f, 0f, 26f));
        editorCamera.rotate(7f,-35f,-30f);
        ambientLight.set(0.1f);
        {
            Entity entity = createEntity("Helm")
                    .setPosition(3,-2,3)
                    .addComponent(Loader.loadMeshObj("assets/obj/helm.obj"));
            addEntityToScene(entity);
        }
        {
            Entity entity = createEntity("Directional light")
                    .setPosition(-1,2,4)
                    .setRotation(333,53,0)
                    .addComponent(LightFactory.getLight(LightType.DIRECTIONAL));
            addEntityToScene(entity);
        }
    }

    @Override
    public void update(float dt)
    {
    }

}
