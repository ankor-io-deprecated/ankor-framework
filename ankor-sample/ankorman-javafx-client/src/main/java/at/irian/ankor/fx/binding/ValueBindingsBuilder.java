package at.irian.ankor.fx.binding;

import at.irian.ankor.ref.Ref;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

    private Ref itemsRef;

    private TextInputControl inputControl;

    private ComboBox comboBox;

    private TableView tableView;

    private Button button;

    private ClickAction clickAction;

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

    public ValueBindingsBuilder toButton(Button button) {
        this.button = button;
        return this;
    }

    public ValueBindingsBuilder onClick(ClickAction clickAction) {
        this.clickAction = clickAction;
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


    public ValueBindingsBuilder withSelectItems(Ref itemsRef) {
        this.itemsRef = itemsRef;
        return this;
    }

    public void createWithin(BindingContext bindingContext) {
        if (text != null) {
            bind(valueRef, text, bindingContext);
        } else if (comboBox != null) {
            if (itemsRef == null) {
                throw new IllegalStateException("Illegal Binding, missing itemsRef " + this);
            }
            bind(valueRef, itemsRef, comboBox);
        } else if (inputControl != null) {
            bind(valueRef, inputControl, bindingContext);
        } else if (tableView != null) {
            bind(valueRef, tableView);
        } else if(button != null) {
            if (valueRef == null || clickAction == null) {
                throw new IllegalStateException("Illegal Binding, missing valueRef or clickAction " + this);
            }
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    clickAction.onClick(valueRef);
                    valueRef.setValue(valueRef.getValue());
                }
            });
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
        new RemoteBinding(valueRef, comboBox.valueProperty());
        new RemoteBinding(itemsRef, comboBox.itemsProperty());
    }

    private static void bind(final Ref valueRef, final Text text, BindingContext context) {
        new RemoteBinding(valueRef, createProperty(text.textProperty(), context));
    }

    private static void bind(final Ref valueRef, final TextInputControl control, BindingContext context) {
        new RemoteBinding(valueRef, createProperty(control.textProperty(), context));
    }

    private static void bind(Ref valueRef, TableView tableView) {
        new RemoteBinding(valueRef, tableView.itemsProperty());
    }

    private static SimpleStringProperty createProperty(StringProperty stringProperty, BindingContext context) {
        SimpleStringProperty prop = new SimpleStringProperty();
        Bindings.bindBidirectional(prop, stringProperty);
        context.add(prop);
        return prop;
    }

}