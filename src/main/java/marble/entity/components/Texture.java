package marble.entity.components;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Texture extends Component {

    private final int textureId;

    public Texture(int textureId)
    {
        this.textureId = textureId;
    }

    public void bind()
    {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void unbind()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getId()
    {
        return textureId;
    }

    public void cleanUp()
    {
        glDeleteTextures(textureId);
    }

    @Override
    public void render()
    {
    }
}
