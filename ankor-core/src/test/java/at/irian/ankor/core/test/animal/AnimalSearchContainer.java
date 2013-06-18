package at.irian.ankor.core.test.animal;

import java.util.Collections;
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
        this.resultList = Collections.emptyList();
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

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        AnimalSearchContainer that = (AnimalSearchContainer) o;

        if (!filter.equals(that.filter)) { return false; }
        if (!resultList.equals(that.resultList)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = filter.hashCode();
        result = 31 * result + resultList.hashCode();
        return result;
    }
}
