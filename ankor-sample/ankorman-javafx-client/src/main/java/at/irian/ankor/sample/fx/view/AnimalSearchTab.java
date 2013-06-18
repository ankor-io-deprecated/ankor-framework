package at.irian.ankor.sample.fx.view;

import at.irian.ankor.sample.fx.model.AnimalSearchFilter;

/**
* @author Thomas Spiegl
*/
public class AnimalSearchTab {

    private AnimalSearchFilter filter;

    public AnimalSearchTab() {
        this.filter = new AnimalSearchFilter();
    }

    public AnimalSearchFilter getFilter() {
        return filter;
    }
}
