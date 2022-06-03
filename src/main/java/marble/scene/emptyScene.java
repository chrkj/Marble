package marble.scene;

import marble.entity.components.camera.PerspectiveCamera;

import marble.util.Loader;
import marble.entity.Entity;
import marble.entity.components.light.LightType;
import marble.entity.components.light.LightFactory;

public class emptyScene extends Scene {

    public emptyScene(String name)
    {
        super(name);
    }

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
            Entity entity = createEntity("Helmet")
                    .setPosition(3,-2,3)
                    .addComponent(Loader.loadMeshObj("assets/obj/helm.obj"));
            Entity child1 = createEntity("ChildEntity1");
            Entity child2 = createEntity("ChildEntity2");
            Entity child3 = createEntity("ChildEntity3");
            child1.setChild(child2);
            entity.setChild(child1);
            entity.setChild(child3);
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
