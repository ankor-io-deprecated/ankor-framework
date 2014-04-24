/*
 * Copyright (C) 2013-2014  Irian Solutions  (http://www.irian.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
