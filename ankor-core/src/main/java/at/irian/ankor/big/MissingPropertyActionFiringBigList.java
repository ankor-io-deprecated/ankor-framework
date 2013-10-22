package at.irian.ankor.big;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.MissingPropertyActionEventListener;
import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.ref.Ref;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Manfred Geiler
 */
public class MissingPropertyActionFiringBigList<E> extends AbstractBigList<E> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MissingPropertyActionFiringBigList.class);

    @AnkorIgnore
    private final Ref listRef;

    @AnkorIgnore
    private final Set<Integer> pendingRequests = new CopyOnWriteArraySet<Integer>();

    @AnkorIgnore
    private final E missingElementSubstitute;

    @AnkorIgnore
    private final FloodControl floodControl;

    public MissingPropertyActionFiringBigList(int size, Ref listRef, E missingElementSubstitute) {
        super(size);
        this.listRef = listRef;
        this.missingElementSubstitute = missingElementSubstitute;
        this.floodControl = new FloodControl(listRef, 500);
    }

    @Override
    protected E getMissingElement(int index) {
        if (pendingRequests.add(index)) {
            floodControl.control(new Runnable() {
                @Override
                public void run() {
                    for (Integer i : pendingRequests) {
                        listRef.appendIndex(i).fire(new Action(MissingPropertyActionEventListener.ACTION_NAME));
                    }
                }
            });
        }
        return missingElementSubstitute;
    }

    @Override
    public void add(int index, E element) {
        pendingRequests.remove(index);
        super.add(index, element);
    }

    @Override
    public E set(int index, E element) {
        pendingRequests.remove(index);
        return super.set(index, element);
    }

    @Override
    public E remove(int index) {
        pendingRequests.remove(index);
        return super.remove(index);
    }

    @Override
    public void clear() {
        pendingRequests.clear();
        super.clear();
    }
}
