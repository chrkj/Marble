package marble.scene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import marble.editor.ConsolePanel;
import marble.entity.Entity;
import marble.entity.Transform;
import marble.entity.components.camera.PerspectiveCamera;
import org.joml.Vector3f;
import org.yaml.snakeyaml.Yaml;

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
        try {
            mapper.writeValue(new File("assets/scenes/" + scene.getSaveName() + ".marble"), scene);
            ConsolePanel.log("Scene saved.");
        } catch (IOException e) {
            ConsolePanel.log("Failed to save scene: " + scene.getSaveName());
            e.printStackTrace();
        }
    }

    public Scene deSerialize(String filePath)
    {
        Scene deserializedScene = null;
        //filePath = "assets/scenes/empty_scene.marble"; // Temp: For debugging
        try {
            InputStream io = new FileInputStream(filePath);
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(io);

            String sceneName = (String)data.get("name");
            double specularPower = (double)data.get("specularPower");
            Map<String, Object> al = (Map<String, Object>)data.get("ambientLight");
            double x = (double)al.get("x");
            double y = (double)al.get("y");
            double z = (double)al.get("z");
            Vector3f ambientLight = new Vector3f((float)x, (float)y, (float)z);
            deserializedScene = new Scene(sceneName, (float)specularPower, ambientLight);

            // TODO: Deserialize and add scene variables and entities to deserializedScene.

            List<Map> entities = (List<Map>) data.get("entities");

            for (Map entity : entities)
            {
                String entityName = (String)entity.get("name");
                Entity newEntity = new Entity(entityName);
                newEntity.transform = loadTransform((Map)entity.get("transform"));

                var components = (Map)entity.get("components");
                loadComponents(newEntity, components);

                deserializedScene.addEntityToScene(newEntity);
            }

            ConsolePanel.log("Loading scene: " + filePath);
        } catch (IOException e) {
            ConsolePanel.log("Failed to load scene: " + filePath);
            e.printStackTrace();
        }
        return deserializedScene;
    }

    private void loadComponents(Entity newEntity, Map components)
    {
        for (Object key : components.keySet())
        {
            String componentName = (String)key;
            Map componentsData = (Map)components.get(key);
            switch (componentName) {
                //TODO: Load rest of components
                case "marble.entity.components.camera.PerspectiveCamera":
                    PerspectiveCamera camera = new PerspectiveCamera();
                    double near = (double)componentsData.get("near");
                    double far = (double)componentsData.get("far");
                    double fov = (double)componentsData.get("fov");
                    camera.near = (float) near;
                    camera.far = (float) far;
                    camera.fov = (float) fov;
                    newEntity.addComponent(camera);
            }
        }
    }

    private Transform loadTransform(Map transformData)
    {
        Map posMap = (Map)transformData.get("position");
        double[] position = {(double)posMap.get("x"), (double)posMap.get("y"), (double)posMap.get("z")};
        Map rotMap = (Map)transformData.get("rotation");
        double[] rotation = {(double)posMap.get("x"), (double)rotMap.get("y"), (double)rotMap.get("z")};
        Map scaleMap = (Map)transformData.get("scale");
        double[] scale = {(double)scaleMap.get("x"), (double)scaleMap.get("y"), (double)scaleMap.get("z")};
        return new Transform(
                new Vector3f((float) position[0], (float) position[1], (float) position[2]),
                new Vector3f((float) rotation[0], (float) rotation[1], (float) rotation[2]),
                new Vector3f((float) scale[0], (float) scale[1], (float) scale[2]));
    }

}
