package marble.scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import marble.entity.components.Mesh;
import marble.entity.components.camera.Camera;
import marble.entity.components.light.Light;
import marble.entity.components.light.LightFactory;
import marble.entity.components.light.LightType;
import marble.util.Loader;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.yaml.snakeyaml.Yaml;

import marble.editor.ConsolePanel;
import marble.entity.Entity;
import marble.entity.Transform;
import marble.entity.components.camera.PerspectiveCamera;

public class SceneSerializer {

    private final ObjectMapper mapper;

    public SceneSerializer()
    {
        mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
    }

    public void serialize(Scene scene)
    {
        try
        {
            mapper.writeValue(new File("assets/scenes/" + scene.getSaveName() + ".marble"), scene);
            ConsolePanel.log("Scene saved.");
        }
        catch (IOException e)
        {
            ConsolePanel.log("Failed to save scene: " + scene.getSaveName());
            e.printStackTrace();
        }
    }

    public Scene deSerialize(String filePath)
    {
        Scene deserializedScene = null;
        try
        {
            InputStream io = new FileInputStream(filePath);
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(io);

            String sceneName = extractString(data, "name");
            float specularPower = extractFloat(data, "specularPower");
            Vector3f ambientLight = extractVec3f(data, "ambientLight");

            deserializedScene = new Scene(sceneName, specularPower, ambientLight);

            LinkedHashMap<Map, Map> entities = extractMap(data,"entities");
            for (Map entity : entities.values())
            {
                Entity newEntity = new Entity(extractInt(entity, "uuid"));

                newEntity.name = extractString(entity,"name");
                newEntity.transform = loadTransform(extractMap(entity,"transform"));

                var components = extractMap(entity, "components");
                loadComponents(newEntity, components);

                List children = extractList(entity, "children");
                if (children.size() > 0)
                    recursiveChildCall(children, newEntity);

                deserializedScene.addEntityToScene(newEntity);
            }
            ConsolePanel.log("Loading scene: " + filePath);
        }
        catch (IOException e)
        {
            ConsolePanel.log("Failed to load scene: " + filePath);
        }
        return deserializedScene;
    }

    private void recursiveChildCall(List<Map> childEntities, Entity parent)
    {
        for (Map entity : childEntities)
        {
            Entity newEntity = new Entity(extractInt(entity, "uuid"));
            newEntity.setParent(parent);

            newEntity.name = extractString(entity,"name");
            newEntity.transform = loadTransform(extractMap(entity,"transform"));

            var components = extractMap(entity, "components");
            loadComponents(newEntity, components);

            List children = extractList(entity, "children");
            if (children.size() > 0)
                recursiveChildCall(children, newEntity);
        }
    }

    private void loadComponents(Entity newEntity, Map components)
    {
        for (Object key : components.keySet())
        {
            String componentName = (String) key;
            Map componentData = (Map) components.get(key);
            switch (componentName)
            {
                case "marble.entity.components.Mesh"                     -> newEntity.addComponent(loadMesh(componentData));
                case "marble.entity.components.camera.PerspectiveCamera" -> newEntity.addComponent(loadPerspectiveCamera(componentData));
                case "marble.entity.components.light.DirectionalLight"   -> newEntity.addComponent(loadDirectionalLight(componentData));
            }
        }
    }

    private Light loadDirectionalLight(Map componentData)
    {
        Light light = LightFactory.getLight(LightType.DIRECTIONAL);
        light.setIntensity(extractFloat(componentData, "intensity"));
        light.setColor(extractVec4f(componentData, "color"));
        return LightFactory.getLight(LightType.DIRECTIONAL);
    }

    private Mesh loadMesh(Map componentData)
    {
        String filePath = (String) componentData.get("filePath");
        return Loader.loadMeshObj(filePath);
    }

    private Camera loadPerspectiveCamera(Map componentsData)
    {
        PerspectiveCamera camera = new PerspectiveCamera();
        camera.near = extractFloat(componentsData, "near");
        camera.far =  extractFloat(componentsData, "far");;
        camera.fov =  extractFloat(componentsData, "fov");;
        return camera;
    }

    private Transform loadTransform(Map transformData)
    {
        Map posMap = (Map) transformData.get("position");
        double[] position = { (double) posMap.get("x"), (double) posMap.get("y"), (double) posMap.get("z") };
        Map rotMap = (Map) transformData.get("rotation");
        double[] rotation = { (double) rotMap.get("x"), (double) rotMap.get("y"), (double) rotMap.get("z") };
        Map scaleMap = (Map) transformData.get("scale");
        double[] scale = { (double) scaleMap.get("x"), (double) scaleMap.get("y"), (double) scaleMap.get("z") };
        return new Transform(
                new Vector3f((float) position[0], (float) position[1], (float) position[2]),
                new Vector3f((float) rotation[0], (float) rotation[1], (float) rotation[2]),
                new Vector3f((float) scale[0], (float) scale[1], (float) scale[2]));
    }

    private Vector3f extractVec3f(Map data, String key)
    {
        Map<String, Object> comp = (Map<String, Object>) data.get(key);
        double x = (double) comp.get("x");
        double y = (double) comp.get("y");
        double z = (double) comp.get("z");
        return new Vector3f((float) x, (float) y, (float) z);
    }

    private Vector4f extractVec4f(Map data, String key)
    {
        Map<String, Object> comp = (Map<String, Object>) data.get(key);
        double x = (double) comp.get("x");
        double y = (double) comp.get("y");
        double z = (double) comp.get("z");
        double w = (double) comp.get("w");
        return new Vector4f((float) x, (float) y, (float) z, (float) w);
    }

    private String extractString(Map data, String key)
    {
        return (String) data.get(key);
    }

    private float extractFloat(Map data, String key)
    {
        double dValue = (double) data.get(key);
        return (float) dValue;
    }

    private int extractInt(Map data, String key)
    {
        return (int) data.get(key);
    }

    private List extractList(Map data, String key)
    {
        return (List) data.get(key);
    }

    private LinkedHashMap extractMap(Map data, String key)
    {
        return (LinkedHashMap) data.get(key);
    }

}
