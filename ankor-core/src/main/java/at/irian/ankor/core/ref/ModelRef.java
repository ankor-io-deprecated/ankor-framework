package at.irian.ankor.core.ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface ModelRef {
    void setValue(Object value);
    <T> T getValue();

    RootRef root();
    ModelRef unwatched();

    void fireAction(String action);

    String getPath();

    ModelRef with(String subPath);
}
