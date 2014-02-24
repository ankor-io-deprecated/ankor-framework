package at.irian.ankor.el;

import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.context.ModelContext;
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
    private final ModelContext modelContext;
    private final BeanResolverELResolver beanResolverELResolver;
    private final ModelRootFactory modelRootFactory;

    public AnkorELSupport(ModelContext modelContext, BeanResolver beanResolver, ModelRootFactory modelRootFactory) {
        this.modelRootFactory = modelRootFactory;
        this.expressionFactory = ExpressionFactory.newInstance();
        this.modelContext = modelContext;
        this.beanResolverELResolver = new BeanResolverELResolver(beanResolver);
    }

    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    @Override
    public ELContext getELContextFor(RefFactory refFactory) {
        ModelContextELResolver modelContextELResolver = new ModelContextELResolver(modelContext,
                                                                                   refFactory,
                                                                                   modelRootFactory);
        return new StandardELContext(modelContextELResolver, beanResolverELResolver);
    }
}
