package at.irian.ankor.ref;

/**
 * @author Manfred Geiler
 */
public interface RefFactory {
    /**
     * @deprecated use {@link #ref(String)} instead, e.g. ref("root")
     */
    @Deprecated
    Ref rootRef();

    Ref ref(String path);
}
