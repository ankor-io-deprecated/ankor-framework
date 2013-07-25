package at.irian.ankor.model;

import at.irian.ankor.ref.Ref;

import java.lang.reflect.Field;

import static at.irian.ankor.model.ModelProperty.createReferencedProperty;

/**
 * @author Manfred Geiler
 */
public class ModelHelper {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHelper.class);

    public static Initializer initializer(Object modelObject, Ref modelRef) {
        return new Initializer(modelObject, modelRef);
    }

    public static class Initializer {

        private final Object modelObject;
        private final Ref modelRef;

        public Initializer(Object modelObject, Ref modelRef) {
            this.modelObject = modelObject;
            this.modelRef = modelRef;
        }

        public <T> Initializer initAll() {
            for (Field field : modelObject.getClass().getDeclaredFields()) {
                if (ModelProperty.class.isAssignableFrom(field.getType())) {
                    assureAccessible(field);
                    Object currentValue = getValue(field);
                    if (currentValue == null) {
                        ModelProperty<Object> mp = createReferencedProperty(modelRef.append(field.getName()));
                        setValue(field, mp);
                    }
                }
            }
            return this;
        }

        public <T> Initializer withInitialValue(String fieldName, T initialValue) {
            Field field = getFieldByName(fieldName);
            assureAccessible(field);
            ModelProperty<T> mp = getValue(field);
            if (mp != null) {
                mp.putWrappedValue(initialValue);
            } else {
                mp = createReferencedProperty(modelRef, initialValue);
                setValue(field, mp);
            }
            return this;
        }

        private <T> void setValue(Field field, T value) {
            try {
                field.set(modelObject, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error setting value of model field " + field, e);
            }
        }

        private <T> T getValue(Field field) {
            try {
                //noinspection unchecked
                return (T)field.get(modelObject);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error getting value of model field " + field, e);
            }
        }

        private void assureAccessible(Field field) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
        }

        private Field getFieldByName(String fieldName) {
            for (Field field : modelObject.getClass().getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
            throw new IllegalArgumentException("Model object of type " + modelObject.getClass().getName() + " has no field " + fieldName);
        }
    }


}
