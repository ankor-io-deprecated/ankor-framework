package at.irian.ankor.api.model.deprecated;

/**
 */
public interface ModelPropertyPath {
    String getName();
    ModelPropertyPath getRoot();
    ModelPropertyPath getParent();
    String getProperty();
    String toString();
    ModelPropertyPath withChild(String property);
    boolean isChildOf(ModelPropertyPath p);
    boolean isDescendantOf(ModelPropertyPath p);
}
