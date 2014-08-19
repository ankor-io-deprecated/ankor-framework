package at.irian.ankor.event;

import at.irian.ankor.event.source.ModelSource;
import at.irian.ankor.event.source.Source;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.ModelSession;

/**
 * An {@link Event} that is bound to a certain model instance.
 * In fact a ModelPropertyEvent is bound to a model property, i.e. a {@link at.irian.ankor.ref.Ref}.
 * Events that cannot be associated with a certain model property should be bound to the root property of that model.
 *
 * @author Manfred Geiler
 */
public abstract class ModelPropertyEvent extends Event {

    private final Ref property;

    public ModelPropertyEvent(Source source, Ref property) {
        super(source);
        this.property = property;
    }

    public Ref getProperty() {
        return property;
    }

}
