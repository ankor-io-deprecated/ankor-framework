package at.irian.ankor.connection;

/**
 * @author Manfred Geiler
 */
@Deprecated   // use ModelAddress instead
public interface RemoteSystem {

    String getId();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    @Override
    String toString();

}
