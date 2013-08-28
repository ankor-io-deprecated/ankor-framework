package at.irian.ankor.event;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public abstract class ModelPropertyEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelEvent.class);

    public ModelPropertyEvent(Ref source) {
        super(source);
    }

    public Ref getSourceProperty() {
        return (Ref)getSource();
    }

}
