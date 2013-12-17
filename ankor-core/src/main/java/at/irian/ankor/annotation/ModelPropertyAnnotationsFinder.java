package at.irian.ankor.annotation;

import at.irian.ankor.ref.Ref;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * todo   caching...
 *
 * @author Manfred Geiler
 */
@Deprecated
public class ModelPropertyAnnotationsFinder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelPropertyAnnotationsFinder.class);

    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    public Annotation[] getModelPropertyAnnotations(Ref propertyRef) {

        if (propertyRef.isRoot()) {
            return EMPTY_ANNOTATIONS;
        }

        Object parentValue = propertyRef.parent().getValue();
        if (parentValue == null) {
            // prevent NPE, happens rarely, but may happen (race conditions, etc.)
            return EMPTY_ANNOTATIONS;
        }

        String propertyName = propertyRef.propertyName();

        Class<?> parentType = parentValue.getClass();
        Field field = findField(parentType, propertyName);
        if (field == null) {
            // todo   support getter/setter annotations
            return EMPTY_ANNOTATIONS;
        }

        return field.getDeclaredAnnotations();
    }

    public <A extends Annotation> A findModelPropertyAnnotations(Ref propertyRef, Class<A> annotationType) {
        Annotation[] annotations = getModelPropertyAnnotations(propertyRef);
        for (Annotation annotation : annotations) {
            if (annotationType.isAssignableFrom(annotation.getClass())) {
                //noinspection unchecked
                return (A)annotation;
            }
        }
        return null;
    }

    private Field findField(Class<?> type, String fieldName) {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superType = type.getSuperclass();
            if (superType != null) {
                return findField(superType, fieldName);
            }
            return null;
        }
    }

}
