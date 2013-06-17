package at.irian.ankor.core.test.animal;

import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AnimalSearchContainer {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnimalSearchContainer.class);

    private final AnimalFilter filter;
    private List<Animal> resultList;

    public AnimalSearchContainer() {
        this.filter = new AnimalFilter();
        this.resultList = null;
    }

    public AnimalFilter getFilter() {
        return filter;
    }

    public List<Animal> getResultList() {
        return resultList;
    }

    public void setResultList(List<Animal> resultList) {
        this.resultList = resultList;
    }
}
