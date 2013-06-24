package at.irian.ankorman.sample1.model.animal;

/**
 * @author Thomas Spiegl
 */
public class Paginator {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Paginator.class);

    private int first;
    private int maxResults;
    private int count;

    protected Paginator() {
    }

    public Paginator(int first, int maxResults) {
        this.first = first;
        this.maxResults = maxResults;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void previous() {
        first = first - maxResults;
    }

    public void next() {
        first = first + maxResults;
    }

    public void reset() {
        first = 0;
    }
}
