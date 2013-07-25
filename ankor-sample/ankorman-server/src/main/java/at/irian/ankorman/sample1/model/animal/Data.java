package at.irian.ankorman.sample1.model.animal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Spiegl
 */
public class Data<T> {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Data.class);

    private Paginator paginator;

    //@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@javaType")
    private List<T> rows;

    protected Data() {
    }

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
