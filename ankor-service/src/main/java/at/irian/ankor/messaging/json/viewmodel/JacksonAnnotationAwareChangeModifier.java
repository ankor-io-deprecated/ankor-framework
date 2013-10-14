package at.irian.ankor.messaging.json.viewmodel;

import at.irian.ankor.annotation.ModelPropertyAnnotationsFinder;
import at.irian.ankor.change.Change;
import at.irian.ankor.messaging.ChangeModifier;
import at.irian.ankor.ref.Ref;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.Converter;

import java.lang.annotation.Annotation;

/**
 * @author Manfred Geiler
 */
public class JacksonAnnotationAwareChangeModifier implements ChangeModifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JacksonAnnotationAwareChangeModifier.class);

    @Override
    public Change modify(Change change, Ref changedProperty) {
        Annotation[] modelPropertyAnnotations
                = new ModelPropertyAnnotationsFinder().getModelPropertyAnnotations(changedProperty);
        change = handleRecursively(change, modelPropertyAnnotations);
        return change;
    }

    private Change handleRecursively(Change change, Annotation[] modelPropertyAnnotations) {
        for (Annotation annotation : modelPropertyAnnotations) {
            change = handle(change, annotation);
            if (annotation.annotationType().getAnnotation(JacksonAnnotationsInside.class) != null) {
                change = handleRecursively(change, annotation.annotationType().getDeclaredAnnotations());
            }
        }
        return change;
    }

    private Change handle(Change change, Annotation annotation) {

        if (annotation instanceof JsonSerialize) {
            Class<? extends Converter> converter = ((JsonSerialize) annotation).converter();
            if (converter != Converter.None.class) {
                return convert(change, converter);
            }
        }

        return change;
    }

    private Change convert(Change change, Class<? extends Converter> converter) {
        try {
            Object value = change.getValue();
            @SuppressWarnings("unchecked") Object convertedValue = converter.newInstance().convert(value);
            return change.withValue(convertedValue);
        } catch (Exception e) {
            throw new RuntimeException("Error converting " + change, e);
        }
    }
}
