package at.irian.ankor.big;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Manfred Geiler
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AnkorBigList {
    int thresholdSize() default -1;     // todo: rename to "threshold"
    int initialSendSize() default 10;   // todo: rename to "initialSize"
    int aheadSendSize() default 0;      // todo: rename to "blockSize"
    Class<?> missingElementSubstitute() default Null.class;

    static class Null {}
}
