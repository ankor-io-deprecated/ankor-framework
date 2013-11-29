package at.irian.ankor.fx.controller;

import at.irian.ankor.fx.binding.value.ObservableRef;
import at.irian.ankor.ref.Ref;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class I18nSupport {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(I18nSupport.class);

    private final Ref resourcesRef;

    public I18nSupport(Ref resourcesRef) {
        this.resourcesRef = resourcesRef;
    }

    public void bindTextProperty(Object node) {
        Property<String> textProperty = getTextProperty(node);
        if (textProperty != null) {
            String text = textProperty.getValue();
            if (text != null && text.startsWith("%")) {
                LOG.trace("Binding text property of {} to resource {}", textProperty, text);
                textProperty.bind(getObservableResource(text.substring(1)));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Property<String> getTextProperty(Object node) {

        Method textPropertyMethod;
        try {
            textPropertyMethod = node.getClass().getMethod("textProperty");
        } catch (NoSuchMethodException e) {
            return null;
        }

        try {
            return (Property<String>) textPropertyMethod.invoke(node);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot access text property of " + node);
        }
    }

    private ObservableValue<String> getObservableResource(String key) {
        return ObservableRef.createObservableValue(resourcesRef.appendLiteralKey(key), "?" + key + "?");
    }

}
