package at.irian.ankor.fx.binding.fxref;

import at.irian.ankor.fx.binding.property.RefProperty;
import at.irian.ankor.fx.binding.value.ObservableRef;
import at.irian.ankor.ref.el.ELRef;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import javax.el.ValueExpression;

/**
 * @author Manfred Geiler
 */
class DefaultFxRef extends ELRef implements FxRef {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultFxRef.class);

    public DefaultFxRef(DefaultFxRefContext refContext, ValueExpression ve) {
        super(refContext, ve);
    }

    @Override
    public FxRef root() {
        return (FxRef) super.root();
    }

    @Override
    public FxRef parent() {
        return (FxRef) super.parent();
    }

    @Override
    public FxRef appendPath(String propertyOrSubPath) {
        return (FxRef) super.appendPath(propertyOrSubPath);
    }

    @Override
    public FxRef appendIndex(int index) {
        return (FxRef) super.appendIndex(index);
    }

    @Override
    public FxRef appendLiteralKey(String literalKey) {
        return (FxRef) super.appendLiteralKey(literalKey);
    }

    @Override
    public FxRef appendPathKey(String pathKey) {
        return (FxRef) super.appendPathKey(pathKey);
    }

    @Override
    public FxRef $(String propertyOrSubPath) {
        return (FxRef) super.$(propertyOrSubPath);
    }

    @Override
    public FxRef $(int index) {
        return (FxRef) super.$(index);
    }

    @Override
    public FxRef ancestor(String ancestorPropertyName) {
        return (FxRef) super.ancestor(ancestorPropertyName);
    }

    @Override
    public FxRefContext context() {
        return (FxRefContext) super.context();
    }

    @Override
    public <T> ObservableValue<T> fxObservable() {
        return new ObservableRef<>(this);
    }

    @Override
    public <T> Property<T> fxProperty() {
        return new RefProperty<>(this);
    }

}
