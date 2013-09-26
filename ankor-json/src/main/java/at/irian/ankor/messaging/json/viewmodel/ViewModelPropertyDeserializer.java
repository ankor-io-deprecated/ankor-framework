package at.irian.ankor.messaging.json.viewmodel;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

/**
* @author Manfred Geiler
*/
class ViewModelPropertyDeserializer extends StdDeserializer<ViewModelProperty> {

    ViewModelPropertyDeserializer() {
        super(Ref.class);
    }

    @Override
    public ViewModelProperty deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        TreeNode tree = mapper.readTree(jp);
        Object value = mapper.treeToValue(tree, Object.class);
        //noinspection unchecked
        return new ViewModelProperty().withInitialValue(value);
    }
}
