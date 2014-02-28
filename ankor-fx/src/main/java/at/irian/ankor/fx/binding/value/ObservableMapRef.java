package at.irian.ankor.fx.binding.value;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.fx.binding.cache.FxCacheSupport;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.util.RefMap;
import com.sun.javafx.collections.MapListenerHelper;
import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.util.Callback;

/**
 * A JavaFX ObservableMap backed by a Ankor Ref that references a Map.
 * The map entries of this observable map are directly retrieved from the underlying "map" Ref.
 * Listeners of this observable get notified whenever the referenced map's content changes.
 *
 * @author Manfred Geiler
 */
public class ObservableMapRef<K,V> extends RefMap<K,V> implements ObservableMap<K,V> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ObservableMapRef.class);

    private final FxMapChangeHelper<K,V> changeHelper;
    private final ChangeEventListener changeEventListener;

    private MapListenerHelper<K,V> listenerHelper = null;

    protected ObservableMapRef(Ref ref) {
        super(ref);
        this.changeHelper = new FxMapChangeHelper<>(this);
        this.changeEventListener = new ObservableChangeEventListener(ref, this) {
            @Override
            protected void handleChange(Ref changedProperty, Change change) {
                if (changedProperty.equals(ObservableMapRef.this.mapRef)) {
                    switch (change.getType()) {
                        case value:
                            // map itself was replaced --> ignore here, handled by the wrapping ObservableValue
                            break;
                        case insert:
                        case delete:
                        case replace:
                            for (MapChangeListener.Change<K, V> mapChange : changeHelper.toFxChanges(change)) {
                                MapListenerHelper.fireValueChangedEvent(listenerHelper, mapChange);
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown change type " + change.getType());
                    }
                }
            }
        };
        this.mapRef.context().modelSession().getEventListeners().add(this.changeEventListener);
    }


    public static <K,V> ObservableMap<K,V> createObservableMap(Ref ref) {
        return FxCacheSupport.getBindingCache(ref)
                             .getObservableMap(ref, null, new Callback<Ref, ObservableMap<K, V>>() {
                                 @Override
                                 public ObservableMap<K, V> call(Ref ref) {
                                     LOG.debug("Creating ObservableMap for {}", ref);
                                     return new ObservableMapRef<>(ref);
                                 }
                             });
    }


    @Override
    public void addListener(InvalidationListener invalidationlistener) {
        listenerHelper = MapListenerHelper.addListener(listenerHelper, invalidationlistener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationlistener) {
        listenerHelper = MapListenerHelper.removeListener(listenerHelper, invalidationlistener);
    }

    @Override
    public void addListener(MapChangeListener<? super K, ? super V> mapChangeListener) {
        listenerHelper = MapListenerHelper.addListener(listenerHelper, mapChangeListener);
    }

    @Override
    public void removeListener(MapChangeListener<? super K, ? super V> mapChangeListener) {
        listenerHelper = MapListenerHelper.removeListener(listenerHelper, mapChangeListener);
    }


    // misc

    protected void finalize() throws Throwable {
        this.mapRef.context().modelSession().getEventListeners().remove(this.changeEventListener);
        super.finalize();
    }

}
