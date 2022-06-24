package marble.entity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import marble.editor.ConsolePanel;
import marble.editor.EditorLayer;
import marble.entity.components.Component;
import marble.entity.components.ScriptableComponent;

import javax.tools.*;

public class Entity {

    public String name;
    public Transform transform;
    public final Map<Class<? extends Component>, Component> components = new HashMap<>();

    private int uuid;
    private transient Entity parent;
    public ScriptableComponent script;
    private final List<Entity> children = new ArrayList<>();

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
        if (script != null)
            script.onUpdate(dt);
    }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        Component component = components.get(componentClass);
        if (component != null)
            return componentClass.cast(component);
        return null;
    }

    public <T extends Component> T removeComponent(Class<T> componentClass)
    {
        var comp = componentClass.cast(components.remove(componentClass));
        EditorLayer.currentScene.getRegistry().remove(comp);
        return comp;
    }

    public <T extends Component> void removeComponent(T component)
    {
        components.remove(component.getClass());
        EditorLayer.currentScene.getRegistry().remove(component);
    }

    public Entity addComponent(Component component)
    {
        components.put(component.getClass(), component);
        component.setEntity(this);

        return this;
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

    public Entity setParent(Entity entity)
    {
        this.parent = entity;
        entity.children.add(this);
        return this;
    }

    public Entity setChild(Entity entity)
    {
        this.children.add(entity);
        entity.parent = this;
        return this;
    }

    public int getUuid()
    {
        return uuid;
    }

    public void setScript(String name)
    {
        try
        {
            final String targetDir = "Runtime/src";
            final String filename = "MyScript";

            Path temp = Paths.get(System.getProperty("user.dir"), targetDir);

            Path javaSourceFile = Paths.get(temp.normalize().toAbsolutePath().toString(), filename + ".java");

            // Definition of the files to compile
            File[] files1 = {javaSourceFile.toFile()};

            // Get the compiler
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            // Get the file system manager of the compiler
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

            // Create a compilation unit (files)
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files1));

            // A feedback object (diagnostic) to get errors
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

            // Compilation unit can be created and called only once
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    null,
                    null,
                    compilationUnits
            );

            // The compile task is called
            task.call();

            // Printing of any compile problems
            for (Diagnostic diagnostic : diagnostics.getDiagnostics())
                System.out.format("Error on line %d in %s%n", diagnostic.getLineNumber(), diagnostic.getSource());

            // Close the compile resources
            fileManager.close();

            ClassLoader classLoader = Entity.class.getClassLoader();
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{temp.toUri().toURL()}, classLoader);
            Class<?> javaDemoClass = urlClassLoader.loadClass(filename);

            script = (ScriptableComponent) javaDemoClass.getDeclaredConstructor().newInstance();
            script.entity = this;
            ConsolePanel.log("Script added to: " + this.name);

        }
        catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }

}
