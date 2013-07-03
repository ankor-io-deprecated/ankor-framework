package at.irian.ankor.fx.binding;

import at.irian.ankor.ref.Ref;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;

/**
 * @author Thomas Spiegl
 */
public class ValueBindingsBuilder {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BindingsBuilder.class);

    private Ref valueRef;

    private Text text;

    private Tab tab;

    private Ref itemsRef;

    private TextInputControl inputControl;

    private ComboBox comboBox;

    private TableView tableView;

    private Ref editableRef;

    public static ValueBindingsBuilder bindValue(Ref value) {
        return new ValueBindingsBuilder().forValue(value);
    }

    private ValueBindingsBuilder forValue(Ref value) {
        this.valueRef = value;
        return this;
    }

    public ValueBindingsBuilder toText(Text text) {
        this.text = text;
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

    public ValueBindingsBuilder toTable(TableView tableView) {
        this.tableView = tableView;
        return this;
    }

    public ValueBindingsBuilder toTabText(Tab tab) {
        this.tab = tab;
        return this;
    }

    public ValueBindingsBuilder withSelectItems(Ref itemsRef) {
        this.itemsRef = itemsRef;
        return this;
    }

    public ValueBindingsBuilder withEditable(Ref editableRef) {
        this.editableRef = editableRef;
        return this;
    }

    public void createWithin(BindingContext bindingContext) {
        if (text != null) {
            bind(valueRef, text, bindingContext);
        } else if (tab != null) {
            bind(valueRef, tab, bindingContext);
        } else if (comboBox != null) {
            if (itemsRef == null) {
                throw new IllegalStateException("Illegal Binding, missing itemsRef " + this);
            }
            bind(valueRef, itemsRef, comboBox);
        } else if (inputControl != null) {
            bind(valueRef, inputControl, bindingContext);
            if (editableRef != null) {
                bindEditable(editableRef, inputControl, bindingContext);
            }
        } else if (tableView != null) {
            bind(valueRef, tableView);
        } else {
            throw new IllegalStateException("Illegal Binding " + this);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BindingsBuilder{");
        sb.append("valueRef=").append(valueRef);
        sb.append(", text=").append(text);
        sb.append(", itemsRef=").append(itemsRef);
        sb.append(", inputControl=").append(inputControl);
        sb.append(", comboBox=").append(comboBox);
        sb.append('}');
        return sb.toString();
    }


    // static utils

    private static void bind(final Ref valueRef, final Ref itemsRef, final ComboBox comboBox) {
        //noinspection unchecked
        new RefPropertyBinding(valueRef, comboBox.valueProperty());
        new RefPropertyBinding(itemsRef, comboBox.itemsProperty());
    }

    private static void bind(final Ref valueRef, final Text text, BindingContext context) {
        new RefPropertyBinding(valueRef, createProperty(text.textProperty(), context));
    }

    private static void bind(final Ref valueRef, final Tab tab, BindingContext context) {
        new RefPropertyBinding(valueRef, createProperty(tab.textProperty(), context));
    }

    private static void bind(final Ref valueRef, final TextInputControl control, BindingContext context) {
        new RefPropertyBinding(valueRef, createProperty(control.textProperty(), context));
    }

    private static void bindEditable(Ref valueRef, TextInputControl control, BindingContext context) {
        new RefPropertyBinding(valueRef, createBooleanProperty(control.editableProperty(), context));
    }

    private static void bind(Ref valueRef, TableView tableView) {
        new RefPropertyBinding(valueRef, tableView.itemsProperty());
    }

    private static SimpleStringProperty createProperty(StringProperty property, BindingContext context) {
        SimpleStringProperty prop = new SimpleStringProperty();
        Bindings.bindBidirectional(prop, property);
        context.add(prop);
        return prop;
    }

    private static SimpleBooleanProperty createBooleanProperty(BooleanProperty property, BindingContext context) {
        SimpleBooleanProperty prop = new SimpleBooleanProperty();
        Bindings.bindBidirectional(prop, property);
        context.add(prop);
        return prop;
    }
}