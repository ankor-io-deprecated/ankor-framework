package at.irian.ankor.bigcoll;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractRequestingBigList<E> extends BigList<E> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractRequestingBigList.class);

    public AbstractRequestingBigList(int size) {
        super(size);
    }

    public AbstractRequestingBigList(Collection<? extends E> c) {
        super(c);
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        try {
            return super.get(index);
        } catch (MissingItemException e) {
            requestElement(index);
            return missingElementValue();
        }
    }

    protected abstract E missingElementValue();

    protected abstract void requestElement(int index);

}
