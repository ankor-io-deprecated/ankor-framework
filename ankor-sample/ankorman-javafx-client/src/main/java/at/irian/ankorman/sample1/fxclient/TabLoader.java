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

    private final TabPane tabPane;

    public TabLoader(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public void loadTab(TabType tabType) {
        final String tabId = TabIds.next();
        facade().openTab(tabId, tabType, new ActionCompleteCallbackImpl(tabId, tabType.getFxmlResource()));
    }

    private class ActionCompleteCallbackImpl implements ActionCompleteCallback {

        private final String tabId;
        private final String fxmlResource;


        ActionCompleteCallbackImpl(String tabId, String fxmlResource) {
            this.tabId = tabId;
            this.fxmlResource = fxmlResource;
        }

        public void onComplete() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    final Tab tab;
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlResource));
                        loader.setControllerFactory(new Callback<Class<?>, Object>() {
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
                        });
                        tab = (Tab) loader.load();
                        tabPane.getTabs().add(tab);
                        tabPane.getSelectionModel().select(tab);
                    } catch (IOException e) {
                        throw new IllegalStateException("cannot load animal_search_tab.fxml", e);
                    }
                }
            });
        }
    };

}
