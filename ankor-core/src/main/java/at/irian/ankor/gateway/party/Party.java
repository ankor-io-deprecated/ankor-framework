package at.irian.ankor.gateway.party;

/**
 * @author Manfred Geiler
 */
public interface Party {

    String getModelName();

    boolean equals(Object obj);

    int hashCode();

    String toString();
}
