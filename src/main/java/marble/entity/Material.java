package marble.entity;

import org.joml.Vector4f;

import marble.renderer.Shader;
import marble.entity.components.Texture;

public class Material {

    private Shader shader;
    private Texture texture;
    private Vector4f ambient;
    private Vector4f diffuse;
    private Vector4f specular;
    private float reflectance;

    private final String DEFAULT_SHADER_PATH = "assets/shaders/default.glsl";
    private final Vector4f DEFAULT_COLOR = new Vector4f(1f, 0f, 1f, 1f);

    public Material()
    {
        init(null, new Shader(DEFAULT_SHADER_PATH), DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, 0);
    }

    public void init(Texture texture, Shader shader, Vector4f ambient, Vector4f diffuse, Vector4f specular, float reflectance)
    {
        this.shader = shader;
        this.texture = texture;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.reflectance = reflectance;
        shader.compile();
    }

    public Shader getShader()
    {
        return shader;
    }

    public void setShader(Shader shader)
    {
        this.shader = shader;
    }

    public Texture getTexture()
    {
        return texture;
    }

    public void setTexture(Texture texture)
    {
        this.texture = texture;
    }

    public Vector4f getAmbient()
    {
        return ambient;
    }

    public void setAmbient(float r, float g, float b, float a)
    {
        this.ambient.x = r;
        this.ambient.y = g;
        this.ambient.z = b;
        this.ambient.w = a;
    }

    public Vector4f getDiffuse()
    {
        return diffuse;
    }

    public void setDiffuse(float r, float g, float b, float a)
    {
        this.diffuse.x = r;
        this.diffuse.y = g;
        this.diffuse.z = b;
        this.diffuse.w = a;
    }

    public Vector4f getSpecular()
    {
        return specular;
    }

    public void setSpecular(float r, float g, float b, float a)
    {
        this.specular.x = r;
        this.specular.y = g;
        this.specular.z = b;
        this.specular.w = a;
    }

    public float getReflectance()
    {
        return reflectance;
    }

    public void setReflectance(float reflectance)
    {
        this.reflectance = reflectance;
    }

    public int hasTexture()
    {
        return texture == null ? 0 : 1;
    }

}
