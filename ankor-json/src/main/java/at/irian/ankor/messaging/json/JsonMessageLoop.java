package at.irian.ankor.messaging.json;

import at.irian.ankor.messaging.MessageLoop;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class JsonMessageLoop extends MessageLoop<String> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JsonMessageLoop.class);

    public JsonMessageLoop(String name) {
        super(name, new JsonMessageMapper());
    }

}
