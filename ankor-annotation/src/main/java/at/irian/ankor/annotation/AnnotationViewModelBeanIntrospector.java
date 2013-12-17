package at.irian.ankor.annotation;

import at.irian.ankor.ref.TypedRef;
import at.irian.ankor.ref.match.RefMatcherFactory;
import at.irian.ankor.ref.match.pattern.AntlrRefMatcherFactory;
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
public class AnnotationViewModelBeanIntrospector implements BeanMetadataProvider {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnnotationViewModelBeanIntrospector.class);

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
        BeanMetadata beanMetadata = new BeanMetadata();
        //beanMetadata = scanType(beanMetadata, type);
        beanMetadata = scanFields(beanMetadata, type);
        beanMetadata = scanMethods(beanMetadata, type);
        return beanMetadata;
    }

//    private BeanMetadata scanType(BeanMetadata beanMetadata, Class<?> type) {
//        return beanMetadata;
//    }

    private BeanMetadata scanFields(BeanMetadata beanMetadata, Class<?> type) {

        for (Field field : type.getDeclaredFields()) {
            AnkorWatched watchedAnnotation = field.getAnnotation(AnkorWatched.class);
            if (watchedAnnotation != null) {
                beanMetadata = beanMetadata.withPropertyMetadata(field.getName(),
                                                                 new WatchedPropertyMetadata(
                                                                         watchedAnnotation.diffThreshold(),
                                                                         field));
            }
        }

        Class<?> superclass = type.getSuperclass();
        if (superclass != null) {
            beanMetadata = scanFields(beanMetadata, superclass);
        }

        return beanMetadata;
    }

    private BeanMetadata scanMethods(BeanMetadata beanMetadata, Class<?> type) {

        boolean autoSignalForAllSetters = (type.getAnnotation(AutoSignal.class) != null);

        Collection<ChangeListenerMetadata> changeListeners = new ArrayList<ChangeListenerMetadata>();
        Collection<ChangeSignalMetadata> changeSignals = new ArrayList<ChangeSignalMetadata>();
        Collection<ActionListenerMetadata> actionListeners = new ArrayList<ActionListenerMetadata>();
        for (Method method : type.getDeclaredMethods()) {

            ChangeListener changeListenerAnnotation = method.getAnnotation(ChangeListener.class);
            if (changeListenerAnnotation != null) {
                for (String pattern : changeListenerAnnotation.pattern()) {
                    ParameterMetadata[] parameters = getMethodParameters(method);
                    InvocationMetadata invocation = new InvocationMetadata(method, parameters);
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
            AutoSignal autoSignalAnnotation = method.getAnnotation(AutoSignal.class);
            if (autoSignalAnnotation != null) {
                String[] paths = autoSignalAnnotation.value();
                if (paths.length == 0 || (paths.length == 1 && paths[0].isEmpty())) {
                    if (isSetter(method)) {
                        String propertyName = Introspector.decapitalize(method.getName().substring(3));
                        changeSignals.add(new ChangeSignalMetadata(method, '.' + propertyName));
                        setterAutoSignal = true;
                    } else {
                        throw new IllegalStateException("Method " + method + " is no setter, but is annotated with " + AutoSignal.class.getName() + " without a path");
                    }
                } else {
                    for (String path : paths) {
                        changeSignals.add(new ChangeSignalMetadata(method, path));
                    }
                }
            }

            if (!setterAutoSignal && autoSignalForAllSetters && isSetter(method)) {
                String propertyName = Introspector.decapitalize(method.getName().substring(3));
                changeSignals.add(new ChangeSignalMetadata(method, '.' + propertyName));
            }

        }

        beanMetadata = beanMetadata.withChangeListeners(changeListeners);
        beanMetadata = beanMetadata.withActionListeners(actionListeners);
        beanMetadata = beanMetadata.withChangeSignals(changeSignals);

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
