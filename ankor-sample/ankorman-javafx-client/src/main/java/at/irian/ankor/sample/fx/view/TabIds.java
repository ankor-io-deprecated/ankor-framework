package at.irian.ankor.sample.fx.view;

import java.util.concurrent.atomic.AtomicInteger;

/**
* @author Thomas Spiegl
*/
class TabIds {
    private static AtomicInteger current = new AtomicInteger(0);
    static String next() {
        return "A" + current.incrementAndGet();
    }
}
