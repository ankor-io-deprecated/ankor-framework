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

    private Ref valueRef;

    private Text text;

    private Label label;

    private Button button;

    private Tab tab;

    private Ref itemsRef;

    private TextInputControl inputControl;

    private ComboBox comboBox;

    private TableView tableView;

    private ListView listView;

    private Ref editableRef;

    boolean hackyIntegerFlag = false;

    public static ValueBindingsBuilder bindValue(Ref value) {
        return new ValueBindingsBuilder().forValue(value);
    }

    private ValueBindingsBuilder forValue(Ref value) {
        this.valueRef = value;
        return this;
    }

    public ValueBindingsBuilder forIntegerValue() {
        hackyIntegerFlag = true;
        return this;
    }

    public ValueBindingsBuilder toText(Text text) {
        this.text = text;
        return this;
    }

    public ValueBindingsBuilder toLabel(Label label) {
        this.label = label;
        return this;
    }

    public ValueBindingsBuilder toButton(Button button) {
        this.button = button;
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

    public ValueBindingsBuilder toList(ListView listView) {
        this.listView = listView;
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
        } else if (label != null) {
            if (!hackyIntegerFlag) {
                bind(valueRef, label, bindingContext);
            } else {
                bindInteger(valueRef, label, bindingContext);
            }
        } else if (button != null) {
            bind(valueRef, button, bindingContext);
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
        } else if (listView != null) {
            bind(valueRef, listView);
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

    private static void bindInteger(final Ref valueRef, final Label label, BindingContext context) {
        new RefPropertyBinding(valueRef, createIntegerProperty(label.textProperty(), context));
    }

    private static void bind(final Ref valueRef, final Label label, BindingContext context) {
        new RefPropertyBinding(valueRef, createProperty(label.textProperty(), context));
    }

    private static void bind(final Ref valueRef, final Button button, BindingContext context) {
        new RefPropertyBinding(valueRef, createProperty(button.textProperty(), context));
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

    private static void bind(Ref valueRef, ListView listView) {
        new RefPropertyBinding(valueRef, listView.itemsProperty());
    }

    private static StringProperty createProperty(StringProperty property, BindingContext context) {
        SimpleStringProperty prop = new SimpleStringProperty();
        Bindings.bindBidirectional(prop, property);
        context.add(prop);
        return prop;
    }

    private static IntegerProperty createIntegerProperty(StringProperty property, BindingContext context) {
        SimpleIntegerProperty prop = new SimpleIntegerProperty();
        Bindings.bindBidirectional(property, prop, new NumberStringConverter());
        context.add(prop);
        return prop;
    }

    private static BooleanProperty createBooleanProperty(BooleanProperty property, BindingContext context) {
        SimpleBooleanProperty prop = new SimpleBooleanProperty();
        Bindings.bindBidirectional(prop, property);
        prop.unbind();
        context.add(prop);
        return prop;
    }
}