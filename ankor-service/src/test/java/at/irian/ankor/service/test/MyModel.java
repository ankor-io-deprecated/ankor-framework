package at.irian.ankor.service.test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class MyModel {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MyModel.class);

    private Map<String, Object> containers = new HashMap<String, Object>();

    public Map<String, Object> getContainers() {
        return containers;
    }

}