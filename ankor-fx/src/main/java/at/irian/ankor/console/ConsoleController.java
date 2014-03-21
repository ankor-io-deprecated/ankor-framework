package at.irian.ankor.console;

import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.converter.Converter;
import at.irian.ankor.fx.binding.fxref.FxRef;
import at.irian.ankor.fx.binding.fxref.FxRefs;
import at.irian.ankor.fx.controller.FXControllerSupport;
import at.irian.ankor.ref.Ref;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Manfred Geiler
 */
public class ConsoleController implements Initializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConsoleController.class);

    @FXML
    private Text inbound;
    @FXML
    private Text outbound;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Ref rootRef = FxRefs.refFactory().ref("ankorConsole");
        FXControllerSupport.init(this, rootRef);
    }

    @ChangeListener(pattern = "ankorConsole")
    public void modelRootChanged() {
        final FxRef rootRef = FxRefs.refFactory().ref("ankorConsole");

        Converter<Number, String> converter = new Converter<Number, String>() {
            @Override
            public String convertTo(Number number) {
                return number != null ? number.toString() : "";
            }
        };

        inbound.textProperty().bind(FxRefs.convert(rootRef.appendPath("totalInboundMessages").<Number>fxObservable(), converter));
        outbound.textProperty().bind(FxRefs.convert(rootRef.appendPath("totalOutboundMessages").<Number>fxObservable(),
                                                    converter));
    }

}
