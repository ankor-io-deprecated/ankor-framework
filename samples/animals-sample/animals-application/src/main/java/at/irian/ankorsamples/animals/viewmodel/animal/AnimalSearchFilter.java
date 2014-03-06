package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankorsamples.animals.domain.AnimalFamily;
import at.irian.ankorsamples.animals.domain.AnimalType;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class AnimalSearchFilter {

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String name;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private AnimalType type;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private AnimalFamily family;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnimalType getType() {
        return type;
    }

    public void setType(AnimalType type) {
        this.type = type;
    }

    public AnimalFamily getFamily() {
        return family;
    }

    public void setFamily(AnimalFamily family) {
        this.family = family;
    }

}
