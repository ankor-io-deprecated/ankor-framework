package at.irian.ankor.todosample.fxclient.task;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import at.irian.ankor.ref.listener.RefListeners;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import static at.irian.ankor.fx.controller.FXControllerAnnotationSupport.annotationSupport;
import static at.irian.ankor.todosample.fxclient.App.refFactory;

public abstract class AnkorController implements Initializable {

    public AnkorController() {
        annotationSupport().registerChangeListeners(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // XXX: Annotation syntax not supported
        RefListeners.addPropChangeListener(refFactory().rootRef().append("model"), new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                initialize(refFactory().rootRef().append("model"));
            }
        });

        refFactory().rootRef().fire(new Action("init"));
    }

    abstract void initialize(Ref modelRef);
}
