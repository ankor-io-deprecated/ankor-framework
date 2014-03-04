package at.irian.ankor.connection;

/**
* @author Manfred Geiler
*/
@Deprecated
public class SimpleRemoteSystem implements RemoteSystem {

    private final String id;

    public SimpleRemoteSystem(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SimpleRemoteSystem that = (SimpleRemoteSystem) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
