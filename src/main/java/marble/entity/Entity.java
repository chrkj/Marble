package marble.entity;

import org.joml.Vector3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;

import physx.common.PxQuat;

import marble.editor.EditorLayer;
import marble.editor.Console;
import marble.entity.components.Component;
import marble.entity.components.ScriptableComponent;
import marble.entity.components.RigidBody;

public class Entity
{
    public String name;
    public String scriptName;
    public Transform transform;
    public final Map<Class<? extends Component>, Component> components = new HashMap<>();

    public transient ScriptableComponent script;

    private int uuid;
    private final List<Entity> children = new ArrayList<>();

    private transient Entity parent;

    public Entity()
    {

        init("Empty Entity", new Transform(), Math.abs(UUID.randomUUID().hashCode()));
    }

    public Entity(int uuid)
    {
        init("Empty Entity", new Transform(), uuid);
    }

    public Entity(String name)
    {
        init(name, new Transform(), Math.abs(UUID.randomUUID().hashCode()));
    }

    private void init(String name, Transform transform, int uuid)
    {
        this.uuid = uuid;
        this.name = name;
        this.transform = transform;
    }

    public void start()
    {
        if (script != null)
            script.onInit();
    }

    public void update(float dt)
    {
        if (components.containsKey(RigidBody.class))
        {
            var rb = (RigidBody) components.get(RigidBody.class);
            if (rb.isStatic) return;

            var pos = rb.rigidActor.getGlobalPose().getP();
            var rot = quatToAxisRot(rb.rigidActor.getGlobalPose().getQ());
            transform.setPosition(pos.getX(), pos.getY(), pos.getZ());
            transform.setRotation(rot.x, rot.y, rot.z);
        }

        if (script != null)
            script.onUpdate(dt);
    }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        return componentClass.cast(components.get(componentClass));
    }

    public <T extends Component> T removeComponent(Class<T> componentClass)
    {
        var component = componentClass.cast(components.remove(componentClass));
        EditorLayer.currentScene.getRegistry().remove(component);
        return component;
    }

    public <T extends Component> void removeComponent(T component)
    {
        components.remove(component.getClass());
        EditorLayer.currentScene.getRegistry().remove(component);
    }

    public void addComponent(Component component)
    {
        components.put(component.getClass(), component);
        component.setEntity(this);
    }

    public boolean hasComponent(Class<? extends Component> componentClass)
    {
        return components.containsKey(componentClass);
    }

    public Collection<Component> getAllComponents()
    {
        return components.values();
    }

    public void cleanUp()
    {
        for (Component component : getAllComponents())
            component.cleanUp();
    }

    public Entity setPosition(float x, float y, float z)
    {
        transform.setPosition(x,y,z);
        return this;
    }

    public Entity setRotation(float x, float y, float z)
    {
        transform.setRotation(x,y,z);
        return this;
    }

    public Entity setScale(float x, float y, float z)
    {
        transform.setScale(x,y,z);
        return this;
    }

    public List<Entity> getChildren()
    {
        return children;
    }

    public void setParent(Entity entity)
    {
        this.parent = entity;
        entity.children.add(this);
    }

    public void setChild(Entity entity)
    {
        this.children.add(entity);
        entity.parent = this;
    }

    public int getUuid()
    {
        return uuid;
    }

    public Matrix4f getWorldMatrix()
    {
        return new Matrix4f().translation(transform.getPosition()).
                rotateX((float)Math.toRadians(transform.getRotation().x)).
                rotateY((float)Math.toRadians(transform.getRotation().y)).
                rotateZ((float)Math.toRadians(transform.getRotation().z)).
                scale(transform.getScale().x, transform.getScale().y, transform.getScale().z);
    }

    public void setScript(String name)
    {
        final String targetDir = "Runtime/src";

        if (name.endsWith(".java"))
            name = name.substring(0, name.length() - 5);

        try
        {
            Path temp = Paths.get(System.getProperty("user.dir"), targetDir);
            Path javaSourceFile = Paths.get(temp.normalize().toAbsolutePath().toString(), name + ".java");
            File[] file = { javaSourceFile.toFile() };

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    null,
                    null,
                    compilationUnits
            );
            task.call();

            for (var diagnostic : diagnostics.getDiagnostics())
                Console.log("Error on line " + diagnostic.getLineNumber() + " in " + diagnostic.getSource());
            fileManager.close();

            ClassLoader classLoader = Entity.class.getClassLoader();
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{temp.toUri().toURL()}, classLoader);
            Class<?> javaDemoClass = urlClassLoader.loadClass(name);

            script = (ScriptableComponent) javaDemoClass.getDeclaredConstructor().newInstance();
            script.entity = this;
            this.scriptName = name;
        }
        catch (ClassNotFoundException e)
        {
            Console.log("Could not find class: " + name);
        }
        catch (IOException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }

    private static Vector3f quatToAxisRot(PxQuat q)
    {
        double ax, ay, az;
        var tmpVec = new Vector4f(q.getX(), q.getY(), q.getZ(), q.getW()).normalize();
        float x = tmpVec.x;
        float y = tmpVec.y;
        float z = tmpVec.z;
        float w = tmpVec.w;

        double theta = Math.sqrt(1 - w * w);
        double angle = Math.toDegrees(2 * Math.acos(w));
        if (theta < 0.001)
        {
            ax = x;
            ay = y;
            az = z;
        }
        else
        {
            ax = x / theta;
            ay = y / theta;
            az = z / theta;
        }
        return new Vector3f((float) (ax * angle), (float) (ay * angle),  (float) (az * angle));
    }

}
