package at.irian.ankor.big;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.MissingPropertyActionEventListener;
import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.ref.Ref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private final int chunkSize;

    @AnkorIgnore
    private final FloodControl floodControl;

    public MissingPropertyActionFiringBigList(int size, Ref listRef, E missingElementSubstitute, int chunkSize,
                                              List<E> initialElements) {
        super(size, initialElements);
        this.listRef = listRef;
        this.missingElementSubstitute = missingElementSubstitute;
        this.chunkSize = chunkSize;
        this.floodControl = new FloodControl(listRef, 50);

    }

    @Override
    protected E getMissingElement(int index) {
        if (pendingRequests.add(index)) {
            floodControl.control(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Integer> list = new ArrayList<Integer>(pendingRequests);
                    Collections.sort(list);
                    int lastIdx = -1;
                    for (Integer reqIdx : list) {
                        if (lastIdx == -1 || reqIdx >= lastIdx + chunkSize) {
                            listRef.appendIndex(reqIdx).fire(new Action(MissingPropertyActionEventListener.ACTION_NAME));
                            lastIdx = reqIdx;
                        }
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
