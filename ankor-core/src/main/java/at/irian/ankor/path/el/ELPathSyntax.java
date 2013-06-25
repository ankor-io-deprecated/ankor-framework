package at.irian.ankor.path.el;

import at.irian.ankor.path.PathSyntax;

/**
 * @author Manfred Geiler
 */
public class ELPathSyntax implements PathSyntax {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELPathSyntax.class);

    private static final ELPathSyntax INSTANCE = new ELPathSyntax();

    protected ELPathSyntax() {}

    public static ELPathSyntax getInstance() {
        return INSTANCE;
    }

    @Override
    public String parentOf(String path) {
        if (path.endsWith("]")) {
            int i = path.lastIndexOf('[');
            if (i >= 0) {
                return path.substring(0, i);
            }
        } else {
            int i = path.lastIndexOf('.');
            if (i >= 0) {
                return path.substring(0, i);
            }
        }
        throw new IllegalArgumentException("Not a valid path: " + path);
    }

    @Override
    public String concat(String path, String subPath) {
        return path + '.' + subPath;
    }

    @Override
    public String addArrayIdx(String path, int index) {
        return path + '[' + index + ']';
    }

    @Override
    public String addLiteralMapKey(String path, String literalKey) {
        return path + "['" + literalKey + "']";
    }

    @Override
    public String addMapKey(String path, String literalKey) {
        return path + '[' + literalKey + ']';
    }
}
