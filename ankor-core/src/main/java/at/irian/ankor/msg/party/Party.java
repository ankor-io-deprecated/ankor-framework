package at.irian.ankor.msg.party;

/**
 * @author Manfred Geiler
 */
public interface Party {

    @Deprecated  // todo  needed?
    String getModelName();

    boolean equals(Object obj);

    int hashCode();

}
