package at.irian.ankorman.model;

import java.util.Arrays;
import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class Menu {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Menu.class);

    private static final String[] ITEMS = {"Test1", "Test2"};

    public List<String> getMenuItems() {
        return Arrays.asList(ITEMS);
    }

}
