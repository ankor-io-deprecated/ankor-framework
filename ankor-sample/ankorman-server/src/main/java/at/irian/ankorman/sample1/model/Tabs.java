package at.irian.ankorman.sample1.model;

import at.irian.ankor.annotation.ActionListener;
import at.irian.ankor.annotation.Param;
import at.irian.ankor.ref.Ref;
import at.irian.ankorman.sample1.model.animal.*;
import at.irian.ankorman.sample1.server.AnimalRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Thomas Spiegl
 */
public class Tabs extends MapViewModelBase<String, Tab> {

    private AnimalRepository animalRepository;

    protected Tabs() {
        super(null, new HashMap<String, Tab>());
    }

    protected Tabs(Ref viewModelRef, AnimalRepository animalRepository) {
        super(viewModelRef, new HashMap<String, Tab>());
        this.animalRepository = animalRepository;
    }

    @Override
    public Tab put(String key, Tab value) {
        if (value == null) {
            return map.remove(key);
        } else {
            return map.put(key, value);
        }
    }

    @ActionListener
    public void createAnimalSearchTab(@Param("tabId") final String tabId) {
        Ref tabRef = thisRef().append(tabId);

        Tab<AnimalSearchModel> tab = new Tab<AnimalSearchModel>(tabId, tabRef, "Animal Search");
        AnimalSearchModel model = new AnimalSearchModel(tabRef.append("model"), animalRepository, getAnimalSelectItems(), tab.getName());

        tab.setModel(model);

        tabRef.setValue(tab);

    }

    @ActionListener
    public void createAnimalDetailTab(@Param("tabId") final String tabId) {

        Ref tabRef = thisRef().append(tabId);
        ModelRoot root = thisRef().root().getValue();

        Tab<AnimalDetailModel> tab = new Tab<AnimalDetailModel>(tabId, tabRef, "New Animal");

        AnimalDetailModel model = new AnimalDetailModel(animalRepository,
                new Animal(),
                getAnimalSelectItems(),
                tabRef.append("model"),
                tab.getName(),
                root.getServerStatus());

        tab.setModel(model);

        tabRef.setValue(tab);
    }

    private AnimalSelectItems getAnimalSelectItems() {
        List<AnimalType> types = new ArrayList<AnimalType>(AnimalType.values().length + 1);
        types.addAll(Arrays.asList(AnimalType.values()));
        return new AnimalSelectItems(types, new ArrayList<AnimalFamily>());
    }
}
