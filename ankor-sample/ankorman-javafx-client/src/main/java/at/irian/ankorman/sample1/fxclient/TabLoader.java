package at.irian.ankorman.sample1.fxclient;

import at.irian.ankor.fx.app.ActionCompleteCallback;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.util.Callback;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static at.irian.ankorman.sample1.fxclient.App.facade;

/**
 * @author Thomas Spiegl
 */
public class TabLoader {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TabLoader.class);

    private final String tabId;
    private final TabType tabType;

    public TabLoader(TabType tabType) {
        this.tabId = TabIds.next();
        this.tabType = tabType;
        if (tabType == null) {
            throw new IllegalStateException("tabType is null");
        }
    }

    public void loadTabTo(TabPane tabPane) {
        if (tabType == null) {
            throw new IllegalStateException("tabType is null");
        }
        if (tabPane == null) {
            throw new IllegalStateException("tabPane is null");
        }
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(tabType.getFxmlResource()));
        loader.setControllerFactory(new TabControllerFactory());
        facade().openTab(tabId, tabType, new ActionCompleteCallbackImpl(loader, tabPane));
    }

    private class ActionCompleteCallbackImpl implements ActionCompleteCallback {

        private final FXMLLoader loader;
        private final TabPane tabPane;

        private ActionCompleteCallbackImpl(FXMLLoader loader, TabPane tabPane) {
            this.loader = loader;
            this.tabPane = tabPane;
        }

        public void onComplete() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Tab tab = (Tab) loader.load();
                        tabPane.getTabs().add(tab);
                        tabPane.getSelectionModel().select(tab);
                    } catch (IOException e) {
                        throw new IllegalStateException("cannot load animal_search_tab.fxml", e);
                    }
                }

            });
        }
    }

    private class TabControllerFactory implements Callback<Class<?>, Object> {

        @Override
        public Object call(Class<?> aClass) {
            try {
                return aClass.getConstructor(String.class).newInstance(tabId);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException("Cannot load tab for " + aClass.getName() + ": Constructor(String tabId) not found");
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("InvocationTargetException for " + aClass.getName());
            } catch (InstantiationException e) {
                throw new IllegalStateException("InstantiationException for " + aClass.getName());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("IllegalAccessException for " + aClass.getName());
            }
        }
    }


}
