package at.irian.ankor.state;

import com.google.common.collect.ImmutableSet;

import java.util.Collections;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public class SimpleStateDefinition implements StateDefinition {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleStateDefinition.class);

    private final Set<String> paths;

    protected SimpleStateDefinition(Set<String> paths) {
        this.paths = paths;
    }

    public static StateDefinition create() {
        return new SimpleStateDefinition(Collections.<String>emptySet());
    }

    @Override
    public SimpleStateDefinition withPath(String path) {
        return new SimpleStateDefinition(ImmutableSet.<String>builder().addAll(paths).add(path).build());
    }

    @Override
    public Set<String> getPaths() {
        return paths;
    }

}
