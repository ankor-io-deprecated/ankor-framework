package at.irian.ankor.ref;

import at.irian.ankor.path.PathSyntax;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface RefContext {

    String getModelContextPath();

    RefContext withModelContextPath(String modelContextPath);

    PathSyntax getPathSyntax();

    RefFactory getRefFactory();

}
