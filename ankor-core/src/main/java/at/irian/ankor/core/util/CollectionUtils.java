package at.irian.ankor.core.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author MGeiler (Manfred Geiler)
 */
public final class CollectionUtils {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CollectionUtils.class);

    private CollectionUtils() {}

    public static <T> Collection<T> concat(Collection<T> c1, Collection<T> c2) {
        if (c1 == null && c2 == null) {
            return Collections.emptyList();
        } else if (c1 == null) {
            return c2;
        } else if (c2 == null) {
            return c1;
        } else if (c1.isEmpty() && c2.isEmpty()) {
            return c1;
        } else if (c1.isEmpty()) {
            return c2;
        } else if (c2.isEmpty()) {
            return c1;
        } else {
            return new ConcatCollection<T>(c1, c2);
        }
    }

    private static class ConcatCollection<T> extends AbstractCollection<T> {

        private final Collection<T> c1;
        private final Collection<T> c2;

        public ConcatCollection(Collection<T> c1, Collection<T> c2) {
            this.c1 = c1;
            this.c2 = c2;
        }

        @Override
        public Iterator<T> iterator() {
            final Iterator<T> it1 = c1.iterator();
            final Iterator<T> it2 = c2.iterator();
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return it1.hasNext() || it2.hasNext();
                }

                @Override
                public T next() {
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


    public static <A,B> Collection<B> wrap(final Collection<A> c, final Wrapper<A,B> wrapper) {
        return new AbstractCollection<B>() {
            @Override
            public Iterator<B> iterator() {
                final Iterator<A> iterator = c.iterator();
                return new Iterator<B>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public B next() {
                        return wrapper.wrap(iterator.next());
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }

            @Override
            public int size() {
                return c.size();
            }
        };
    }

    public static interface Wrapper<A,B> {
        B wrap(A objToWrap);
    }

}
