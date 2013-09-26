package at.irian.ankor.viewmodel;

import at.irian.ankor.ref.Ref;

import java.lang.reflect.Field;

/**
 * This ViewModelPostProcessor scans a class for fields of type ViewModelProperty and initializes these fields.
 *
* @author Manfred Geiler
*/
@SuppressWarnings("UnusedDeclaration")
public class FieldsInitializerViewModelPostProcessor implements ViewModelPostProcessor {

    @Override
    public void postProcess(Object modelObject, Ref modelRef) {
        for (Field field : modelObject.getClass().getDeclaredFields()) {
            if (ViewModelProperty.class.isAssignableFrom(field.getType())) {
                assureAccessible(field);
                ViewModelProperty fieldValue = getFieldValue(modelObject, field);
                if (fieldValue == null) {
                    ViewModelProperty mp = new ViewModelProperty().withRef(modelRef.appendPath(field.getName()));
                    setFieldValue(modelObject, field, mp);
                } else {
                    if (fieldValue.getRef() == null) {
                        ViewModelProperty mp = fieldValue.withRef(modelRef.appendPath(field.getName()));
                        setFieldValue(modelObject, field, mp);
                    }
                }
            }
        }
    }

    private <T> void setFieldValue(Object modelObject, Field field, T value) {
        try {
            field.set(modelObject, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error setting value of model field " + field, e);
        }
    }

    private <T> T getFieldValue(Object modelObject, Field field) {
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

    private Field getFieldByName(Object modelObject, String fieldName) {
        for (Field field : modelObject.getClass().getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        throw new IllegalArgumentException("Model object of type " + modelObject.getClass().getName() + " has no field " + fieldName);
    }
}
