package at.irian.ankor.core.ref;

import at.irian.ankor.core.action.ModelAction;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ModelRef {

    void setValue(Object value);
    <T> T getValue();

    RootRef root();
    ModelRef parent();
    ModelRef sub(String subPath);
    ModelRef unwatched();

    void fire(ModelAction action);

    String path();

    /**
     * @return true, if the given ref is a parent (or grand-parent, or...) of this ref
     */
    boolean isDescendantOf(ModelRef ref);
    boolean isAncestorOf(ModelRef ref);
}
