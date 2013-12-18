package at.irian.ankor.viewmodel.proxy;

import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import net.sf.cglib.proxy.CallbackFilter;

import java.lang.reflect.Method;

/**
 * @author Manfred Geiler
 */
public class ViewModelCallbackFilter implements CallbackFilter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelCallbackFilter.class);

    private final BeanMetadata metadata;

    public ViewModelCallbackFilter(BeanMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public int accept(Method method) {
        if (RefAwareCallback.accept(method)) {
            return 1;
        }

        if (AutoSignalCallback.accept(method, metadata)) {
            return 2;
        }

        if (InitMethodCallback.accept(method, metadata)) {
            return 3;
        }

        return 0; // PassThroughCallback
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ViewModelCallbackFilter that = (ViewModelCallbackFilter) o;

        return metadata.equals(that.metadata);
    }

    @Override
    public int hashCode() {
        return metadata.hashCode();
    }
}
