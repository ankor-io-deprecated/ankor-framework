package at.irian.ankor.ref;

/**
 * @author Manfred Geiler
 */
public interface CollectionRef extends Ref {

    void delete(int idx);

    void insert(int idx, Object value);

    void add(Object value);
}
