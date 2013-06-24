package at.irian.ankor.fx.app;

/**
* @author Thomas Spiegl
*/
public interface ActionCompleteCallback {
    void onComplete();

    static ActionCompleteCallback empty = new ActionCompleteCallback() {
        @Override
        public void onComplete() {
        }
    };
}
