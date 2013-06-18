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
}
