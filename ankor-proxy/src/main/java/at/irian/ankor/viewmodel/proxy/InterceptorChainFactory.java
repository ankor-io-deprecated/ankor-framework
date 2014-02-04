package at.irian.ankor.viewmodel.proxy;

import at.irian.ankor.viewmodel.metadata.BeanMetadata;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public class InterceptorChainFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InterceptorChainFactory.class);

    private static final Map<CacheKey, MethodInterceptor[]> CACHE = new ConcurrentHashMap<CacheKey, MethodInterceptor[]>();

    private final BeanMetadata metadata;

    private MethodInterceptor[] interceptorCandidates;

    public InterceptorChainFactory(BeanMetadata metadata) {
        this.metadata = metadata;
        init(metadata);
    }

    private void init(BeanMetadata metadata) {
        this.interceptorCandidates = new MethodInterceptor[] {
            new AutoSignalMethodInterceptor(metadata),
            new FloodControlMethodInterceptor(metadata),
            new RefAwareMethodInterceptor()
        };
    }

    public MethodInterceptor[] createInterceptorChain(Method method) {
        MethodInterceptor[] chain;

        CacheKey cacheKey = new CacheKey(metadata, method);
        chain = CACHE.get(cacheKey);

        if (chain == null) {
            List<MethodInterceptor> list = new ArrayList<MethodInterceptor>();
            for (MethodInterceptor candidate : interceptorCandidates) {
                if (candidate.accept(method)) {
                    list.add(candidate);
                }
            }
            chain = list.toArray(new MethodInterceptor[list.size()]);
            CACHE.put(cacheKey, chain);
        }

        return chain;
    }



    private static class CacheKey {
        private final BeanMetadata metadata;
        private final Method method;

        public CacheKey(BeanMetadata metadata, Method method) {
            this.metadata = metadata;
            this.method = method;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CacheKey cacheKey = (CacheKey) o;

            return metadata.equals(cacheKey.metadata) && method.equals(cacheKey.method);
        }

        @Override
        public int hashCode() {
            int result = metadata.hashCode();
            result = 31 * result + method.hashCode();
            return result;
        }
    }

}
