package at.irian.ankor.el;

import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.connection.ModelRootFactory;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import java.beans.PropertyEditorManager;
import java.util.Locale;

/**
 * @author Manfred Geiler
 */
public class AnkorELSupport implements ELSupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorELSupport.class);

    static {
        PropertyEditorManager.registerEditor(Locale.class, LocalePropertyEditor.class);
    }

    private final ExpressionFactory expressionFactory;
    private final ModelSession modelSession;
    private final BeanResolverELResolver beanResolverELResolver;
    private final ModelRootFactory modelRootFactory;

    public AnkorELSupport(ModelSession modelSession, BeanResolver beanResolver, ModelRootFactory modelRootFactory) {
        this.modelRootFactory = modelRootFactory;
        this.expressionFactory = ExpressionFactory.newInstance();
        this.modelSession = modelSession;
        this.beanResolverELResolver = new BeanResolverELResolver(beanResolver);
    }

    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    @Override
    public ELContext getELContextFor(RefFactory refFactory) {
        ModelSessionELResolver modelSessionELResolver = new ModelSessionELResolver(modelSession,
                                                                                   refFactory,
                                                                                   modelRootFactory);
        return new StandardELContext(modelSessionELResolver, beanResolverELResolver);
    }
}
