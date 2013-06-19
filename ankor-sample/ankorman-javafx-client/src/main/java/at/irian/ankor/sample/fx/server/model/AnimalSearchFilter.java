package at.irian.ankor.sample.fx.server.model;

/**
* @author Thomas Spiegl
*/
public class AnimalSearchFilter {

    private String name;
    private AnimalType type;

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
}
