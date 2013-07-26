package at.irian.ankorman.sample1.viewmodel.animal;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.model.ViewModelBase;
import at.irian.ankor.model.ViewModelProperty;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.domain.animal.Animal;
import at.irian.ankorman.sample1.server.AnimalRepository;
import at.irian.ankorman.sample1.viewmodel.TabNameCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
public class AnimalDetailModel extends ViewModelBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalDetailModel.class);

    @JsonIgnore
    private final AnimalRepository animalRepository;
    @JsonIgnore
    private final ViewModelProperty<String> tabName;
    @JsonIgnore
    private final ViewModelProperty<String> serverStatus;
    @JsonIgnore
    private boolean saved = false;

    private Animal animal;

    private AnimalSelectItems selectItems;

    private ViewModelProperty<Boolean> editable;

    private ViewModelProperty<String> nameStatus;

    public AnimalDetailModel(Ref myRef,
                             Animal animal, AnimalSelectItems selectItems, AnimalRepository animalRepository,
                             ViewModelProperty<String> tabName, ViewModelProperty<String> serverStatus) {
        super(myRef);
        this.animal = animal;
        this.selectItems = selectItems;
        this.animalRepository = animalRepository;
        this.tabName = tabName;
        this.serverStatus = serverStatus;

        this.editable.set(true);
        this.nameStatus.set("ok");
    }

    @ChangeListener(pattern = "**.<AnimalDetailModel>.animal.name")
    public void onNameChanged() {
        String name = animal.getName();

        tabName.set(new TabNameCreator().createName("New Animal", name));

        if (animalRepository.isAnimalNameAlreadyExists(name)) {
            nameStatus.set("name already exists");
        } else if (name.length() > AnimalRepository.MAX_NAME_LEN) {
            nameStatus.set("name is too long");
        } else {
            nameStatus.set("ok");
        }
    }

    @ChangeListener(pattern = "**.<AnimalDetailModel>.animal.type")
    public void animalTypeChanged() {
        Ref familyRef = thisRef().append("animal.family");
        Ref familiesRef = thisRef().append("selectItems.families");
        new AnimalTypeChangeHandler().handleChange(familyRef, familiesRef, animal.getType());
    }

    @ActionListener
    public void save() {

        LOG.info("save action");

        String status;
        if (saved) {
            status = "Error: Animal already saved";
        } else {
            try {
                animalRepository.saveAnimal(animal);
                saved = true;
                editable.set(false);
                status = "Animal successfully saved";
            } catch (Exception e) {
                status = "Error: " + e.getMessage();
                if (!(e instanceof IllegalArgumentException || e instanceof IllegalStateException)) {
                    LOG.error("Error saving animal " + animal.getUuid(), e);
                }
            }
        }
        serverStatus.set(status);
    }

    public AnimalSelectItems getSelectItems() {
        return selectItems;
    }

    public Animal getAnimal() {
        return animal;
    }

    public ViewModelProperty<Boolean> getEditable() {
        return editable;
    }

    public ViewModelProperty<String> getNameStatus() {
        return nameStatus;
    }
}
