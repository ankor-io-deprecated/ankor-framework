package at.irian.ankor.fx.binding;

import at.irian.ankor.ref.Ref;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;

/**
 * @author Thomas Spiegl
 */
public class BindingsBuilder {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BindingsBuilder.class);

    private Ref valueRef;

    private Text text;

    private Ref itemsRef;

    private TextInputControl inputControl;

    private ComboBox comboBox;

    public static BindingsBuilder newBinding() {
        return new BindingsBuilder();
    }

    public BindingsBuilder bindValue(Ref value) {
        this.valueRef = value;
        return this;
    }

    public BindingsBuilder toText(Text text) {
        this.text = text;
        return this;
    }

    public BindingsBuilder toInput(TextInputControl inputControl) {
        this.inputControl = inputControl;
        return this;
    }

    public BindingsBuilder toCombo(ComboBox comboBox) {
        this.comboBox = comboBox;
        return this;

    }

    public BindingsBuilder withItems(Ref itemsRef) {
        this.itemsRef = itemsRef;
        return this;
    }

    public void createWithin(BindingContext bindingContext) {
        if (text != null) {
            ModelBindings.bind(valueRef, text, bindingContext);
        } else if (comboBox != null) {
            if (itemsRef == null) {
                throw new IllegalStateException("Illegal Binding, missing itemsRef " + this);
            }
            ModelBindings.bind(valueRef, itemsRef, comboBox);
        } else if (inputControl != null) {
            ModelBindings.bind(valueRef, inputControl, bindingContext);
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
}
