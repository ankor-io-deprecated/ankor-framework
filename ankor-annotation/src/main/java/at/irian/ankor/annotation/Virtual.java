package at.irian.ankor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A view model property that is annotated with {@link Virtual} defines a "virtual model property".
 * Virtual properties may have a Ref like other properties. However, setting a new value to this Ref does
 * not change the underlying model property. In other words: a setter for a virtual model property is not getting
 * invoked and the property field is never changed when the associated Ref is assigned a new value.
 *
 * Typical usage: a model property, of which the value shall be propagated to the client but where now actual value
 * is stored on the server. Applying a new value to the Ref of such a virtual property fires a ChangeEvent, that is
 * broadcasted to all connected (synchronized) model instances (on clients), but no memory is consumed within
 * the server-side model instance.
 *
 * @author Manfred Geiler
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Virtual {
}
