package at.irian.ankor.model;

import at.irian.ankor.ref.Ref;

import java.lang.reflect.Field;

/**
 * @author Manfred Geiler
 */
public class ModelHelper {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHelper.class);

    public static void init(Object modelObject, Ref ref) {

        for (Field field : modelObject.getClass().getDeclaredFields()) {

            if (ModelProperty.class.isAssignableFrom(field.getType())) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                try {
                    Object currentValue = field.get(modelObject);
                    if (currentValue == null) {
                        ModelProperty<Object> newValue
                                = ModelProperty.createReferencedProperty(ref.append(field.getName()));
                        field.set(modelObject, newValue);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Could not access model property field " + field, e);
                }

            }
        }

    }


}
