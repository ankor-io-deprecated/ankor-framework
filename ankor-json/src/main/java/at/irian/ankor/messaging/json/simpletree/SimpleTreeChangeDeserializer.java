package at.irian.ankor.messaging.json.simpletree;

import at.irian.ankor.change.Change;
import com.fasterxml.jackson.core.JsonParser;
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

        Object newValue;
        JsonNode newValueTree = tree.get("newValue");
        if (newValueTree.getNodeType() == JsonNodeType.OBJECT) {
            newValue = mapper.treeToValue(newValueTree, Map.class);
        } else if (newValueTree.getNodeType() == JsonNodeType.ARRAY) {
            newValue = mapper.treeToValue(newValueTree, List.class);
        } else {
            newValue = mapper.treeToValue(newValueTree, Object.class);
        }

        return new Change(newValue);
    }
}
