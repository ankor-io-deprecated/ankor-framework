package at.irian.ankor.annotation;

import at.irian.ankor.big.BigListMetadata;
import at.irian.ankor.big.BigMapMetadata;
import at.irian.ankor.delay.FloodControlMetadata;
import at.irian.ankor.ref.TypedRef;
import at.irian.ankor.ref.match.RefMatcherFactory;
import at.irian.ankor.ref.match.pattern.AntlrRefMatcherFactory;
import at.irian.ankor.state.StateHolder;
import at.irian.ankor.viewmodel.factory.InitMethodMetadata;
import at.irian.ankor.viewmodel.metadata.*;
import at.irian.ankor.viewmodel.watch.WatchedPropertyMetadata;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public class AnnotationBeanMetadataProvider implements BeanMetadataProvider {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationBeanMetadataProvider.class);

    private static Map<Class<?>, BeanMetadata> CACHE = new ConcurrentHashMap<Class<?>, BeanMetadata>();

    private final RefMatcherFactory refMatcherFactory = new AntlrRefMatcherFactory();

    @Override
    public BeanMetadata getMetadata(Object viewModelBean) {
        if (viewModelBean == null) {
            throw new NullPointerException("viewModelBean");
        }

        Class<?> type = viewModelBean.getClass();

        return getMetadata(type);
    }

    @Override
    public BeanMetadata getMetadata(Class<?> type) {
        BeanMetadata beanMetadata = CACHE.get(type);
        if (beanMetadata != null) {
            return beanMetadata;
        }

        beanMetadata = createBeanTypeInfo(type);
        CACHE.put(type, beanMetadata);

        return beanMetadata;
    }

    private BeanMetadata createBeanTypeInfo(Class<?> type) {
        BeanMetadata beanMetadata = BeanMetadata.EMPTY_BEAN_METADATA;
        beanMetadata = scanFields(beanMetadata, type);
        beanMetadata = scanMethods(beanMetadata, type);
        return beanMetadata;
    }

    private BeanMetadata scanFields(BeanMetadata beanMetadata, Class<?> type) {

        for (Field field : type.getDeclaredFields()) {

            beanMetadata = beanMetadata.withTypedProperty(field.getName(), field.getType());

            AnkorWatched watchedAnnotation = field.getAnnotation(AnkorWatched.class);
            if (watchedAnnotation != null) {
                beanMetadata = addWatchedMetadata(beanMetadata, field.getName(), field, watchedAnnotation);
            }

            AnkorBigList bigListAnnotation = field.getAnnotation(AnkorBigList.class);
            if (bigListAnnotation != null) {
                beanMetadata = addBigListMetadata(beanMetadata, field.getName(), bigListAnnotation);
            }

            AnkorBigMap bigMapAnnotation = field.getAnnotation(AnkorBigMap.class);
            if (bigMapAnnotation != null) {
                beanMetadata = addBigMapMetadata(beanMetadata, field.getName(), bigMapAnnotation);
            }

            Virtual virtualAnnotation = field.getAnnotation(Virtual.class);
            if (virtualAnnotation != null) {
                beanMetadata = beanMetadata.withVirtualProperty(field.getName());
            }

            StateHolder stateHolderAnnotation = field.getAnnotation(StateHolder.class);
            if (stateHolderAnnotation != null) {
                beanMetadata = beanMetadata.withStateHolderProperty(field.getName());
            }
        }

        Class<?> superclass = type.getSuperclass();
        if (superclass != null) {
            beanMetadata = scanFields(beanMetadata, superclass);
        }

        return beanMetadata;
    }

    private BeanMetadata addWatchedMetadata(BeanMetadata beanMetadata,
                                            String propertyName, Field field, // todo  also support setter as field accessor
                                            AnkorWatched watchedAnnotation) {
        WatchedPropertyMetadata metadata = new WatchedPropertyMetadata(watchedAnnotation.diffThreshold(),
                                                                       field);
        beanMetadata = beanMetadata.withGenericPropertyMetadata(propertyName, metadata);
        return beanMetadata;
    }

    private BeanMetadata addBigListMetadata(BeanMetadata beanMetadata,
                                            String propertyName,
                                            AnkorBigList bigListAnnotation) {
        Class<?> missingElementSubstitute = bigListAnnotation.missingElementSubstitute();
        BigListMetadata metadata = new BigListMetadata(bigListAnnotation.threshold(),
                                                       bigListAnnotation.initialSize(),
                                                       bigListAnnotation.chunkSize(),
                                                       missingElementSubstitute.equals(AnkorBigList.Null.class)
                                                       ? null
                                                       : missingElementSubstitute);
        beanMetadata = beanMetadata.withGenericPropertyMetadata(propertyName, metadata);
        return beanMetadata;
    }

    private BeanMetadata addBigMapMetadata(BeanMetadata beanMetadata,
                                           String propertyName,
                                           AnkorBigMap bigMapAnnotation) {
        Class<?> missingValueSubstitute = bigMapAnnotation.missingValueSubstitute();
        BigMapMetadata metadata = new BigMapMetadata(bigMapAnnotation.threshold(),
                                                     bigMapAnnotation.initialSize(),
                                                     missingValueSubstitute.equals(AnkorBigMap.Null.class)
                                                     ? null
                                                     : missingValueSubstitute);
        beanMetadata = beanMetadata.withGenericPropertyMetadata(propertyName, metadata);
        return beanMetadata;
    }

    private BeanMetadata scanMethods(BeanMetadata beanMetadata, Class<?> type) {

        boolean autoSignalForAllSetters = (type.getAnnotation(AutoSignal.class) != null);

        Collection<ChangeListenerMetadata> changeListeners = new ArrayList<ChangeListenerMetadata>();
        Collection<ActionListenerMetadata> actionListeners = new ArrayList<ActionListenerMetadata>();
        for (Method method : type.getDeclaredMethods()) {

            ChangeListener changeListenerAnnotation = method.getAnnotation(ChangeListener.class);
            if (changeListenerAnnotation != null) {
                for (String pattern : changeListenerAnnotation.pattern()) {
                    ParameterMetadata[] parameters = getMethodParameters(method);
                    InvocationMetadata invocation = new InvocationMetadata(method, parameters);

                    // TODO: Check backRef count in pattern against method parameter count
                    changeListeners.add(new ChangeListenerMetadata(refMatcherFactory.getRefMatcher(pattern),
                                                                   invocation));
                }
            }

            ActionListener actionListenerAnnotation = method.getAnnotation(ActionListener.class);
            if (actionListenerAnnotation != null) {
                ParameterMetadata[] parameters = getMethodParameters(method);
                InvocationMetadata invocation = new InvocationMetadata(method, parameters
                );
                for (String pattern : actionListenerAnnotation.pattern()) {
                    String name = actionListenerAnnotation.name();
                    actionListeners.add(new ActionListenerMetadata(name.isEmpty() ? method.getName() : name,
                                                                   pattern.isEmpty()
                                                                   ? null
                                                                   : refMatcherFactory.getRefMatcher(pattern),
                                                                   invocation));
                }
            }

            boolean setterAutoSignal = false;
            AutoSignalMetadata signalMetadata = AutoSignalMetadata.empty();
            AutoSignal autoSignalAnnotation = method.getAnnotation(AutoSignal.class);
            if (autoSignalAnnotation != null) {
                String[] paths = autoSignalAnnotation.value();
                if (paths.length == 0 || (paths.length == 1 && paths[0].isEmpty())) {
                    if (isSetter(method)) {
                        String propertyName = Introspector.decapitalize(method.getName().substring(3));
                        signalMetadata = signalMetadata.withPath('.' + propertyName);
                        setterAutoSignal = true;
                    } else {
                        throw new IllegalStateException("Method " + method + " is no setter, but is annotated with " + AutoSignal.class.getName() + " without a path");
                    }
                } else {
                    for (String path : paths) {
                        signalMetadata = signalMetadata.withPath(path);
                    }
                }
            }

            if (!setterAutoSignal && autoSignalForAllSetters && isSetter(method)) {
                String propertyName = Introspector.decapitalize(method.getName().substring(3));
                signalMetadata = signalMetadata.withPath('.' + propertyName);
            }

            if (!signalMetadata.getPaths().isEmpty()) {
                beanMetadata = beanMetadata.withMethodMetadata(method, signalMetadata);
            }

            if (isSetter(method) || isGetter(method)) {
                beanMetadata = beanMetadata.withTypedProperty(getPropertyNameFromMethod(method), method.getReturnType());
            }

            AnkorBigList bigListAnnotation = method.getAnnotation(AnkorBigList.class);
            if (bigListAnnotation != null) {
                beanMetadata = addBigListMetadata(beanMetadata, getPropertyNameFromMethod(method), bigListAnnotation);
            }

            AnkorBigMap bigMapAnnotation = method.getAnnotation(AnkorBigMap.class);
            if (bigMapAnnotation != null) {
                beanMetadata = addBigMapMetadata(beanMetadata, getPropertyNameFromMethod(method), bigMapAnnotation);
            }

            AnkorInit initAnnotation = method.getAnnotation(AnkorInit.class);
            if (initAnnotation != null) {
                beanMetadata = beanMetadata.withMethodMetadata(method, InitMethodMetadata.INSTANCE);
            }

            AnkorFloodControl floodControlAnnotation = method.getAnnotation(AnkorFloodControl.class);
            if (floodControlAnnotation != null) {
                beanMetadata = beanMetadata.withMethodMetadata(method, new FloodControlMetadata(floodControlAnnotation.delayMillis()));
            }

            Virtual virtualAnnotation = method.getAnnotation(Virtual.class);
            if (virtualAnnotation != null) {
                beanMetadata = beanMetadata.withVirtualProperty(getPropertyNameFromMethod(method));
            }
        }

        beanMetadata = beanMetadata.withChangeListeners(changeListeners);
        beanMetadata = beanMetadata.withActionListeners(actionListeners);

        Class<?> superclass = type.getSuperclass();
        if (superclass != null) {
            beanMetadata = scanMethods(beanMetadata, superclass);
        }

        return beanMetadata;
    }

    private boolean isSetter(Method method) {
        return method.getName().startsWith("set")
               && method.getParameterTypes().length == 1
               && method.getReturnType().equals(Void.TYPE);
    }

    private boolean isGetter(Method method) {
        return (method.getName().startsWith("get") || method.getName().startsWith("is"))
               && method.getParameterTypes().length == 0
               && !method.getReturnType().equals(Void.TYPE);
    }

    private String getPropertyNameFromMethod(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith("get")) {
            return Introspector.decapitalize(methodName.substring(3));
        } else if (methodName.startsWith("is")) {
            return Introspector.decapitalize(methodName.substring(2));
        } else if (methodName.startsWith("set")) {
            return Introspector.decapitalize(methodName.substring(3));
        } else {
            throw new IllegalArgumentException("Not a valid getter or setter method: " + method);
        }
    }


    private ParameterMetadata[] getMethodParameters(Method method) {
        ParameterMetadata[] parameters = new ParameterMetadata[method.getParameterAnnotations().length];
        for (int i = 0; i < method.getParameterAnnotations().length; i++) {
            Annotation[] paramAnnotations = method.getParameterAnnotations()[i];
            String paramName = getParameterNameFromAnnotations(paramAnnotations);
            Class<?> type = method.getParameterTypes()[i];
            boolean backReference = TypedRef.class.isAssignableFrom(type);
            parameters[i] = new ParameterMetadata(paramName, backReference);
        }
        return parameters;
    }

    private String getParameterNameFromAnnotations(Annotation[] paramAnnotations) {
        for (Annotation paramAnnotation : paramAnnotations) {
            if (paramAnnotation instanceof Param) {
                return ((Param) paramAnnotation).value();
            }
        }
        return null;
    }


}
