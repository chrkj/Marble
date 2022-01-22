package marble.renderer;

import java.lang.Math;
import java.util.HashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.FloatBuffer;

import org.joml.*;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

import marble.entity.Material;
import marble.entity.components.light.Light;

public class Shader {

    private int shaderProgramID;
    private boolean inUse = false;
    private String vertexSource;
    private String fragmentSource;

    private final String filepath;
    private final HashMap<String, Integer> uniformLocationCache = new HashMap<>();

    public Shader(String filepath)
    {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first shader text after "#type"
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // Find the second shader text after "#type"
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filepath + "'";
        }
    }

    public void compile()
    {
        int vertexID, fragmentID;

        // Load and compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        // Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE)
        {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        // Load and compile fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        // Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE)
        {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        // Link shaders
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE)
        {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tLinking shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void bind()
    {
        if (!inUse) {
            inUse = true;
            glUseProgram(shaderProgramID);
        }
    }

    public void unbind()
    {
        glUseProgram(0);
        inUse = false;
    }

    public void setUniform1i(String varName, int value)
    {
        int varLocation = getUniformLocation(varName);
        glUniform1i(varLocation, value);
    }

    public void setUniform1f(String varName, float value)
    {
        int varLocation = getUniformLocation(varName);
        glUniform1f(varLocation, value);
    }

    public void setUniform2f(String varName, Vector2f vec2)
    {
        int varLocation = getUniformLocation(varName);
        glUniform2fv(varLocation, new float[]{vec2.x, vec2.y});
    }

    public void setUniform3f(String varName, Vector3f vec3)
    {
        int varLocation = getUniformLocation(varName);
        glUniform3fv(varLocation, new float[]{vec3.x, vec3.y, vec3.z});
    }

    public void setUniform4f(String varName, Vector4f vec4)
    {
        int varLocation = getUniformLocation(varName);
        glUniform4fv(varLocation, new float[]{vec4.x, vec4.y, vec4.z, vec4.w});
    }

    public void setUniformMat3(String varName, Matrix3f mat3)
    {
        int varLocation = getUniformLocation(varName);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3 * 3);
        mat3.get(buffer);
        glUniformMatrix3fv(varLocation, false, buffer);
    }

    public void setUniformMat4(String varName, Matrix4f mat4)
    {
        int varLocation = getUniformLocation(varName);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 4);
        mat4.get(buffer);
        glUniformMatrix4fv(varLocation, false, buffer);
    }

    public void setUniformDirLight(Light light, Matrix4f viewMatrix, int i)
    {
        Vector4f dir = new Vector4f((float) Math.sin(Math.toRadians(light.getEntity().transform.rotation.y)),
                (float) -(Math.cos(Math.toRadians(light.getEntity().transform.rotation.y)) * Math.sin(Math.toRadians(light.getEntity().transform.rotation.x))),
                (float) (Math.cos(Math.toRadians(light.getEntity().transform.rotation.y)) * Math.cos(Math.toRadians(light.getEntity().transform.rotation.x))),
                0).mul(viewMatrix);
        setUniform4f("uDirectionalLight.color", light.getColor());
        setUniform3f("uDirectionalLight.direction", new Vector3f(dir.x, dir.y, dir.z));
        setUniform1f("uDirectionalLight.intensity", light.getIntensity());
    }

    public void setUniformMaterial(Material material)
    {
        setUniform4f("uMaterial.ambient", material.getAmbient());
        setUniform4f("uMaterial.diffuse", material.getDiffuse());
        setUniform4f("uMaterial.specular", material.getSpecular());
        setUniform1i("uMaterial.hasTexture", material.hasTexture());
        setUniform1f("uMaterial.reflectance", material.getReflectance());
    }

    public void cleanUp()
    {
        unbind();
        if (shaderProgramID != 0)
            glDeleteProgram(shaderProgramID);
    }

    private int getUniformLocation(String varName)
    {
        if (uniformLocationCache.containsKey(varName))
            return uniformLocationCache.get(varName);
        int location = glGetUniformLocation(shaderProgramID, varName);
        uniformLocationCache.put(varName, location);
        return location;
    }

}
