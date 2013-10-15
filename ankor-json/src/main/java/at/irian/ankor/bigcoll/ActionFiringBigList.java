package at.irian.ankor.bigcoll;

import at.irian.ankor.action.Action;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.ref.Ref;

import java.util.Collections;

import static at.irian.ankor.bigcoll.MissingItemActionEventListener.ACTION_NAME;
import static at.irian.ankor.bigcoll.MissingItemActionEventListener.INDEX_PARAM;

/**
 * @author Manfred Geiler
 */
public class ActionFiringBigList<E> extends AbstractRequestingBigList<E> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionFiringBigList.class);

    @AnkorIgnore
    private Ref listRef;

    public ActionFiringBigList(int size, Ref listRef) {
        super(size);
        this.listRef = listRef;
    }

    @Override
    protected void requestElement(int index) {
        requestIndex(index);
    }

    @Override
    protected E missingElementValue() {
        return (E) (Collections.emptyMap());  // todo
    }

    private void requestIndex(int index) {
        listRef.fire(new Action(ACTION_NAME, Collections.<String, Object>singletonMap(INDEX_PARAM, index)));
    }
}
