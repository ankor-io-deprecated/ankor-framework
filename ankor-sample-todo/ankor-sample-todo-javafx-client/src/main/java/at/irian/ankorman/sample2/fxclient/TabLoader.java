package at.irian.ankorman.sample2.fxclient;

import at.irian.ankor.action.ActionBuilder;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.listener.RefChangeListener;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.util.Callback;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static at.irian.ankor.ref.listener.RefListeners.addChangeListener;
import static at.irian.ankor.ref.listener.RefListeners.removeListener;
import static at.irian.ankorman.sample2.fxclient.App.refFactory;

/**
 * @author Thomas Spiegl
 */
public class TabLoader {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TabLoader.class);

    private final String tabId;
    private final TabType tabType;
    private final Ref tabsRef;

    public TabLoader(TabType tabType) {
        if (tabType == null) {
            throw new IllegalStateException("tabType is null");
        }
        this.tabId = TabIds.next();
        this.tabType = tabType;
        this.tabsRef = refFactory().rootRef().append("tabs");
    }

    public void loadTabTo(final TabPane tabPane) {
        if (tabPane == null) {
            throw new IllegalStateException("tabPane is null");
        }

        final Ref tabRef = tabsRef.append(tabId);

        addChangeListener(tabsRef, new RefChangeListener() {
            @Override
            public void processChange(Ref changedProperty) {
                if (changedProperty.equals(tabRef)) {
                    removeListener(tabRef.context(), this);
                    showTab(tabPane);
                }
            }
        });

        // load tab
        tabsRef.fireAction(action()
                .withName(tabType.getActionName())
                .withParam("tabId", tabId).create());
    }

    private static ActionBuilder action() {
        return new ActionBuilder();
    }

    private void showTab(final TabPane tabPane) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Tab tab = (Tab) loader().load();
                    tabPane.getTabs().add(tab);
                    tabPane.getSelectionModel().select(tab);
                } catch (IOException e) {
                    throw new IllegalStateException("cannot load animal_search_tab.fxml", e);
                }
            }
        });
    }

    private FXMLLoader loader() {
        final FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(tabType.getFxmlResource()));
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
        return loader;
    }
}
