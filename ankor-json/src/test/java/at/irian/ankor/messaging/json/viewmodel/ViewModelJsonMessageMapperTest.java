package at.irian.ankor.messaging.json.viewmodel;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.CloseAction;
import at.irian.ankor.change.Change;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.messaging.*;
import at.irian.ankor.viewmodel.metadata.EmptyBeanMetadataProvider;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Manfred Geiler
 */
public class ViewModelJsonMessageMapperTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelJsonMessageMapperTest.class);

    private ModelSession modelSession;
    private MessageFactory messageFactory;
    private ViewModelJsonMessageMapper msgMapper;

    @Before
    public void setUp() throws Exception {
        modelSession = mock(ModelSession.class);
        when(modelSession.getId()).thenReturn("mc1");

        messageFactory = new MessageFactory("test", new CounterMessageIdGenerator(""));
        msgMapper = new ViewModelJsonMessageMapper(new EmptyBeanMetadataProvider());
    }

    @Test
    public void testSimpleAction() throws Exception {
        Action action = new Action("test");
        Message msg = messageFactory.createActionMessage(modelSession, "session.model", action);

        Message desMsg = serializeAndDeserialize(msg);

        assertEquals(ActionMessage.class, desMsg.getClass());
        ActionMessage actionMsg = (ActionMessage) desMsg;
        assertEquals(Action.class, actionMsg.getAction().getClass());
        assertEquals("test", actionMsg.getAction().getName());
    }

    @Test
    public void testActionWithParams() throws Exception {
        Map<String, Object> params = Maps.newHashMap();
        params.put("key", "value");
        Action action = new Action("test", params);
        Message msg = messageFactory.createActionMessage(modelSession, "session.model", action);

        Message desMsg = serializeAndDeserialize(msg);

        assertEquals(ActionMessage.class, desMsg.getClass());
        ActionMessage actionMsg = (ActionMessage) desMsg;
        assertEquals(Action.class, actionMsg.getAction().getClass());

        assertEquals("test", actionMsg.getAction().getName());
        assertEquals("value", actionMsg.getAction().getParams().get("key"));
    }

    @Test
    public void testCloseAction() throws Exception {
        CloseAction action = new CloseAction();

        Message msg = messageFactory.createActionMessage(modelSession, "session.model", action);
        Message desMsg = serializeAndDeserialize(msg);

        assertEquals(ActionMessage.class, desMsg.getClass());
        ActionMessage actionMsg = (ActionMessage) desMsg;
        assertEquals(CloseAction.class, actionMsg.getAction().getClass());
        CloseAction closeAction = (CloseAction) actionMsg.getAction();
        assertEquals(CloseAction.CLOSE_ACTION_NAME, closeAction.getName());
    }

    private Message serializeAndDeserialize(Message msg) {
        String json = msgMapper.serialize(msg);
        LOG.info("JSON: {}", json);

        Message desMsg = msgMapper.deserialize(json);
        LOG.info("Message: {}", desMsg);
        return desMsg;
    }

    @Test
    public void testValueChange() throws Exception {
        Message msg = messageFactory.createChangeMessage(modelSession, "root.test1", Change.valueChange("new-value"));
        Message desMsg = serializeAndDeserialize(msg);

        assertEquals(ChangeMessage.class, desMsg.getClass());
        ChangeMessage changeMsg = (ChangeMessage) desMsg;
        assertEquals("root.test1", changeMsg.getProperty());
    }

    @Test
    public void testValueChangeRoot() throws Exception {
        Message msg = messageFactory.createChangeMessage(modelSession, "root", Change.valueChange("new-value"));
        Message desMsg = serializeAndDeserialize(msg);

        assertEquals(ChangeMessage.class, desMsg.getClass());
        ChangeMessage changeMsg = (ChangeMessage) desMsg;
        assertEquals("root", changeMsg.getProperty());
    }

}