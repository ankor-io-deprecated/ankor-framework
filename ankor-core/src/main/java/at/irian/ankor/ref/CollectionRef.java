package at.irian.ankor.ref;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public interface CollectionRef extends Ref {

    void delete(int idx);

    void insert(int idx, Object value);      // todo: insert multiple objects...

    void add(Object value);

    void replace(int fromIdx, Collection elements);
}
