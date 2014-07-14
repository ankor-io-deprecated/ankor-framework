package at.irian.ankor.switching.routing;

/**
 * A ModelAddress uniquely references a named model on the local system or a remote system.
 * Concrete implementations of a ModelAddress are always associated with a concrete
 * {@link at.irian.ankor.switching.connector.Connector} so that the {@link at.irian.ankor.switching.Switchboard}
 * knows how to transmit messages to the (local or remote) model that is referenced by this ModelAddress.
 *
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

    /**
     * See https://weblogs.java.net/blog/tomwhite/archive/2007/11/consistent_hash.html
     * @return a String that is suitable as this ModelAddress' consistent hash key
     */
    String consistentHashKey();

    String toString();
}
