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
    public String rootOf(String path) {
        int i = path.indexOf('.');
        if (i > 0) {
            return path.substring(0, i);
        }

        i = path.indexOf('[');
        if (i > 0) {
            return path.substring(0, i);
        }

        return path;
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
            String key = getArrayIdx(path);
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

    /**
     * @param path A string that contains '[' and ends with ']'
     * @return The string between '[' and ']'
     */
    private String getArrayIdx(String path) {
        int i = path.lastIndexOf('[');
        return path.substring(i + 1, path.length() - 1);
    }

    @Override
    public boolean isArrayIdx(String path) {
        if (!path.endsWith("]")) {
            return false;
        }
        String key = getArrayIdx(path);
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
        String key = getArrayIdx(path);
        return isLiteralKey(key);
    }

    @Override
    public boolean isPathMapKey(String path) {
        if (!path.endsWith("]")) {
            return false;
        }
        String key = getArrayIdx(path);
        return !key.startsWith("'") || !key.endsWith("'");
    }

    @Override
    public boolean isEqual(String path1, String path2) {
        boolean hasParent1 = isHasParent(path1);
        boolean hasParent2 = isHasParent(path2);

        if (hasParent1) {
            if (!hasParent2) {
                return false;
            }

            // both paths have parents

            String leaf1 = getPropertyName(path1);
            String leaf2 = getPropertyName(path2);
            if (leaf1.equals(leaf2)) {
                String parent1 = parentOf(path1);
                String parent2 = parentOf(path2);
                return isEqual(parent1, parent2);
            }

            return false;

        } else {
            if (hasParent2) {
                return false;
            }

            // both are root

            return path1.equals(path2);
        }
    }
}
