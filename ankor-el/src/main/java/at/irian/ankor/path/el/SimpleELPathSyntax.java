package at.irian.ankor.path.el;

import at.irian.ankor.path.PathSyntax;

/**
 * Note: This is a very simple basic implementation that is able to handle "constant" path expressions.
 * This implementation is NOT able to parse sophisticated nested paths like "foo[bar[5].baz['qux']]".
 *
 * @author Manfred Geiler
 */
public class SimpleELPathSyntax implements PathSyntax {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleELPathSyntax.class);

    private static final SimpleELPathSyntax INSTANCE = new SimpleELPathSyntax();

    protected SimpleELPathSyntax() {}

    public static SimpleELPathSyntax getInstance() {
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
    public boolean isHasParent(String path) {
        return path.indexOf('.') >= 0 || path.indexOf('[') >= 0;
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
    public String addPathMapKey(String path, String mapKey) {
        return path + '[' + mapKey + ']';
    }

    @Override
    public boolean isParentChild(String parent, String child) {
        return isHasParent(child) && parentOf(child).equals(parent);
    }

    @Override
    public boolean isDescendant(String descendant, String ancestor) {
        return isParentChild(ancestor, descendant)
               || isHasParent(descendant) && isDescendant(parentOf(descendant), ancestor);
    }

    @Override
    public String getPropertyName(String path) {
        if (path.endsWith("]")) {
            int i = path.lastIndexOf('[');
            String key = path.substring(i + 1, path.length() - 1);
            if (isLiteralKey(key)) {
                return key.substring(1, key.length() - 1);
            } else {
                return key;
            }
        } else {
            int i = path.lastIndexOf('.');
            if (i > 0) {
                return path.substring(i + 1);
            } else {
                throw new IllegalArgumentException("Not a valid path: " + path);
            }
        }
    }

    private boolean isLiteralKey(String key) {
        return key.length() >= 2 && key.startsWith("'") && key.endsWith("'");
    }

    @Override
    public boolean isArrayIdx(String path) {
        if (!path.endsWith("]")) {
            return false;
        }
        int i = path.lastIndexOf('[');
        String key = path.substring(i, path.length() - 1);
        try {
            Integer.parseInt(key);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isLiteralMapKey(String path) {
        if (!path.endsWith("]")) {
            return false;
        }
        int i = path.lastIndexOf('[');
        String key = path.substring(i, path.length() - 1);
        return isLiteralKey(key);
    }

    @Override
    public boolean isPathMapKey(String path) {
        if (!path.endsWith("]")) {
            return false;
        }
        int i = path.lastIndexOf('[');
        String key = path.substring(i, path.length() - 1);
        return !key.startsWith("'") || !key.endsWith("'");
    }
}
