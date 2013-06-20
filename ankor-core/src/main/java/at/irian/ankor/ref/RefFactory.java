package at.irian.ankor.ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface RefFactory {

    Ref rootRef();

    Ref rootRef(RefContext newRefContext);

    Ref ref(String path);

    Ref ref(String path, RefContext newRefContext);
}
