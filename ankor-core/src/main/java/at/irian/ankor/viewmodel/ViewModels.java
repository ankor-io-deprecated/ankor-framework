package at.irian.ankor.viewmodel;

import at.irian.ankor.ref.Ref;

/**
 * Utilities for working with view model beans.
 *
 * @author Manfred Geiler
 */
public final class ViewModels {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModels.class);

    private ViewModels() {}


    public static Ref ref(Object viewModelBean) {
        if (viewModelBean instanceof RefAware) {
            return ((RefAware) viewModelBean).getRef();
        } else {
            throw new IllegalArgumentException("View model bean " + viewModelBean + " does not implement RefAware");
        }
    }

}
