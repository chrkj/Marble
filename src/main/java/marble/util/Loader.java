package marble.util;

import java.util.List;
import java.util.ArrayList;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

import org.joml.Vector2f;
import org.joml.Vector3f;

import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import marble.entity.components.Mesh;
import marble.entity.components.Texture;

public final class Loader {

    private Loader() { }

    public static Mesh loadMeshOBJ(String filePath)
    {
        boolean initArrays = true;
        boolean enableSmoothShading = false;

        int[] indicesArray = null;
        float[] normalsArray = null;
        float[] textureArray = null;
        float[] verticesArray = null;

        List<Integer> indices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();

        try {
            String line;
            BufferedReader sc = new BufferedReader(new FileReader(filePath));
            while ((line = sc.readLine()) != null) {
                if (!line.startsWith("v") && !line.startsWith("vn") && !line.startsWith("vt") && !line.startsWith("f") && !line.startsWith("S"))
                    continue;

                String[] data = line.split(" ");
                switch (data[0]) {
                    case "v" -> vertices.add(new Vector3f(
                            Float.parseFloat(data[1]),
                            Float.parseFloat(data[2]),
                            Float.parseFloat(data[3])
                    ));
                    case "vn" -> normals.add(new Vector3f(
                            Float.parseFloat(data[1]),
                            Float.parseFloat(data[2]),
                            Float.parseFloat(data[3])
                    ));
                    case "vt" -> textures.add(new Vector2f(
                            Float.parseFloat(data[1]),
                            Float.parseFloat(data[2])
                    ));
                    case "s" -> { if(line.contains("on")) enableSmoothShading = true; }
                    case "f" ->
                            {
                                if (initArrays) {
                                    textureArray = new float[vertices.size() * 2];
                                    normalsArray = new float[vertices.size() * 3];
                                    initArrays = false;
                                }

                                String[] vertex1 = data[1].split("/");
                                String[] vertex2 = data[2].split("/");
                                String[] vertex3 = data[3].split("/");

                                processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
                                processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
                                processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
                            }
                }
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        indicesArray = new int[indices.size()];
        verticesArray = new float[vertices.size() * 3];

        int vertexPointer = 0;
        for (Vector3f vertex : vertices) {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        for (int i = 0; i < indices.size(); i++)
            indicesArray[i] = indices.get(i);

        return new Mesh(verticesArray, textureArray, indicesArray, normalsArray, enableSmoothShading);
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

    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
                                      List<Vector3f> normals, float[] texturesArray, float[] normalsArray)
    {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);

        if (!vertexData[1].equals("")) {
            Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
            texturesArray[currentVertexPointer * 2] = currentTex.x;
            texturesArray[currentVertexPointer * 2 + 1] = 1 - currentTex.y;
        }

        Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNorm.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;
    }

}
