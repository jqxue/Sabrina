package edu.stanford.thingengine.sabrina.model;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

import edu.stanford.thingengine.sabrina.events.EventSource;
import edu.stanford.thingengine.sabrina.exceptions.RuleExecutionException;
import edu.stanford.thingengine.sabrina.exceptions.TriggerValueTypeException;
import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;

/**
 * Created by gcampagn on 4/30/15.
 */
public interface Trigger {
    String OBJECT = "object";
    String TRIGGER = "trigger";
    String PARAMS = "params";

    Channel getChannel();

    Collection<EventSource> getEventSources();

    Collection<ObjectPool.Object> getPlaceholders();

    void update(Context ctx) throws RuleExecutionException;

    boolean isFiring() throws RuleExecutionException;

    String toHumanString();

    JSONObject toJSON() throws JSONException;

    void resolve() throws UnknownObjectException;

    void typeCheck(Map<String, Class<? extends Value>> context) throws TriggerValueTypeException;

    void updateContext(Map<String, Value> context) throws RuleExecutionException;
}
