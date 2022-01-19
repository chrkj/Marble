package marble.gameobject.components;

import org.joml.Vector4f;

import marble.renderer.Shader;

public class Material extends Component {

    private Shader shader;
    private Texture texture;
    private Vector4f ambient;
    private Vector4f diffuse;
    private Vector4f specular;
    private float reflectance;

    private static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    public Material(Texture texture, Shader shader)
    {
        this.shader = shader;
        this.texture = texture;
        this.ambient = DEFAULT_COLOUR;
        this.diffuse = DEFAULT_COLOUR;
        this.specular = DEFAULT_COLOUR;
        this.reflectance = 1;
        shader.compile();
    }

    public Material(Shader shader)
    {
        this.shader = shader;
        this.texture = null;
        this.ambient = DEFAULT_COLOUR;
        this.diffuse = DEFAULT_COLOUR;
        this.specular = DEFAULT_COLOUR;
        this.reflectance = 1;
        shader.compile();
    }

    @Override
    public void start()
    {
    }

    @Override
    public void update(float dt)
    {
    }

    @Override
    public void render()
    {
    }

    @Override
    public void cleanUp()
    {
        shader.cleanUp();
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

    public void setAmbient(Vector4f ambient)
    {
        this.ambient = ambient;
    }

    public Vector4f getDiffuse()
    {
        return diffuse;
    }

    public void setDiffuse(Vector4f diffuse)
    {
        this.diffuse = diffuse;
    }

    public Vector4f getSpecular()
    {
        return specular;
    }

    public void setSpecular(Vector4f specular)
    {
        this.specular = specular;
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
