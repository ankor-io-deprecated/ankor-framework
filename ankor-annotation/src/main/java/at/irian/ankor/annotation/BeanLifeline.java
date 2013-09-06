package at.irian.ankor.annotation;

import java.lang.ref.WeakReference;

/**
 * @author Manfred Geiler
 */
public class BeanLifeline implements Lifeline {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BeanLifeline.class);

    private final WeakReference<Object> weakBeanReference;

    public BeanLifeline(Object bean) {
        this.weakBeanReference = new WeakReference<Object>(bean);
    }

    @Override
    public boolean isAlive() {
        return weakBeanReference.get() != null;
    }
}
