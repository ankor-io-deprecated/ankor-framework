package at.irian.ankor.annotation;

import at.irian.ankor.ref.Ref;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * todo   caching...
 *
 * @author Manfred Geiler
 */
public class ModelPropertyAnnotationsFinder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelPropertyAnnotationsFinder.class);

    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    public Annotation[] getModelPropertyAnnotations(Ref propertyRef) {

        if (propertyRef.isRoot()) {
            return EMPTY_ANNOTATIONS;
        }

        Object parentValue = propertyRef.parent().getValue();
        String propertyName = propertyRef.propertyName();

        Class<?> parentType = parentValue.getClass();
        Field field;
        try {
            field = parentType.getDeclaredField(propertyName);
        } catch (NoSuchFieldException e) {
            // todo  search in super types
            // todo  also support getter annotations
            return EMPTY_ANNOTATIONS;
        }

        return field.getDeclaredAnnotations();
    }

}
