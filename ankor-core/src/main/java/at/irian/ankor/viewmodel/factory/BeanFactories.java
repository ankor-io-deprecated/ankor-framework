package at.irian.ankor.viewmodel.factory;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.RefAware;

/**
 * @author Manfred Geiler
 */
public final class BeanFactories {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BeanFactories.class);

    private static ThreadLocal<Ref> CURRENT_REF = new ThreadLocal<Ref>();

    private BeanFactories() {}

    public static <T> T newInstance(Class<T> type, Ref ref, Object... constructorArgs) {
        Ref previousRef = CURRENT_REF.get();
        CURRENT_REF.set(ref);
        try {
            BeanFactory beanFactory = ref.context().beanFactory();
            return beanFactory.createNewInstance(type, ref, constructorArgs);
        } finally {
            CURRENT_REF.set(previousRef);
        }
    }

    public static <T> T newPropertyInstance(Class<T> type, Ref beanRef, String propertyName, Object... constructorArgs) {
        Ref ref = beanRef.appendPath(propertyName);
        return newInstance(type, ref, constructorArgs);
    }

    public static <T> T newPropertyInstance(Class<T> type, Object bean, String propertyName, Object... constructorArgs) {
        if (bean instanceof RefAware) {
            return newPropertyInstance(type, ((RefAware) bean).getRef(), propertyName, constructorArgs);
        } else {
            throw new IllegalArgumentException("Given bean " + bean + " does not implement interface " + RefAware.class.getName());
        }
    }

    public static Ref currentRef() {
        Ref ref = CURRENT_REF.get();
        if (ref == null) {
            throw new IllegalStateException("No current ref - this method must only be called during view model bean construction time!");
        }
        return ref;
    }

}
