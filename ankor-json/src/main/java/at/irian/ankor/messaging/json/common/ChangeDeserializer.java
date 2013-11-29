package at.irian.ankor.messaging.json.common;

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
public class ChangeDeserializer extends StdDeserializer<Change> {

    public ChangeDeserializer() {
        super(Change.class);
    }

    @Override
    public Change deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode tree = mapper.readTree(jp);

        ChangeType type = mapper.treeToValue(tree.get("type"), ChangeType.class);
        switch (type) {
            case value:
                return Change.valueChange(getValue(mapper, tree));

            case insert:
                return Change.insertChange(getIdx(mapper, tree).intValue(), getValue(mapper, tree));

            case delete:
                return Change.deleteChange(getKey(mapper, tree));

            case replace:
                return Change.replaceChange(getIdx(mapper, tree).intValue(), getListValue(mapper, tree));

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
        Object result;
        JsonNode newValueTree = tree.get("value");
        if (newValueTree == null) {
            result = null;
        } else if (newValueTree.getNodeType() == JsonNodeType.OBJECT) {
            result = mapper.treeToValue(newValueTree, Map.class);
        } else if (newValueTree.getNodeType() == JsonNodeType.ARRAY) {
            result = mapper.treeToValue(newValueTree, List.class);
        } else {
            result = mapper.treeToValue(newValueTree, Object.class);
        }
        return result;
    }

    private List getListValue(ObjectMapper mapper, ObjectNode tree) throws JsonProcessingException {
        List result;
        JsonNode newValueTree = tree.get("value");
        if (newValueTree == null || newValueTree.getNodeType() == JsonNodeType.MISSING) {
            result = null;
        } else if (newValueTree.getNodeType() == JsonNodeType.ARRAY) {
            result = mapper.treeToValue(newValueTree, List.class);
        } else {
            throw new IllegalArgumentException("Array/List expected: " + tree);
        }
        return result;
    }
}
