package at.irian.ankor.fx.binding.cache;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class FxCacheSupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FxCacheSupport.class);

    private static final String BINDING_CACHE_MODEL_CONTEXT_ATTRIBUTE = BindingCache.class.getName();

    public static BindingCache getBindingCache(Ref ref) {
        ModelSession modelSession = ref.context().modelSession();
        BindingCache bindingCache = modelSession.getAttribute(BINDING_CACHE_MODEL_CONTEXT_ATTRIBUTE);
        if (bindingCache == null) {
            bindingCache = new BindingCache();
            modelSession.setAttribute(BINDING_CACHE_MODEL_CONTEXT_ATTRIBUTE, bindingCache);
        }
        return bindingCache;
    }

}
