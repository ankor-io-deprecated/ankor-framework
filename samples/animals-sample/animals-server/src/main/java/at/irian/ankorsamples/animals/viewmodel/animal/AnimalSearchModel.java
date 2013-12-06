package at.irian.ankorsamples.animals.viewmodel.animal;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.AnkorWatched;
import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.big.AnkorBigList;
import at.irian.ankor.delay.FloodControl;
import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.proxy.ProxySupport;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.TypedRef;
import at.irian.ankor.viewmodel.ViewModelBase;
import at.irian.ankor.viewmodel.watch.ExtendedList;
import at.irian.ankor.viewmodel.watch.ExtendedListWrapper;
import at.irian.ankorsamples.animals.domain.animal.Animal;
import at.irian.ankorsamples.animals.domain.animal.AnimalFamily;
import at.irian.ankorsamples.animals.domain.animal.AnimalRepository;
import at.irian.ankorsamples.animals.domain.animal.AnimalType;
import at.irian.ankorsamples.animals.viewmodel.PanelNameCreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class AnimalSearchModel extends ViewModelBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchModel.class);

    private final TypedRef<String> panelNameRef;
    private final TypedRef<String> serverStatusRef;
    private final Ref resourcesRef;

    @AnkorIgnore
    private final AnimalRepository animalRepository;
    @AnkorIgnore
    private final FloodControl reloadFloodControl;

    private AnimalSearchFilter filter;

    private AnimalSelectItems selectItems;

    @AnkorBigList(missingElementSubstitute = EmptyAnimal.class,
                  threshold = 500,
                  initialSize = 10,
                  chunkSize = 10)
    @AnkorWatched(diffThreshold = 20)
    private ExtendedList<Animal> animals;

    public AnimalSearchModel(Ref animalSearchModelRef,
                             TypedRef<String> panelNameRef,
                             TypedRef<String> serverStatusRef,
                             Ref resourcesRef,
                             AnimalRepository animalRepository) {
        super(animalSearchModelRef);
        this.panelNameRef = panelNameRef;
        this.serverStatusRef = serverStatusRef;
        this.resourcesRef = resourcesRef;
        this.animalRepository = animalRepository;
        this.filter = ProxySupport.createProxyBean(animalSearchModelRef.appendPath("filter"), AnimalSearchFilter.class, null, null);
        this.selectItems = AnimalSelectItems.create(animalSearchModelRef.appendPath("selectItems"), animalRepository.getAnimalTypes());
        this.animals = new ExtendedListWrapper<>(new ArrayList<Animal>());
        this.reloadFloodControl = new FloodControl(animalSearchModelRef, 500L);
        AnkorPatterns.initViewModel(this);
    }

    public AnimalSearchFilter getFilter() {
        return filter;
    }

    public AnimalSelectItems getSelectItems() {
        return selectItems;
    }

    public ExtendedList<Animal> getAnimals() {
        return animals;
    }

    @ChangeListener(pattern = {".filter.**"})
    public void reloadAnimals() {
        reloadFloodControl.control(new Runnable() {
            @Override
            public void run() {

                reloadAnimalsImmediately();

                // reset server status display
                serverStatusRef.setValue("");
            }
        });
    }

    private void reloadAnimalsImmediately() {
        // get new list from database
        LOG.info("RELOADING animals ...");
        List<Animal> newAnimalsList = animalRepository.searchAnimals(filter, 0, Integer.MAX_VALUE);
        LOG.info("... finished RELOADING");

        animals.setAll(newAnimalsList);
    }

    @ChangeListener(pattern = {".filter.name", "root.locale"})
    public void onNameOrLocaleChanged() {
        panelNameRef.setValue(getPanelName());
    }

    public String getPanelName() {
        String name = filter.getName();
        return new PanelNameCreator().createName(resourcesRef.appendLiteralKey("SearchForAnimal").<String>getValue(), name);
    }

    @ChangeListener(pattern = ".filter.type")
    public void animalTypeChanged() {

        AnimalType type = filter.getType();
        List<AnimalFamily> families;
        if (type != null) {
            families = animalRepository.getAnimalFamilies(type);
        } else {
            families = Collections.emptyList();
        }
        selectItems.setFamilies(AnimalSelectItems.createSelectItemsFrom(families));

        //noinspection SuspiciousMethodCalls
        if (!families.contains(filter.getFamily())) {
            filter.setFamily(null);
        }
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

    @ActionListener(name = "delete")
    public void delete(@Param("uuid") String uuid) {
        animalRepository.deleteAnimal(uuid);
        reloadAnimalsImmediately();
        serverStatusRef.setValue("Animal deleted");
    }

    public static class EmptyAnimal extends Animal {
        public EmptyAnimal() {
            super("...", null, null);
        }
    }
}
