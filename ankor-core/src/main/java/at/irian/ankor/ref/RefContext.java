package at.irian.ankor.ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface RefContext {
    Ref getModelContext();
    RefContext withModelContext(Ref modelContext);
}
