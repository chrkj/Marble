package marble.util;

import java.io.*;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;

import de.javagl.obj.*;

import marble.editor.ConsolePanel;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import marble.entity.components.Mesh;
import marble.entity.components.Texture;

public final class Loader {

    private Loader() { }

    public static Mesh loadMeshObj(String filePath)
    {
        try {
            InputStream stream = new FileInputStream(filePath);
            Obj mesh = ObjUtils.convertToRenderable(ObjReader.read(stream));
            return new Mesh(ObjData.getVerticesArray(mesh), ObjData.getTexCoordsArray(mesh, 2),
                    ObjData.getFaceVertexIndicesArray(mesh), ObjData.getNormalsArray(mesh), filePath);
        } catch (IOException e) {
            e.printStackTrace();
            ConsolePanel.log("Error: '" + "' invalid filepath.");
            return new Mesh();
        }
    }

    public static Texture loadTexture(String filePath) {
        int width = 0;
        int height = 0;
        ByteBuffer buf = null;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            buf = stbi_load(filePath, w, h, channels, 4);
            if (buf == null)
                throw new Exception("Image file [" + filePath + "] not loaded: " + stbi_failure_reason());

            width = w.get();
            height = h.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glGenerateMipmap(GL_TEXTURE_2D);
        stbi_image_free(buf);

        return new Texture(textureId);
    }

}
