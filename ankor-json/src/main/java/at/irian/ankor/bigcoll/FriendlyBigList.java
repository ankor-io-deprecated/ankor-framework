package at.irian.ankor.bigcoll;

import at.irian.ankor.messaging.AnkorIgnore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public class FriendlyBigList<E> extends BigList<E> /*implements MissingItemsAware<Integer>*/ {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FriendlyBigList.class);

    @AnkorIgnore
    private final Set<Integer> missingItems = new HashSet<Integer>();

    @AnkorIgnore
    private final E missingItemValue;

    public FriendlyBigList(Collection<? extends E> c) {
        this(c, null);
    }

    public FriendlyBigList(Collection<? extends E> c, E missingItemValue) {
        super(c);
        this.missingItemValue = missingItemValue;
    }

    public FriendlyBigList(int size) {
        this(size, null);
    }

    public FriendlyBigList(int size, E missingItemValue) {
        super(size);
        this.missingItemValue = missingItemValue;
    }

    @Override
    public E get(int index) throws MissingItemException {
        try {
            return super.get(index);
        } catch (MissingItemException e) {
            missingItems.add(index);
            return missingItemValue;
        }
    }

    public Set<Integer> getMissingItemsAndReset() {
        Set<Integer> result = new HashSet<Integer>(missingItems);
        missingItems.clear();
        return result;
    }
}
