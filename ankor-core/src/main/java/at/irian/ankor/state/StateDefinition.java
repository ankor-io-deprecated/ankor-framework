package at.irian.ankor.state;

import java.util.Set;

/**
 * @author Manfred Geiler
 */
public interface StateDefinition {

    Set<String> getPaths();

    StateDefinition withPath(String path);

}
