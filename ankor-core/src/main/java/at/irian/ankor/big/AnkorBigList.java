package at.irian.ankor.big;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Manfred Geiler
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AnkorBigList {
    int thresholdSize() default -1;
    int initialSendSize() default 10;
    int aheadSendSize() default 0;
    Class<?> missingElementSubstitute() default Null.class;

    static class Null {}
}
