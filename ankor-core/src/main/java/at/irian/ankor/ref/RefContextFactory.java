package at.irian.ankor.ref;

import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public interface RefContextFactory {

    RefContext createRefContextFor(ModelSession modelSession);

}
