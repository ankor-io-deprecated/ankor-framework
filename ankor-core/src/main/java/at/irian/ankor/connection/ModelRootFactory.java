package at.irian.ankor.connection;

import at.irian.ankor.ref.Ref;

import java.util.Set;

/**
 * @author Manfred Geiler
 */
public interface ModelRootFactory {

    Set<String> getKnownRootNames();

    Object createModelRoot(Ref rootRef);

}
