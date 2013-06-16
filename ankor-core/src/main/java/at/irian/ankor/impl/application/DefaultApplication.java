package at.irian.ankor.impl.application;

import at.irian.ankor.api.application.Application;
import at.irian.ankor.impl.el.AnkorELResolver;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;

/**
 */
public class DefaultApplication extends Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultApplication.class);

    private final ELResolver elResolver;
    private final ExpressionFactory expressionFactory;

    public DefaultApplication() {
        elResolver = new AnkorELResolver();
        expressionFactory = ExpressionFactory.newInstance();
    }

    @Override
    public ELResolver getELResolver() {
        return elResolver;
    }

    @Override
    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }
}
