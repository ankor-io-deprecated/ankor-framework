package at.irian.ankorsamples.statelesstodo.fxclient;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * The FXMLLoader is currently not designed to perform as a template provider that instantiates the same item over and 
 * over again. Rather it is meant to be a one-time-loader for large GUIs (or to serialize them).
 * 
 * The performance is poor because depending on the FXML file, on each call to load(), the FXMLLoader has to look up the
 * classes and its properties via reflection. That means:
 *
 * <ol>
 *     <li>For each import statement, try to load each class until the class could successfully be loaded.</li>
 *     <li>For each class, create a BeanAdapter that looks up all properties this class has and tries to apply the given
 *     parameters to the property.</li>
 *     <li>The application of the parameters to the properties is done via reflection again.</li>
 * </ol> 
 * 
 * There is also currently no improvement for subsequent calls to load() to the same FXML file done in the code. 
 * This means: no caching of found classes, no caching of BeanAdapters and so on.
 * 
 * There is a workaround for the performance of step 1, though, by setting a custom class loader to the FXMLLoader 
 * instance.
 * 
 * Source: http://stackoverflow.com/questions/11734885/javafx2-very-poor-performance-when-adding-custom-made-fxmlpanels-to-gridpane 
 */
public class CachingClassLoader extends ClassLoader {
    private final Map<String, Class> classes = new HashMap<>();
    private final ClassLoader parent;

    public CachingClassLoader(ClassLoader parent) {
        this.parent = parent;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> c = findClass(name);
        if (c == null) {
            throw new ClassNotFoundException(name);
        }
        return c;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        // System.out.print("try to load " + className); 
        if (classes.containsKey(className)) {
            return classes.get(className);
        } else {
            try {
                Class<?> result = parent.loadClass(className);
                // System.out.println(" -> success!"); 
                classes.put(className, result);
                return result;
            } catch (ClassNotFoundException ignore) {
                // System.out.println(); 
                classes.put(className, null);
                return null;
            }
        }
    }

    // ========= delegating methods ============= 
    @Override
    public URL getResource(String name) {
        return parent.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return parent.getResources(name);
    }

    @Override
    public String toString() {
        return parent.toString();
    }

    @Override
    public void setDefaultAssertionStatus(boolean enabled) {
        parent.setDefaultAssertionStatus(enabled);
    }

    @Override
    public void setPackageAssertionStatus(String packageName, boolean enabled) {
        parent.setPackageAssertionStatus(packageName, enabled);
    }

    @Override
    public void setClassAssertionStatus(String className, boolean enabled) {
        parent.setClassAssertionStatus(className, enabled);
    }

    @Override
    public void clearAssertionStatus() {
        parent.clearAssertionStatus();
    }
}
