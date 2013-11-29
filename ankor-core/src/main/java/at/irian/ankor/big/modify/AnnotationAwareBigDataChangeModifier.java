package at.irian.ankor.big.modify;

import at.irian.ankor.annotation.ModelPropertyAnnotationsFinder;
import at.irian.ankor.big.AnkorBigList;
import at.irian.ankor.big.AnkorBigMap;
import at.irian.ankor.change.Change;
import at.irian.ankor.ref.Ref;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
class AnnotationAwareBigDataChangeModifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BigListAwareSendReceiveModifier.class);

    public Change modify(Change change, Ref changedProperty) {
        switch (change.getType()) {
            case value:
            {
                Object modifiedChangeValue = handleChangeValueBeforeSend(change.getValue(),
                                                                         getPropertyAnnotations(changedProperty));
                return change.withValue(modifiedChangeValue);
            }
            case insert:
            case delete:
            case replace:
                return change;
            default:
                throw new IllegalArgumentException("Unknown change type " + change.getType());
        }
    }

    private Annotation[] getPropertyAnnotations(Ref changedProperty) {
        return new ModelPropertyAnnotationsFinder().getModelPropertyAnnotations(changedProperty);
    }

    private Object handleChangeValueBeforeSend(Object value, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            value = handleChangeValueBeforeSend(value, annotation);
        }
        return value;
    }

    private Object handleChangeValueBeforeSend(Object value, Annotation annotation) {
        if (annotation instanceof AnkorBigList) {
            ListToBigListDummyConverter converter
                    = ListToBigListDummyConverter.createFromAnnotation((AnkorBigList) annotation);
            return converter.convert((Collection) value);
        }
        if (annotation instanceof AnkorBigMap) {
            MapToBigMapDummyConverter converter
                    = MapToBigMapDummyConverter.createFromAnnotation((AnkorBigMap) annotation);
            return converter.convert((Map) value);
        }
        return value;
    }

}
