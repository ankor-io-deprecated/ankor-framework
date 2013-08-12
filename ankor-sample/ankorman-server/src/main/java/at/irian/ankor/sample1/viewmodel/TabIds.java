package at.irian.ankor.sample1.viewmodel;

import java.util.concurrent.atomic.AtomicInteger;

/**
* @author Thomas Spiegl
*/
public class TabIds {
    private static AtomicInteger current = new AtomicInteger(0);
    public static String next() {
        return "A" + current.incrementAndGet();
    }
}