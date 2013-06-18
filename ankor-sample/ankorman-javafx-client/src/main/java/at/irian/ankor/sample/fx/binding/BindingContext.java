package at.irian.ankor.sample.fx.binding;

import javafx.beans.property.Property;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Thomas Spiegl
 */
public class BindingContext {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelBindingContext.class);

    private Set<Property> properties;

    public BindingContext() {
        properties = new HashSet<Property>();
    }

    void add(Property property) {
        properties.add(property);
    }
}
