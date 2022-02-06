package game;

import marble.entity.components.camera.PerspectiveCamera;

import marble.scene.Scene;
import marble.util.Loader;
import marble.entity.Entity;
import marble.entity.components.light.LightType;
import marble.entity.components.light.LightFactory;

public class emptyScene extends Scene {

    @Override
    public void init()
    {
        ambientLight.set(0.1f);
        {
            Entity entity = createEntity("Main Camera")
                    .setPosition(3f, -2.5f, 35f)
                    .addComponent(new PerspectiveCamera());
            addEntityToScene(entity);
        }
        {
            Entity entity = createEntity("Entity")
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
