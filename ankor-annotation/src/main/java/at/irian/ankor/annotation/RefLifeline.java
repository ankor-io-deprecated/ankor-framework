package at.irian.ankor.annotation;

import at.irian.ankor.ref.Ref;

/**
* @author Manfred Geiler
*/
class RefLifeline implements Lifeline {

    private final Ref ref;

    public RefLifeline(Ref ref) {
        this.ref = ref;
    }

    @Override
    public boolean isAlive() {
        return ref.isValid();
    }
}
