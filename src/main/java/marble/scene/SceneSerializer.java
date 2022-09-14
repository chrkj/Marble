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

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.yaml.snakeyaml.Yaml;

import marble.editor.ConsolePanel;
import marble.entity.Entity;
import marble.entity.Transform;
import marble.entity.components.camera.PerspectiveCamera;
import marble.entity.Material;
import marble.entity.components.Mesh;
import marble.entity.components.camera.Camera;
import marble.entity.components.light.*;
import marble.util.Loader;

@SuppressWarnings("rawtypes")
public class SceneSerializer
{
    private SceneSerializer() { }

    public static Scene copyScene(Scene scene)
    {
        var mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);

        Scene newScene = null;
        var File = new File("assets/scenes/temp/tmp.marble");
        try
        {
            mapper.writeValue(File, scene);
            newScene = deSerialize(File);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return newScene;
    }

    public static void serialize(Scene scene)
    {
        var mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);

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

    // TODO: Fix duplicate methods
    public static Scene deSerialize(String filePath)
    {
        if (!filePath.endsWith(".marble")) return null;
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

                if (loadScript(entity) != null)
                    newEntity.setScript(loadScript(entity));

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

    public static Scene deSerialize(File file)
    {
        Scene deserializedScene = null;
        try
        {
            InputStream io = new FileInputStream(file);
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

                if (loadScript(entity) != null)
                    newEntity.setScript(loadScript(entity));

                var components = extractMap(entity, "components");
                loadComponents(newEntity, components);

                List children = extractList(entity, "children");
                if (children.size() > 0)
                    recursiveChildCall(children, newEntity);

                deserializedScene.addEntityToScene(newEntity);
            }
        }
        catch (IOException e)
        {
            ConsolePanel.log("Failed to deserialize scene: " + file.getAbsolutePath());
        }
        return deserializedScene;
    }

    private static void recursiveChildCall(List<Map> childEntities, Entity parent)
    {
        for (Map entity : childEntities)
        {
            Entity newEntity = new Entity(extractInt(entity, "uuid"));
            newEntity.setParent(parent);

            newEntity.name = extractString(entity,"name");
            newEntity.transform = loadTransform(extractMap(entity,"transform"));

            if (loadScript(entity) != null)
                newEntity.setScript(loadScript(entity));

            var components = extractMap(entity, "components");
            loadComponents(newEntity, components);

            List children = extractList(entity, "children");
            if (children.size() > 0)
                recursiveChildCall(children, newEntity);
        }
    }

    private static void loadComponents(Entity newEntity, Map components)
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
                case "marble.entity.components.light.PointLight"         -> newEntity.addComponent(loadPointLight(componentData));
                case "marble.entity.components.light.SpotLight"          -> newEntity.addComponent(loadSpotLight(componentData));
            }
        }
    }

    private static String loadScript(Map componentData)
    {
       return extractString(componentData, "scriptName");
    }

    private static PointLight loadPointLight(Map componentData)
    {
        PointLight pl = (PointLight) LightFactory.getLight(LightType.POINT);
        pl.setIntensity(extractFloat(componentData, "intensity"));
        pl.setColor(extractVec4f(componentData, "color"));
        pl.linear = extractFloat(componentData, "linear");
        pl.constant = extractFloat(componentData, "constant");
        pl.exponent = extractFloat(componentData, "exponent");
        return pl;
    }

    private static SpotLight loadSpotLight(Map componentData)
    {
        SpotLight sp = (SpotLight) LightFactory.getLight(LightType.SPOT);
        sp.setIntensity(extractFloat(componentData, "intensity"));
        sp.setColor(extractVec4f(componentData, "color"));
        sp.setCutOff(extractFloat(componentData, "cutOff"));
        var pointLightData = extractMap(componentData, "pointLight");
        sp.setPointLight(loadPointLight(pointLightData));
        return sp;
    }

    private static DirectionalLight loadDirectionalLight(Map componentData)
    {
        DirectionalLight dl = (DirectionalLight) LightFactory.getLight(LightType.DIRECTIONAL);
        dl.setIntensity(extractFloat(componentData, "intensity"));
        dl.setColor(extractVec4f(componentData, "color"));
        return dl;
    }

    private static Mesh loadMesh(Map componentData)
    {
        String filePath = (String) componentData.get("filePath");
        Mesh mesh = Loader.loadMeshObj(filePath);
        Material material = new Material();
        Map materialData = extractMap(componentData, "material");
        material.setAmbient(extractVec4f(materialData, "ambient"));
        material.setSpecular(extractVec4f(materialData, "specular"));
        material.setDiffuse(extractVec4f(materialData, "diffuse"));
        material.setReflectance(extractFloat(materialData, "reflectance"));
        mesh.setMaterial(material);
        return mesh;
    }

    private static Camera loadPerspectiveCamera(Map componentsData)
    {
        PerspectiveCamera camera = new PerspectiveCamera();
        camera.near = extractFloat(componentsData, "near");
        camera.far = extractFloat(componentsData, "far");
        camera.fov = extractFloat(componentsData, "fov");
        return camera;
    }

    private static Transform loadTransform(Map transformData)
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

    private static Vector3f extractVec3f(Map data, String key)
    {
        Map<String, Object> comp = (Map<String, Object>) data.get(key);
        double x = (double) comp.get("x");
        double y = (double) comp.get("y");
        double z = (double) comp.get("z");
        return new Vector3f((float) x, (float) y, (float) z);
    }

    private static Vector4f extractVec4f(Map data, String key)
    {
        Map<String, Object> comp = (Map<String, Object>) data.get(key);
        double x = (double) comp.get("x");
        double y = (double) comp.get("y");
        double z = (double) comp.get("z");
        double w = (double) comp.get("w");
        return new Vector4f((float) x, (float) y, (float) z, (float) w);
    }

    private static String extractString(Map data, String key)
    {
        return (String) data.get(key);
    }

    private static float extractFloat(Map data, String key)
    {
        double dValue = (double) data.get(key);
        return (float) dValue;
    }

    private static int extractInt(Map data, String key)
    {
        return (int) data.get(key);
    }

    private static List extractList(Map data, String key)
    {
        return (List) data.get(key);
    }

    private static LinkedHashMap extractMap(Map data, String key)
    {
        return (LinkedHashMap) data.get(key);
    }

}
