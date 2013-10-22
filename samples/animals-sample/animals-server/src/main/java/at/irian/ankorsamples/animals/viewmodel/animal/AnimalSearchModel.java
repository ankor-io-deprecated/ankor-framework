package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.big.AnkorBigList;
import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.TypedRef;
import at.irian.ankor.viewmodel.diff.ListDiff;
import at.irian.ankorsamples.animals.domain.animal.Animal;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.viewmodel.PanelNameCreator;

import java.util.ArrayList;
import java.util.List;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class AnimalSearchModel {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchModel.class);

    private final Ref myRef;
    private final TypedRef<String> panelNameRef;
    private final TypedRef<String> serverStatusRef;

    @AnkorIgnore
    private final AnimalRepository animalRepository;
    @AnkorIgnore
    private final FloodControl reloadFloodControl;

    private AnimalSearchFilter filter;

    private AnimalSelectItems selectItems;

    @AnkorBigList( missingElementSubstitute = EmptyAnimal.class,
                   threshold = 1000,
                   initialSize = 10,
                   chunkSize = 10)
    private List<Animal> animals;

    public AnimalSearchModel(Ref animalSearchModelRef,
                             TypedRef<String> panelNameRef,
                             TypedRef<String> serverStatusRef,
                             AnimalRepository animalRepository) {
        this.myRef = animalSearchModelRef;
        this.panelNameRef = panelNameRef;
        this.serverStatusRef = serverStatusRef;
        this.animalRepository = animalRepository;
        this.filter = new AnimalSearchFilter();
        this.selectItems = AnimalSelectItems.create(animalRepository.getAnimalTypes());
        this.animals = new ArrayList<>();
        this.reloadFloodControl = new FloodControl(animalSearchModelRef, 500L);
        AnkorPatterns.initViewModel(this, animalSearchModelRef);
    }

    public AnimalSearchFilter getFilter() {
        return filter;
    }

    public AnimalSelectItems getSelectItems() {
        return selectItems;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<Animal> animals) {
        this.animals = animals;
    }

    @ChangeListener(pattern = {".filter.**"})
    public void reloadAnimals() {
        reloadFloodControl.control(new Runnable() {
            @Override
            public void run() {

                // remember old values for later diff
                List<Animal> oldAnimalsList = animals;

                // get new list from database
                LOG.info("RELOADING animals ...");
                List<Animal> newAnimalsList = animalRepository.searchAnimals(filter, 0, Integer.MAX_VALUE);
                LOG.info("... finished RELOADING");

                // compare old and new list and apply smart changes
                Ref listRef = myRef.appendPath("animals");
                new ListDiff<>(oldAnimalsList, newAnimalsList)
                        .withThreshold(5) // send whole list if 5 changes or more
                        .applyChangesTo(listRef);

                // reset server status display
                serverStatusRef.setValue("");
            }
        });
    }

    @ChangeListener(pattern = ".filter.name")
    public void onNameChanged() {
        String name = filter.getName();
        panelNameRef.setValue(new PanelNameCreator().createName("Animal Search", name));
    }

    @ChangeListener(pattern = ".filter.type")
    public void animalTypeChanged() {
        Ref familyRef = myRef.appendPath("filter.family");
        Ref familiesRef = myRef.appendPath("selectItems.families");
        new AnimalTypeChangeHandler(animalRepository).handleChange(filter.getType(), familyRef, familiesRef);
    }

    @ActionListener(name = "save")
    public void save() {
        String status;
        try {
            for (Animal animal : getAnimals()) {
                animalRepository.saveAnimal(animal);
            }
            status = "Animals successfully saved";
        } catch (Exception e) {
            status = "Error: " + e.getMessage();
            if (!(e instanceof IllegalArgumentException || e instanceof IllegalStateException)) {
                LOG.error("Error saving animals ", e);
            }
        }
        serverStatusRef.setValue(status);
    }

    public static class EmptyAnimal extends Animal {
        public EmptyAnimal() {
            super("...", null, null);
        }
    }
}
