package at.irian.ankor.switching.routing;

/**
 * @author Manfred Geiler
 */
public interface ModelAddress {

    String getModelName();

    boolean equals(Object obj);

    /**
     * Since a ModelAddress is normally immutable, this Object's hashCode should be precalculated or cached for
     * optimized performance.
     */
    int hashCode();

    String toString();
}
