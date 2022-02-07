package marble.scene;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import marble.imgui.Console;

import java.io.File;
import java.io.IOException;

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
            Console.log("Scene saved.");
        } catch (IOException e) {
            Console.log("Failed to save scene: " + scene.getName());
            e.printStackTrace();
        }
    }

    public void deSerialize(String filePath)
    {
    }
}
