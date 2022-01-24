package marble.entity;

import org.joml.Vector4f;

import marble.renderer.Shader;
import marble.entity.components.Texture;

public class Material {

    private Shader shader;
    private Texture texture;
    private float reflectance;
    private final Vector4f ambient;
    private final Vector4f diffuse;
    private final Vector4f specular;

    public Material()
    {
        Vector4f DEFAULT_COLOR = new Vector4f(1f, 0f, 1f, 1f);
        this.shader = new Shader("assets/shaders/default.glsl");
        this.texture = null;
        this.ambient = DEFAULT_COLOR;
        this.diffuse = DEFAULT_COLOR;
        this.specular = DEFAULT_COLOR;
        this.reflectance = 0;
        shader.compile();
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

    public int hasTexture()
    {
        return texture == null ? 0 : 1;
    }

}
