package at.irian.ankor.sample1.viewmodel.animal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Spiegl
 */
public class Data<T> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Data.class);

    private Paginator paginator;

    private List<T> rows;

    public Data(Paginator paginator) {
        this.paginator = paginator;
        this.rows = new ArrayList<T>(0);
    }

    public Paginator getPaginator() {
        return paginator;
    }

    public void setPaginator(Paginator paginator) {
        this.paginator = paginator;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
