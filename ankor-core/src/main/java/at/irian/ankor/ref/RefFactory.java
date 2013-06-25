package at.irian.ankor.ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface RefFactory {
    Ref rootRef();
    Ref ref(String path);
}
