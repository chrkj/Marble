package marble.scene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import marble.editor.ConsolePanel;

public class SceneManager {

    public SceneManager()
    {
    }

    public void serialize(Scene scene)
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        try {
            mapper.writeValue(new File("assets/scenes/" + scene.getName() + ".marble"), scene);
            ConsolePanel.log("Scene saved.");
        } catch (IOException e) {
            ConsolePanel.log("Failed to save scene: " + scene.getName());
            e.printStackTrace();
        }
    }

    public Scene deSerialize(String filePath)
    {
        Scene deserializedScene = null;
        filePath = "assets/scenes/empty_scene.marble"; // Temp: For debugging
        try {
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String sceneName = source.substring(7, source.indexOf("\"",7));
            deserializedScene = new Scene(sceneName) {
                @Override
                public void init()
                {
                }

                @Override
                public void update(float dt)
                {
                }
            };

            // TODO: Deserialize and add scene variables and entities to deserializedScene.

            ConsolePanel.log("Loading scene: " + filePath);
        } catch (IOException e) {
            ConsolePanel.log("Failed to load scene: " + filePath);
            e.printStackTrace();
        }
        return deserializedScene;
    }

    public void changeScene(Scene scene)
    {
    }

}
