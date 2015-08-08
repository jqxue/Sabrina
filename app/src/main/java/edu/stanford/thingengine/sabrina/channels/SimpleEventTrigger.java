package edu.stanford.thingengine.sabrina.channels;

import java.io.IOException;

import edu.stanford.thingengine.sabrina.events.EventSource;
import edu.stanford.thingengine.sabrina.exceptions.RuleExecutionException;

/**
 * Created by gcampagn on 4/30/15.
 * <p>
 * A trigger that is firing if and only if the associated event source
 * reports something
 * <p>
 * The trigger will keep firing or not according to the behavior
 * of the event source (edge triggered vs level trigger)
 */
public abstract class SimpleEventTrigger<K extends EventSource> extends SingleEventTrigger<K> {
    protected SimpleEventTrigger() {
        super();
    }

    public boolean isFiring() throws RuleExecutionException {
        try {
            return getSource().checkEvent();
        } catch (IOException e) {
            throw new RuleExecutionException("IO exception while checking event source", e);
        }
    }
}
