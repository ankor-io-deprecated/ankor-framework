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
public @interface ActionListener {
    /**
     * Name of the action.
     */
    String name() default "";

    /**
     * One or more Ref patterns that must match.
     */
    String[] pattern() default "";
}
