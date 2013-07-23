package at.irian.ankor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Thomas Spiegl
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TreeChangeListener {
    /**
     * name of the param.
     */
    Class value() default TreeChangeListener.class;

}
