package at.irian.ankor.sample.fx.view.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Thomas Spiegl
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS
)
public class Tab<T> {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Tab.class);
    private final String id;
    private T model;

    @JsonCreator
    public Tab(@JsonProperty("id") String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }
}
