package at.irian.ankor.core.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
* @author MGeiler (Manfred Geiler)
*/
public class CombinedCollection extends AbstractCollection {

    private final Collection c1;
    private final Collection c2;

    public CombinedCollection(Collection c1, Collection c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator iterator() {
        final Iterator it1 = c1.iterator();
        final Iterator it2 = c2.iterator();
        return new Iterator() {
            @Override
            public boolean hasNext() {
                return it1.hasNext() || it2.hasNext();
            }

            @Override
            public Object next() {
                if (it1.hasNext()) {
                    return it1.next();
                } else {
                    return it2.next();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int size() {
        return c1.size() + c2.size();
    }
}
