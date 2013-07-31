package at.irian.ankor.messaging.json.viewmodel;

import at.irian.ankor.viewmodel.ViewModelProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
* @author Manfred Geiler
*/
class ViewModelPropertySerializer extends StdSerializer<ViewModelProperty> {

    ViewModelPropertySerializer() {
        super(ViewModelProperty.class);
    }

    @Override
    public void serialize(ViewModelProperty viewModelProperty, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        Object value = viewModelProperty.get();
        if (value != null) {
            jgen.writeObject(value);
        } else {
            jgen.writeNull();
        }
    }
}
