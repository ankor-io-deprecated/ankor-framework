package at.irian.ankor.ref.el;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.path.el.ELPathSyntax;
import at.irian.ankor.ref.ActionListener;
import at.irian.ankor.ref.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;

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
    public void apply(Change change) {
        this.propertyValue = change.getNewValue();
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
    public Ref append(String propertyOrSubPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ref appendIdx(int index) {
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
    public String path() {
        if (parent == null) {
            return propertyName;
        } else {
            return ELPathSyntax.getInstance().concat(parent.path(), propertyName);
        }
    }

    @Override
    public boolean isDescendantOf(Ref ref) {
        return ELPathSyntax.getInstance().isDescendant(path(), ref.path());
    }

    @Override
    public boolean isAncestorOf(Ref ref) {
        return ELPathSyntax.getInstance().isDescendant(ref.path(), path());
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
    public Ref withContext(RefContext newContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fireAction(Action action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addPropActionListener(ActionListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addPropChangeListener(ChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addTreeChangeListener(ChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        throw new UnsupportedOperationException();
    }
}
