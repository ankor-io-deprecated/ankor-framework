package at.irian.ankor.core.ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ModelRef {

    static String NIL = "NIL";

    void setValue(Object value);
    <T> T getValue();

    RootRef root();
    ModelRef parent();
    ModelRef sub(String subPath);
    ModelRef unwatched();

    void fireAction(String action);

    String path();

    /**
     * @return true, if the given ref is a parent (or grand-parent, or...) of this ref
     */
    boolean isDescendantOf(ModelRef ref);
    boolean isAncestorOf(ModelRef ref);
}
