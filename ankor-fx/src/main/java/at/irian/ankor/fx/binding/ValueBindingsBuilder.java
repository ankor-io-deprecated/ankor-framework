package at.irian.ankor.fx.binding;

import at.irian.ankor.ref.Ref;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.converter.NumberStringConverter;

/**
 * @author Thomas Spiegl
 */
public class ValueBindingsBuilder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BindingsBuilder.class);

    private Property property;
    private Ref valueRef;

    private Ref itemsRef;
    private TextInputControl inputControl;
    private ComboBox comboBox;

    // XXX: Need better solution for this?
    private boolean hackyIntegerFlag = false;
    private boolean treeBindingFlag = false;

    private Long floodControlDelay;

    public static ValueBindingsBuilder bindValue(Ref value) {
        return new ValueBindingsBuilder().forValue(value);
    }

    public static ValueBindingsBuilder bindSubValues(Ref value) {
        return new ValueBindingsBuilder().forSubValues(value);
    }

    private ValueBindingsBuilder forSubValues(Ref value) {
        this.valueRef = value;
        this.treeBindingFlag = true;
        return this;
    }

    private ValueBindingsBuilder forValue(Ref value) {
        this.valueRef = value;
        return this;
    }

    public ValueBindingsBuilder toProperty(Property<?> property) {
        this.property = property;
        return this;
    }

    public ValueBindingsBuilder forIntegerValue() {
        hackyIntegerFlag = true;
        return this;
    }

    @Deprecated
    public ValueBindingsBuilder toInput(TextInputControl inputControl) {
        this.inputControl = inputControl;
        return this;
    }

    @Deprecated
    public ValueBindingsBuilder toInput(ComboBox comboBox) {
        this.comboBox = comboBox;
        return this;
    }

    @Deprecated
    public ValueBindingsBuilder withSelectItems(Ref itemsRef) {
        this.itemsRef = itemsRef;
        return this;
    }

    @Deprecated
    public ValueBindingsBuilder toText(Text text) {
        this.property = text.textProperty();
        return this;
    }

    @Deprecated
    public ValueBindingsBuilder toTabText(Tab tab) {
        this.property = tab.textProperty();
        return this;
    }

    @Deprecated
    public ValueBindingsBuilder toLabel(Label label) {
        this.property = label.textProperty();
        return this;
    }

    @Deprecated
    public ValueBindingsBuilder toCheckBox(CheckBox checkBox) {
        this.property = checkBox.selectedProperty();
        return this;
    }

    @Deprecated
    public ValueBindingsBuilder toButton(Button button) {
        this.property = button.textProperty();
        return this;
    }

    @Deprecated
    public ValueBindingsBuilder toTable(TableView tableView) {
        this.property = tableView.itemsProperty();
        return this;
    }

    @Deprecated
    public ValueBindingsBuilder toList(ListView listView) {
        this.property = listView.itemsProperty();
        return this;
    }

    public ValueBindingsBuilder withFloodControlDelay(Long delayMillis) {
        this.floodControlDelay = delayMillis;
        return this;
    }

    public void createWithin(BindingContext bindingContext) {
        if (property != null) {
            if (!hackyIntegerFlag) {
                if (!treeBindingFlag) {
                    bind(valueRef, property, bindingContext);
                } else {
                    bindTree(valueRef, property, bindingContext);
                }
            } else {
                bindInteger(valueRef, property, bindingContext);
            }
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
        sb.append(", property=").append(property);
        sb.append('}');
        return sb.toString();
    }


    // static utils

    private static void bind(Ref valueRef, Property property, BindingContext context) {
        context.add(property);
        new RefPropertyBinding(valueRef, property);
    }

    private static void bindTree(Ref valueRef, Property property, BindingContext context) {
        context.add(property);
        new RefTreeBinding(valueRef, property);
    }

    private static void bind(final Ref valueRef, final Ref itemsRef, final ComboBox comboBox) {
        //noinspection unchecked
        new RefPropertyBinding(valueRef, comboBox.valueProperty());
        new RefPropertyBinding(itemsRef, comboBox.itemsProperty());
    }

    private static void bindInteger(final Ref valueRef, final Property property, BindingContext context) {
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

    // Note that this uses a NumberStringConverter!
    private static IntegerProperty createIntegerProperty(Property property, BindingContext context) {
        SimpleIntegerProperty prop = new SimpleIntegerProperty();
        Bindings.bindBidirectional(property, prop, new NumberStringConverter());
        context.add(prop);
        return prop;
    }
}