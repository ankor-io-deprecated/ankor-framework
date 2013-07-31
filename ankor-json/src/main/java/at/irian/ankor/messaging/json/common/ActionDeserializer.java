package at.irian.ankor.messaging.json.common;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.CloseAction;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class ActionDeserializer extends StdDeserializer<Action> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionDeserializer.class);

    public ActionDeserializer() {
        super(Action.class);
    }

    @Override
    public Action deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        TreeNode tree = mapper.readTree(jp);
        if (tree.isValueNode()) {
            String actionName = mapper.treeToValue(tree, String.class);
            return createAction(actionName);
        } else {
            String actionName = ((ObjectNode)tree).get("name").asText();
            JsonNode paramsNode = ((ObjectNode)tree).get("params");
            if (paramsNode != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> params = mapper.treeToValue(paramsNode, Map.class);
                return new Action(actionName, params);
            } else {
                return createAction(actionName);
            }
        }
    }

    private Action createAction(String actionName) {
        if (CloseAction.CLOSE_ACTION_NAME.equals(actionName)) {
            return new CloseAction();
        } else {
            return new Action(actionName);
        }
    }

}
