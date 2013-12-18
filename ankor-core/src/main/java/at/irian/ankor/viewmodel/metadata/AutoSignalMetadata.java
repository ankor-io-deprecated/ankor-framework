package at.irian.ankor.viewmodel.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class AutoSignalMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AutoSignalMetadata.class);

    private static final AutoSignalMetadata EMPTY_INSTANCE = new AutoSignalMetadata();

    private final Collection<String> paths;

    public AutoSignalMetadata() {
        this(Collections.<String>emptyList());
    }

    protected AutoSignalMetadata(Collection<String> paths) {
        this.paths = paths;
    }

    public static AutoSignalMetadata empty() {
        return EMPTY_INSTANCE;
    }

    public AutoSignalMetadata withPath(String path) {
        List<String> newPaths = new ArrayList<String>(paths);
        newPaths.add(path);
        return new AutoSignalMetadata(newPaths);
    }

    public Collection<String> getPaths() {
        return paths;
    }
}
