package at.irian.ankorsamples.animals.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class Animal {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Animal.class);

    private final String uuid;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String name;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private AnimalType type;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private AnimalFamily family;

    public Animal() {
        this.uuid = UUID.randomUUID().toString();
        this.name = "";
    }

    public Animal(Animal other) {
        this.uuid = other.uuid;
        this.name = other.name;
        this.type = other.type;
        this.family = other.family;
    }

    public Animal(String name, AnimalType type, AnimalFamily family) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.family = family;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public AnimalType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Animal{"
               + "name='" + name + '\''
               + ", type=" + type
               + ", family=" + family
               + '}';
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Animal animal = (Animal) o;

        if (!uuid.equals(animal.uuid)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
