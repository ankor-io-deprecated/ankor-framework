package at.irian.ankor.messaging.json.viewmodel;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.dispatch.EventDispatcher;
import at.irian.ankor.messaging.*;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Manfred Geiler
 */
public class ViewModelJsonMessageMapperTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelJsonMessageMapperTest.class);

    private ModelContext modelContext;
    private MessageFactory messageFactory;
    private ViewModelJsonMessageMapper msgMapper;

    @org.junit.Before
    public void setUp() throws Exception {
        modelContext = new ModelContext() {
            @Override
            public String getId() {
                return "mc1";
            }

            @Override
            public EventListeners getEventListeners() {
                return null;
            }

            @Override
            public Object getModelRoot() {
                return null;
            }

            @Override
            public void setModelRoot(Object modelRoot) {
            }

            @Override
            public EventDispatcher getEventDispatcher() {
                return null;
            }

            @Override
            public void close() {
            }
        };
        messageFactory = new MessageFactory("test", new CounterMessageIdGenerator(""));
        msgMapper = new ViewModelJsonMessageMapper();
    }

    @Test
    public void testSimpleAction() throws Exception {
        Action action = new Action("test");
        Message msg = messageFactory.createActionMessage(modelContext, "context.model", action);
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ActionMessage.class, desMsg.getClass());
        ActionMessage actionMsg = (ActionMessage) desMsg;
        Assert.assertEquals(Action.class, actionMsg.getAction().getClass());
        Action simpleAction = actionMsg.getAction();
        Assert.assertEquals("test", simpleAction.getName());
    }

    @Test
    public void testChange() throws Exception {
        Message msg = messageFactory.createChangeMessage(modelContext, "root.test1", new Change("new-value"));
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ChangeMessage.class, desMsg.getClass());
        ChangeMessage changeMsg = (ChangeMessage) desMsg;
        Assert.assertEquals("root.test1", changeMsg.getProperty());
    }

    @Test
    public void testChangeRoot() throws Exception {
        Message msg = messageFactory.createChangeMessage(modelContext, "root", new Change("new-value"));
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);

        Assert.assertEquals(ChangeMessage.class, desMsg.getClass());
        ChangeMessage changeMsg = (ChangeMessage) desMsg;
        Assert.assertEquals("root", changeMsg.getProperty());
    }


}
