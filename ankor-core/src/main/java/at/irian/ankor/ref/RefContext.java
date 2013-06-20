package at.irian.ankor.ref;

import at.irian.ankor.path.PathSyntax;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface RefContext {
    Ref getModelContext();
    RefContext withModelContext(Ref modelContext);
    PathSyntax getPathSyntax();
}
