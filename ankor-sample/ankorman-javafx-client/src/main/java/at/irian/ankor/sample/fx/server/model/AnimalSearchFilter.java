package at.irian.ankor.sample.fx.server.model;

import java.util.List;

/**
* @author Thomas Spiegl
*/
public class AnimalSearchFilter {

    private String name;
    private AnimalType type;
    private AnimalFamily family;

    private List<AnimalType> types;
    private List<AnimalFamily> families;

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

    public List<AnimalType> getTypes() {
        return types;
    }

    public AnimalFamily getFamily() {
        return family;
    }

    public void setFamily(AnimalFamily family) {
        this.family = family;
    }

    public void setTypes(List<AnimalType> types) {
        this.types = types;
    }

    public List<AnimalFamily> getFamilies() {
        return families;
    }

    public void setFamilies(List<AnimalFamily> families) {
        this.families = families;
    }
}
