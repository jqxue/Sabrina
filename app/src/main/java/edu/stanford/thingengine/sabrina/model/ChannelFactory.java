package edu.stanford.thingengine.sabrina.model;

import java.util.Map;

import edu.stanford.thingengine.sabrina.exceptions.TriggerValueTypeException;
import edu.stanford.thingengine.sabrina.exceptions.UnknownChannelException;
import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;

/**
 * Created by gcampagn on 4/30/15.
 */
public abstract class ChannelFactory extends ObjectPool.ObjectFactory<Channel> {
    protected ChannelFactory(String prefix) {
        super(prefix);
    }

    public abstract Class<? extends Value> getParamType(String method, String name) throws UnknownChannelException, TriggerValueTypeException;

    public abstract Trigger createTrigger(Channel channel, String method, Map<String, Value> params)
            throws UnknownObjectException, UnknownChannelException, TriggerValueTypeException;

    public abstract Action createAction(Channel channel, String method, Map<String, Value> params)
            throws UnknownObjectException, UnknownChannelException, TriggerValueTypeException;
}
