package at.irian.ankor.connection;

/**
 *
 * todo  rename to Party
 *
 * @author Manfred Geiler
 */
public interface RemoteSystem {

    String getId();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    @Override
    String toString();

}
