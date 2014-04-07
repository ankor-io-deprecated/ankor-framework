package at.irian.ankor.state;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public class SimpleSendStateDefinition implements SendStateDefinition {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleSendStateDefinition.class);

    private final Set<String> paths;

    protected SimpleSendStateDefinition(Set<String> paths) {
        this.paths = paths;
    }

    public static SendStateDefinition empty() {
        return new SimpleSendStateDefinition(ImmutableSet.<String>of());
    }

    public static SendStateDefinition of(Collection<String> paths) {
        return new SimpleSendStateDefinition(paths != null ? ImmutableSet.copyOf(paths) : ImmutableSet.<String>of());
    }

//    public SimpleSendStateDefinition withPath(String path) {
//        return new SimpleSendStateDefinition(ImmutableSet.<String>builder().addAll(paths).add(path).build());
//    }

    @Override
    public Set<String> getPaths() {
        return paths;
    }

}
