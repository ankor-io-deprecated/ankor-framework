package at.irian.ankor.messaging.json.simpletree;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
class SimpleTreeChangeDeserializer extends StdDeserializer<Change> {

    SimpleTreeChangeDeserializer() {
        super(Change.class);
    }

    @Override
    public Change deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode tree = mapper.readTree(jp);

        ChangeType type = mapper.treeToValue(tree.get("type"), ChangeType.class);
        switch (type) {
            case new_value:
                return Change.valueChange(getValue(mapper, tree));

            case insert:
                return Change.insertChange(getIdx(mapper, tree).intValue(), getValue(mapper, tree));

            case delete:
                return Change.deleteChange(getKey(mapper, tree));

            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    private Object getKey(ObjectMapper mapper, ObjectNode tree) throws JsonProcessingException {
        return mapper.treeToValue(tree.get("key"), Object.class);
    }

    private Number getIdx(ObjectMapper mapper, ObjectNode tree) throws JsonProcessingException {
        return mapper.treeToValue(tree.get("key"), Number.class);
    }

    private Object getValue(ObjectMapper mapper, ObjectNode tree) throws JsonProcessingException {
        Object newValue;
        JsonNode newValueTree = tree.get("value");
        if (newValueTree == null) {
            newValue = null;
        } else if (newValueTree.getNodeType() == JsonNodeType.OBJECT) {
            newValue = mapper.treeToValue(newValueTree, Map.class);
        } else if (newValueTree.getNodeType() == JsonNodeType.ARRAY) {
            newValue = mapper.treeToValue(newValueTree, List.class);
        } else {
            newValue = mapper.treeToValue(newValueTree, Object.class);
        }
        return newValue;
    }
}
