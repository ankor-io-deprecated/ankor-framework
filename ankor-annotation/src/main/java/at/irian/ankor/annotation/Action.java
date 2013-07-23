package at.irian.ankor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Manfred Geiler
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
    /**
     * Name of the action.
     */
    String name() default "";

    /**
     * Required java type of the action property.
     */
    Class<?> refType() default Action.class;

}
