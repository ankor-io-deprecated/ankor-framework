package at.irian.ankorman.sample2.viewmodel.animal.helper;

/**
 * @author Thomas Spiegl
 */
public class Paginator {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Paginator.class);

    private int first;
    private int maxResults;
    private int count;

    public Paginator(int first, int maxResults) {
        this.first = first;
        this.maxResults = maxResults;
    }

    public int getFirst() {
        return first;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public int getCount() {
        return count;
    }

    public Paginator previous() {
        return new Paginator(first - maxResults, maxResults);
    }

    public Paginator next() {
        return new Paginator(first + maxResults, maxResults);
    }

    public void reset() {
        first = 0;
    }
}
