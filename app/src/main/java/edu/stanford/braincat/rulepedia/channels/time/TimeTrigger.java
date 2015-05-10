package edu.stanford.braincat.rulepedia.channels.time;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import edu.stanford.braincat.rulepedia.channels.PollingTrigger;
import edu.stanford.braincat.rulepedia.exceptions.RuleExecutionException;
import edu.stanford.braincat.rulepedia.exceptions.TriggerValueTypeException;
import edu.stanford.braincat.rulepedia.exceptions.UnknownObjectException;
import edu.stanford.braincat.rulepedia.model.InternalObjectFactory;
import edu.stanford.braincat.rulepedia.model.Trigger;
import edu.stanford.braincat.rulepedia.model.Value;

/**
 * Created by gcampagn on 5/9/15.
 */
public class TimeTrigger extends PollingTrigger {
    private final Timer object;

    public TimeTrigger(Timer object, Value interval) throws UnknownObjectException {
        super(((Value.Number) interval.resolve(null)).getNumber().longValue());
        this.object = object;
    }


    @Override
    public void update() throws RuleExecutionException {
        // nothing to do
    }

    @Override
    public boolean isFiring() throws RuleExecutionException {
        return getSource().checkEvent();
    }

    @Override
    public String toHumanString() {
        return "every " + getSource().getTimeout() + " ms";
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(Trigger.OBJECT, InternalObjectFactory.PREDEFINED_PREFIX + Timer.ID);
        json.put(Trigger.TRIGGER, Timer.ELAPSED);

        JSONArray jsonParams = new JSONArray();
        jsonParams.put(new Value.Number(getSource().getTimeout()).toJSON(Timer.INTERVAL));
        json.put(Trigger.PARAMS, jsonParams);
        return json;
    }

    @Override
    public void typeCheck(Map<String, Class<? extends Value>> context) throws TriggerValueTypeException {
        // nothing to do
    }

    @Override
    public void updateContext(Map<String, Value> context) throws RuleExecutionException {
        // nothing to do
    }
}
