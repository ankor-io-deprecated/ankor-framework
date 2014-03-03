package at.irian.ankor.messaging.json.simpletree;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.messaging.*;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the message de/serialization with some 'reference' json messages.<p>
 * This test is a humble beginning of an interface test suite to ensure that the json protocol (now defined by the
 * referenceMsg_*.json) stays stable. Different clients should run tests against the same reference messages to ensure
 * compatibility.
 *
 * @author David Ferbas
 */
public class SimpleTreeJsonMessageMapperTest {
    private static final Logger log = LoggerFactory.getLogger(SimpleTreeJsonMessageMapperTest.class);

    private SimpleTreeJsonMessageMapper mapper = new SimpleTreeJsonMessageMapper();
    private MessageFactory messageFactory;
    private ModelSession modelSessionMock;

    @Before
    public void setup() {
        MessageIdGenerator idGenerator = mock(MessageIdGenerator.class);
        when(idGenerator.create()).thenReturn("testClient#1");
        messageFactory = new MessageFactory("testSystem", idGenerator);
        modelSessionMock = mock(ModelSession.class);
        when(modelSessionMock.getId()).thenReturn("1");
    }

    @Test
    public void testSimpleChangeMessage() throws Exception {

        String json = mapper.serialize(messageFactory.createChangeMessage(modelSessionMock, "changed.path", Change.valueChange(14)));

        assertJsonEquals("simpleChange", json);

        ChangeMessage msg = (ChangeMessage) mapper.deserialize(json, Message.class);
        assertThat(msg).isInstanceOf(ChangeMessage.class);
        assertThat(msg.getChange().getValue()).isEqualTo(14);
        assertThat(msg.getProperty()).isEqualTo("changed.path");

    }

    @Test
    public void testComplexChangeMessage() throws Exception {

        ChangeMessage msg = (ChangeMessage) mapper.deserialize(getReferenceMsg("complexChange"), Message.class);
        assertThat(msg).isNotNull();
        assertThat(msg.getChange().getValue()).isInstanceOf(Map.class);
    }

    @Test
    public void testSimpleActionMessage() throws Exception {

        String json = mapper.serialize(createActionMessage("init"));

        assertJsonEquals("simpleAction", json);

        ActionMessage msg = (ActionMessage) mapper.deserialize(json, Message.class);
        assertThat(msg).isInstanceOf(ActionMessage.class);
        assertThat(msg.getAction().getName()).isEqualTo("init");
        assertThat(msg.getProperty()).isEqualTo("root.next");
    }

    /** test a message with unusual chars to ensure encoding/decoding is correct */
    @Test @Ignore("Test does not work w.o.UTF-8 build/workspace encoding") // TODO switch all to UTF-8?!
    public void testBadStringActionMessage() throws Exception {

        String badString = "@$&§φόδί?Ν>Οͺ";
        String json = mapper.serialize(createActionMessage(badString));

        assertJsonEquals("badStringAction", json);

        ActionMessage msg = (ActionMessage) mapper.deserialize(json, Message.class);
        assertThat(msg.getAction().getName()).isEqualTo(badString);
    }


    /** Test a message with multiple param data types. Evolve to a separate data type mapping test maybe. */
    @Test
    public void testMultiParamsMessage() throws Exception {
        Map<String, Object> params = Maps.newLinkedHashMap();
        params.put("stringParam", "test");
        params.put("intParam", 1);
        // TODO the date handling has to be addressed sooner or later:
        // params.put("dateParam", new Date(0));
        // json: dateParam: "1970-01-01T00:00:00.000+0000",
        params.put("booleanParam", true);
        params.put("arrayParam", new int[]{1, 2, 3, 4});
        params.put("listParam", Lists.newArrayList(1, 2, 3, 4));
        params.put("doubleParam", 8.8d);

        String json = mapper.serialize(createActionMessage("initParams", params));

        assertJsonEquals("multiParamsAction", json);

        ActionMessage msg = (ActionMessage) mapper.deserialize(json, Message.class);
        assertThat(msg).isInstanceOf(ActionMessage.class);
        assertThat(msg.getMessageId()).isEqualTo("testClient#1");
        Map<String, Object> outParams = msg.getAction().getParams();
        assertThat(outParams.get("booleanParam")).isEqualTo(true);
        assertThat(outParams.get("stringParam")).isEqualTo("test");
        assertThat(outParams.get("doubleParam")).isEqualTo(8.8d);
        @SuppressWarnings("unchecked")
        List<Integer> listParam = (List<Integer>) outParams.get("listParam");
        assertThat(listParam).isInstanceOf(List.class);
        assertThat(listParam.size()).isEqualTo(4);
        assertThat(listParam.get(0)).isEqualTo(1);
    }

    /**
     * read the json reference message with the given key
     */
    private String getReferenceMsg(String key) throws IOException {
        String resourceName = "referenceMsg_" + key + ".json";
        URL resource = getClass().getResource(resourceName);
        if (resource == null) {
            throw new IllegalStateException("resource not found " + resourceName);
        }
        return Resources.toString(resource, Charsets.UTF_8);
    }

    private Message createActionMessage(String name) {
        return createActionMessage(name, null);
    }

    private Message createActionMessage(String name, Map<String, Object> params) {
        return messageFactory.createActionMessage(modelSessionMock, "root.next", new Action(name, params));
    }

    private void assertJsonEquals(String expectedRefMsg, String actualJson) throws JSONException,
        IOException {

        log.debug("result: " + actualJson);
        String expected = getReferenceMsg(expectedRefMsg);
        log.debug("expect: " + expected);

        JSONAssert.assertEquals(expected, actualJson, true);
    }

}
