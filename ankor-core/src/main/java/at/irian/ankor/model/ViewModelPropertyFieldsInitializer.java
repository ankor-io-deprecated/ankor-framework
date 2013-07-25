package at.irian.ankor.model;

import at.irian.ankor.ref.Ref;

import java.lang.reflect.Field;

/**
* @author Manfred Geiler
*/
public class ViewModelPropertyFieldsInitializer implements ViewModelPostProcessor {

    @Override
    public void postProcess(ViewModelBase modelObject, Ref modelRef) {
        for (Field field : modelObject.getClass().getDeclaredFields()) {
            if (ViewModelProperty.class.isAssignableFrom(field.getType())) {
                assureAccessible(field);
                ViewModelProperty currentValue = getValue(modelObject, field);
                if (currentValue == null) {
                    ViewModelProperty mp = new ViewModelProperty(modelRef, field.getName());
                    setValue(modelObject, field, mp);
                } else {
                    if (currentValue.getRef() == null) {
                        currentValue.setRef(modelRef.append(field.getName()));
                    }
                }
            }
        }
    }

    private <T> void setValue(Object modelObject, Field field, T value) {
        try {
            field.set(modelObject, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error setting value of model field " + field, e);
        }
    }

    private <T> T getValue(Object modelObject, Field field) {
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
