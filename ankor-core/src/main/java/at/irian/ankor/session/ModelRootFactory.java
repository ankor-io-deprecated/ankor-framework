package at.irian.ankor.session;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public interface ModelRootFactory {

    Object createModelRoot(Ref rootRef);

}
