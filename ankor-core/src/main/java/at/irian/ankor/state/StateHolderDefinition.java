package at.irian.ankor.state;

import java.util.Set;

/**
 * @author Manfred Geiler
 */
public interface StateHolderDefinition {

    Set<String> getPaths();

    Class<?> getTypeOf(String path);

    void add(String path, Class<?> type);
}
