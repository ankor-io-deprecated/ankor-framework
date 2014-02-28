package at.irian.ankor.fx.binding.cache;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class FxCacheSupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FxCacheSupport.class);

    private static final String BINDING_CACHE_MODEL_CONTEXT_ATTRIBUTE = BindingCache.class.getName();

    public static BindingCache getBindingCache(Ref ref) {
        BindingCache bindingCache = (BindingCache) ref.context().modelSession()
                                                      .getAttributes().get(BINDING_CACHE_MODEL_CONTEXT_ATTRIBUTE);
        if (bindingCache == null) {
            bindingCache = new BindingCache();
            ref.context().modelSession()
               .getAttributes().put(BINDING_CACHE_MODEL_CONTEXT_ATTRIBUTE, bindingCache);
        }
        return bindingCache;
    }

}
