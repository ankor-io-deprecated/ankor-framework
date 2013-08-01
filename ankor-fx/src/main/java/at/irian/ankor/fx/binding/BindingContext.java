package at.irian.ankor.fx.binding;

import javafx.beans.property.Property;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Thomas Spiegl
 */
public class BindingContext {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelBindingContext.class);

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Set<Property> properties;

    public BindingContext() {
        properties = new HashSet<>();
    }

    void add(Property property) {
        properties.add(property);
    }

    public void unbind() {
        for (Property property : properties) {
            if (property.isBound()) {
                property.unbind();
            }
        }
    }
}
