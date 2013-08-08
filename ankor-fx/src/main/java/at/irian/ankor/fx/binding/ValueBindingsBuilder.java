package at.irian.ankor.fx.binding;

import at.irian.ankor.ref.Ref;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.converter.NumberStringConverter;

/**
 * @author Thomas Spiegl
 */
public class ValueBindingsBuilder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BindingsBuilder.class);

    // XXX: Using properties instead of UI components makes this builder much more applicable
    private StringProperty stringProperty;
    private BooleanProperty booleanProperty;
    private ObjectProperty<ObservableList> itemsProperty;

    private Ref valueRef;

    private Ref itemsRef;

    private TextInputControl inputControl;

    private ComboBox comboBox;

    // XXX: Need better solution for this
    private boolean hackyIntegerFlag = false;

    public static ValueBindingsBuilder bindValue(Ref value) {
        return new ValueBindingsBuilder().forValue(value);
    }

    // Shortcut
    public static ValueBindingsBuilder bind(Ref value) {
        return bindValue(value);
    }

    private ValueBindingsBuilder forValue(Ref value) {
        this.valueRef = value;
        return this;
    }

    public ValueBindingsBuilder toBooleanProperty(BooleanProperty booleanProperty) {
        this.booleanProperty = booleanProperty;
        return this;
    }

    public ValueBindingsBuilder toStringProperty(StringProperty stringProperty) {
        this.stringProperty = stringProperty;
        return this;
    }

    public ValueBindingsBuilder toItemsProperty(ObjectProperty<ObservableList> itemsProperty) {
        this.itemsProperty = itemsProperty;
        return this;
    }

    public ValueBindingsBuilder forIntegerValue() {
        hackyIntegerFlag = true;
        return this;
    }

    public ValueBindingsBuilder toInput(TextInputControl inputControl) {
        this.inputControl = inputControl;
        return this;
    }

    public ValueBindingsBuilder toInput(ComboBox comboBox) {
        this.comboBox = comboBox;
        return this;
    }

    public ValueBindingsBuilder withSelectItems(Ref itemsRef) {
        this.itemsRef = itemsRef;
        return this;
    }

    // Convenience methods

    public ValueBindingsBuilder toText(Text text) {
        this.stringProperty = text.textProperty();
        return this;
    }

    public ValueBindingsBuilder toLabel(Label label) {
        this.stringProperty = label.textProperty();
        return this;
    }

    public ValueBindingsBuilder toCheckBox(CheckBox checkBox) {
        this.booleanProperty = checkBox.selectedProperty();
        return this;
    }

    public ValueBindingsBuilder toButton(Button button) {
        this.stringProperty = button.textProperty();
        return this;
    }

    public ValueBindingsBuilder toTable(TableView tableView) {
        this.itemsProperty = tableView.itemsProperty();
        return this;
    }

    public ValueBindingsBuilder toList(ListView listView) {
        this.itemsProperty = listView.itemsProperty();
        return this;
    }

    public void createWithin(BindingContext bindingContext) {
        if (itemsProperty != null) {
            bind(valueRef, itemsProperty);
        } else if (stringProperty != null) {
            if (!hackyIntegerFlag) {
                bind(valueRef, stringProperty, bindingContext);
            } else {
                bindInteger(valueRef, stringProperty, bindingContext);
            }
        } else if (booleanProperty != null) {
            bind(valueRef, booleanProperty, bindingContext);
        } else if (comboBox != null) {
            if (itemsRef == null) {
                throw new IllegalStateException("Illegal Binding, missing itemsRef " + this);
            }
            bind(valueRef, itemsRef, comboBox);
        } else if (inputControl != null) {
            bind(valueRef, inputControl, bindingContext);
        } else {
            throw new IllegalStateException("Illegal Binding " + this);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BindingsBuilder{");
        sb.append("valueRef=").append(valueRef);
        sb.append(", itemsProperty=").append(itemsProperty);
        sb.append(", stringProperty=").append(stringProperty);
        sb.append(", booleanProperty=").append(booleanProperty);
        sb.append('}');
        return sb.toString();
    }


    // static utils

    private static void bind(Ref valueRef, ObjectProperty<ObservableList> property) {
        new RefPropertyBinding(valueRef, property);
    }

    private static void bind(Ref valueRef, StringProperty property, BindingContext context) {
        new RefPropertyBinding(valueRef, createStringProperty(property, context));
    }

    private static void bind(Ref valueRef, BooleanProperty property, BindingContext context) {
        new RefPropertyBinding(valueRef, createBooleanProperty(property, context));
    }

    private static void bind(final Ref valueRef, final Ref itemsRef, final ComboBox comboBox) {
        //noinspection unchecked
        new RefPropertyBinding(valueRef, comboBox.valueProperty());
        new RefPropertyBinding(itemsRef, comboBox.itemsProperty());
    }

    private static void bindInteger(final Ref valueRef, final StringProperty property, BindingContext context) {
        new RefPropertyBinding(valueRef, createIntegerProperty(property, context));
    }

    private static void bind(final Ref valueRef, final TextInputControl control, BindingContext context) {
        new RefPropertyBinding(valueRef, createStringProperty(control.textProperty(), context));
    }

    private static StringProperty createStringProperty(StringProperty property, BindingContext context) {
        SimpleStringProperty prop = new SimpleStringProperty();
        Bindings.bindBidirectional(prop, property);
        context.add(prop);
        return prop;
    }

    private static BooleanProperty createBooleanProperty(BooleanProperty property, BindingContext context) {
        SimpleBooleanProperty prop = new SimpleBooleanProperty();
        Bindings.bindBidirectional(prop, property);
        context.add(prop);
        return prop;
    }

    // Note that this uses a NumberStringConverter!
    private static IntegerProperty createIntegerProperty(StringProperty property, BindingContext context) {
        SimpleIntegerProperty prop = new SimpleIntegerProperty();
        Bindings.bindBidirectional(property, prop, new NumberStringConverter());
        context.add(prop);
        return prop;
    }
}