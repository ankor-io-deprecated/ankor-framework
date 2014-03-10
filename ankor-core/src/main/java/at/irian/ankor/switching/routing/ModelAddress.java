package at.irian.ankor.switching.routing;

/**
 * todo  better rename this to "ModelAddress" ?
 *
 * @author Manfred Geiler
 */
public interface ModelAddress {

    String getModelName();

    boolean equals(Object obj);

    int hashCode();

    String toString();
}
