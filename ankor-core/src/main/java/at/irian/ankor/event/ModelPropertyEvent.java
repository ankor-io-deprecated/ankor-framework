package at.irian.ankor.event;

import at.irian.ankor.event.source.ModelSource;
import at.irian.ankor.event.source.Source;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public abstract class ModelPropertyEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelEvent.class);

    private final Ref property;

    public ModelPropertyEvent(Source source, Ref property) {
        super(source);
        this.property = property;
    }

    public Ref getProperty() {
        return property;
    }


    public boolean isLocalEvent() {
        if (getSource() instanceof ModelSource) {
            String sourceModelSessionId = ((ModelSource) getSource()).getModelSessionId();
            ModelSession currentModelSession = getProperty().context().modelSession();
            return sourceModelSessionId.equals(currentModelSession.getId());
        } else {
            return false;
        }
    }

}
