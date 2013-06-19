package at.irian.ankor.sample.fx;

import at.irian.ankor.sample.fx.model.Animal;
import at.irian.ankor.sample.fx.model.AnimalSearchFilter;
import at.irian.ankor.sample.fx.model.AnimalType;
import at.irian.ankor.sample.fx.view.AnimalSearchModel;
import at.irian.ankor.sample.fx.view.RootModel;
import at.irian.ankor.sample.fx.view.Tab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
* @author Thomas Spiegl
*/
@SuppressWarnings("UnusedDeclaration")
public class ServiceBean {

    public RootModel init() {
        RootModel model = new RootModel();
        model.setUserName("Toni Polster");
        return model;
    }

    public List<Animal> searchAnimals(AnimalSearchFilter filter) {
        List<Animal> animals = new ArrayList<Animal>();
        animals.add(new Animal("Trout", AnimalType.Fish));
        animals.add(new Animal("Salmon", AnimalType.Fish));
        animals.add(new Animal("Pike", AnimalType.Fish));
        animals.add(new Animal("Eagle", AnimalType.Bird));
        animals.add(new Animal("Whale", AnimalType.Mammal));
        String nameFilter = filter.getName() != null && filter.getName().trim().length() > 0 ? filter.getName().toLowerCase() : null;
        if (filter.getType() != null || nameFilter != null) {
            for (Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
                Animal current = it.next();
                if (nameFilter != null && !current.getName().toLowerCase().contains(nameFilter)) {
                    it.remove();
                    continue;
                }
                if (filter.getType() != null && current.getType() != filter.getType()) {
                    it.remove();
                }
            }
        }
        return animals;
    }

    public Tab createAnimalSearchTab(String tabId) {
        Tab<AnimalSearchModel> tab = new Tab<AnimalSearchModel>(tabId);
        tab.setModel(new AnimalSearchModel());
        tab.getModel().getFilter().setName("Eagle");
        tab.getModel().getFilter().setType(AnimalType.Bird);
        return tab;
    }
}
