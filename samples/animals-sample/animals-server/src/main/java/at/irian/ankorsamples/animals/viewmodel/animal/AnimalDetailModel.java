package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelProperty;
import at.irian.ankorsamples.animals.domain.animal.Animal;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.TabNameCreator;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
public class AnimalDetailModel {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalDetailModel.class);

    @AnkorIgnore
    private final AnimalRepository animalRepository;
    @AnkorIgnore
    private final ViewModelProperty<String> tabName;
    @AnkorIgnore
    private final ViewModelProperty<String> serverStatus;
    @AnkorIgnore
    private boolean saved = false;

    private Animal animal;

    private AnimalSelectItems selectItems;

    private ViewModelProperty<Boolean> editable;

    private ViewModelProperty<String> nameStatus;

    public AnimalDetailModel(Ref myRef,
                             Animal animal,
                             AnimalRepository animalRepository,
                             ViewModelProperty<String> tabName,
                             ViewModelProperty<String> serverStatus) {
        AnkorPatterns.initViewModel(this, myRef);
        this.animal = animal;
        this.selectItems = AnimalSelectItems.create(animalRepository.getAnimalTypes());
        this.animalRepository = animalRepository;
        this.tabName = tabName;
        this.serverStatus = serverStatus;

        this.editable.set(true);
        this.nameStatus.set("ok");
    }

    @ChangeListener(pattern = ".animal.name")
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

    @ChangeListener(pattern = "(@).animal.type")
    public void animalTypeChanged(Ref modelRef) {
        Ref familyRef = modelRef.appendPath("animal.family");
        Ref familiesRef = modelRef.appendPath("selectItems.families");
        new AnimalTypeChangeHandler(animalRepository).handleChange(animal.getType(), familyRef, familiesRef);
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
