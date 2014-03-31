package at.irian.ankor.ref.el;

import at.irian.ankor.action.Action;
import at.irian.ankor.path.el.SimpleELPathSyntax;
import at.irian.ankor.ref.*;

/**
 * @author Manfred Geiler
 */
public class MockELRef implements Ref {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MockELRef.class);

    private final Ref parent;
    private final String propertyName;
    private Object propertyValue;

    MockELRef(Ref parent, String propertyName, Object propertyValue) {
        this.parent = parent;
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public static Ref createRootRef(String name, Object value) {
        return new MockELRef(null, name, value);
    }

    public static Ref createRef(Ref parent, String name, Object value) {
        return new MockELRef(parent, name, value);
    }

    @Override
    public void setValue(Object value) {
        this.propertyValue = value;
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue() {
        return (T)propertyValue;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Ref root() {
        if (parent == null) {
            return this;
        } else {
            return parent.root();
        }
    }

    @Override
    public Ref parent() {
        return parent;
    }

    @Override
    public Ref appendPath(String propertyOrSubPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ref appendIndex(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ref appendLiteralKey(String literalKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ref appendPathKey(String pathKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ref $(String propertyOrSubPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ref $(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String path() {
        if (parent == null) {
            return propertyName;
        } else {
            return SimpleELPathSyntax.getInstance().concat(parent.path(), propertyName);
        }
    }

    @Override
    public boolean isDescendantOf(Ref ref) {
        return SimpleELPathSyntax.getInstance().isDescendant(path(), ref.path());
    }

    @Override
    public boolean isAncestorOf(Ref ref) {
        return SimpleELPathSyntax.getInstance().isDescendant(ref.path(), path());
    }

    @Override
    public String propertyName() {
        return propertyName;
    }

    @Override
    public Ref ancestor(String ancestorPropertyName) {
        if (isRoot()) {
            throw new IllegalArgumentException("No ancestor with name " + ancestorPropertyName);
        }
        Ref parent = parent();
        if (parent.propertyName().equals(ancestorPropertyName)) {
            return parent;
        }
        return parent.ancestor(ancestorPropertyName);
    }

    @Override
    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public RefContext context() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fire(Action action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CollectionRef toCollectionRef() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MapRef toMapRef() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return parent != null ? parent.toString() + '.' + propertyName : propertyName;
    }

    @Override
    public void signalValueChange() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypedRef<T> toTypedRef() {
        return (TypedRef<T>)this;
    }

    @Override
    public boolean isVirtual() {
        return false;
    }
}
