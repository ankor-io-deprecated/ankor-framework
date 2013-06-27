package at.irian.ankor.ref;

/**
 * @author Manfred Geiler
 */
public interface RefFactory {
    Ref rootRef();
    Ref ref(String path);
}
