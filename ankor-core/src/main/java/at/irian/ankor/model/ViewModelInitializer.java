package at.irian.ankor.model;

import at.irian.ankor.ref.Ref;

import java.lang.reflect.Field;

/**
* @author Manfred Geiler
*/
public class ViewModelInitializer {

    private final Object modelObject;
    private final Ref modelRef;

    public ViewModelInitializer(Object modelObject, Ref modelRef) {
        this.modelObject = modelObject;
        this.modelRef = modelRef;
    }

    public static ViewModelInitializer initializerFor(Object viewModelObject, Ref viewModelRef) {
        return new ViewModelInitializer(viewModelObject, viewModelRef);
    }

    public ViewModelInitializer initAll() {
        for (Field field : modelObject.getClass().getDeclaredFields()) {
            if (ViewModelProperty.class.isAssignableFrom(field.getType())) {
                assureAccessible(field);
                ViewModelProperty currentValue = getValue(field);
                if (currentValue == null) {
                    ViewModelProperty mp = new ViewModelProperty(modelRef, field.getName());
                    setValue(field, mp);
                } else {
                    if (currentValue.getRef() == null) {
                        currentValue.setRef(modelRef.append(field.getName()));
                    }
                }
            }
        }
        return this;
    }

    public <T> ViewModelInitializer withInitialValue(String fieldName, T initialValue) {
        Field field = getFieldByName(fieldName);
        assureAccessible(field);
        ViewModelProperty<T> mp = getValue(field);
        if (mp != null) {
            mp.putWrappedValue(initialValue);
        } else {
            mp = new ViewModelProperty<T>(modelRef, fieldName, initialValue);
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
