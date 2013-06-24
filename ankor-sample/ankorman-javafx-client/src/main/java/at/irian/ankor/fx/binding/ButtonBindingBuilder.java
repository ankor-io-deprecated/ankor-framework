package at.irian.ankor.fx.binding;

import at.irian.ankor.ref.Ref;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * @author Thomas Spiegl
 */
public class ButtonBindingBuilder {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ButtonBindingBuilder.class);

    private Button button;

    private Ref valueRef;

    private ClickAction clickAction;

    public static ButtonBindingBuilder onButtonClick(Button button) {
        return new ButtonBindingBuilder().onClick(button);
    }

    private ButtonBindingBuilder onClick(Button button) {
        this.button = button;
        return this;
    }

    public ButtonBindingBuilder call(ClickAction clickAction) {
        this.clickAction = clickAction;
        return this;
    }

    public ButtonBindingBuilder refresh(Ref valueRef) {
        this.valueRef = valueRef;
        return this;
    }

    public void bind() {
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
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ButtonBindingBuilder{");
        sb.append("button=").append(button);
        sb.append(", valueRef=").append(valueRef);
        sb.append(", clickAction=").append(clickAction);
        sb.append('}');
        return sb.toString();
    }
}
