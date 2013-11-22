package at.irian.ankor.fx.controller;

import at.irian.ankor.ref.Ref;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings({"TypeParameterExplicitlyExtendsObject", "UnusedDeclaration"})
public class AnkorFXMLLoader extends FXMLLoader {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorFXMLLoader.class);

    private I18nSupport i18nSupport = null;

    public AnkorFXMLLoader() {
    }

    public AnkorFXMLLoader(URL resource) {
        super(resource);
    }

    public AnkorFXMLLoader(URL resource, Ref resourcesRef) {
        super(resource);
        setResourcesRef(resourcesRef);
    }

    public void setResourcesRef(Ref resourcesRef) {
        this.i18nSupport = new I18nSupport(resourcesRef);
    }

    @Override
    public Object load(InputStream inputStream) throws IOException {
        if (getResources() == null) {
            setResources(IGNORE_RESOURCE_BUNDLE);
        }
        Object loaded = super.load(inputStream);
        doPostProcess(loaded);
        return loaded;
    }

    private void doPostProcess(Object node) {

        LOG.info("Post processing {} ...", node);

        if (i18nSupport != null) {
            i18nSupport.bindTextProperty(node);
        }

        recurseChildrenOf(node);
    }

    @SuppressWarnings("unchecked")
    private void recurseChildrenOf(Object node) {
        Class<?> nodeType = node.getClass();
        if (MenuBar.class.isAssignableFrom(nodeType)) {
            recurse(((MenuBar) node).getMenus());
        } else if (Menu.class.isAssignableFrom(nodeType)) {
            recurse(((Menu) node).getItems());
        } else if (Tab.class.isAssignableFrom(nodeType)) {
            doPostProcess(((Tab) node).getContent());
        } else if (TableView.class.isAssignableFrom(nodeType)) {
            recurse(((TableView) node).getColumns());
        }

        // try reflection instead
        Method getChildrenMethod;
        try {
            getChildrenMethod = nodeType.getMethod("getChildren");
        } catch (NoSuchMethodException e) {
            return;
        }

        List<Object> children;
        try {
            children = (List<Object>) getChildrenMethod.invoke(node);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot access children of " + node);
        }

        recurse(children);
    }

    private void recurse(List<? extends Object> childNodes) {
        if (childNodes != null) {
            LOG.info(" ... {} children:", childNodes.size());
            for (Object childNode : childNodes) {
                doPostProcess(childNode);
            }
        }
    }


    private static final ResourceBundle IGNORE_RESOURCE_BUNDLE = new ResourceBundle() {
        @Override
        protected Object handleGetObject(String key) {
            return "%" + key;
        }

        @Override
        public boolean containsKey(String key) {
            return true;
        }

        @Override
        public Enumeration<String> getKeys() {
            return new Enumeration<String>() {
                @Override
                public boolean hasMoreElements() {
                    return false;
                }

                @Override
                public String nextElement() {
                    throw new NoSuchElementException();
                }
            };
        }
    };

}
