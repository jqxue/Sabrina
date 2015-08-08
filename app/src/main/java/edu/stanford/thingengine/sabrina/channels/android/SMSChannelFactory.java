package edu.stanford.thingengine.sabrina.channels.android;

import java.util.Map;

import edu.stanford.thingengine.sabrina.channels.interfaces.Messaging;
import edu.stanford.thingengine.sabrina.exceptions.TriggerValueTypeException;
import edu.stanford.thingengine.sabrina.exceptions.UnknownChannelException;
import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;
import edu.stanford.thingengine.sabrina.model.Action;
import edu.stanford.thingengine.sabrina.model.Channel;
import edu.stanford.thingengine.sabrina.model.ChannelFactory;
import edu.stanford.thingengine.sabrina.model.ChannelPool;
import edu.stanford.thingengine.sabrina.model.PlaceholderChannel;
import edu.stanford.thingengine.sabrina.model.Trigger;
import edu.stanford.thingengine.sabrina.model.Value;

/**
 * Created by gcampagn on 5/9/15.
 */
public class SMSChannelFactory extends ChannelFactory {
    public SMSChannelFactory() {
        super(ChannelPool.PREDEFINED_PREFIX + SMSChannel.ID);
    }

    @Override
    public Action createAction(Channel channel, String method, Map<String, Value> params) throws
            UnknownObjectException, UnknownChannelException {
        switch (method) {
            case Messaging.SEND_MESSAGE:
                return new SMSSendMessageAction(channel, params.get("destination"), params.get("content"));

            default:
                throw new UnknownChannelException(method);
        }
    }

    @Override
    public Trigger createTrigger(Channel channel, String method, Map<String, Value> params)
            throws TriggerValueTypeException, UnknownObjectException, UnknownChannelException {
        switch (method) {
            case Messaging.MESSAGE_RECEIVED:
                return new SMSMessageReceivedTrigger(channel, params.get(Messaging.CONTENT_CONTAINS), params.get(Messaging.SENDER_MATCHES));

            default:
                throw new UnknownChannelException(method);
        }
    }

    @Override
    public Class<? extends Value> getParamType(String method, String name) throws UnknownChannelException, TriggerValueTypeException {
        switch (method) {
            case Messaging.SEND_MESSAGE:
                switch (name) {
                    case Messaging.DESTINATION:
                        return Value.Contact.class;
                    case Messaging.MESSAGE:
                        return Value.Text.class;
                    default:
                        throw new TriggerValueTypeException("unknown parameter " + name);
                }

            case Messaging.MESSAGE_RECEIVED:
                switch (name) {
                    case Messaging.SENDER_MATCHES:
                        return Value.Contact.class;
                    case Messaging.CONTENT_CONTAINS:
                        return Value.Text.class;
                    default:
                        throw new TriggerValueTypeException("unknown parameter " + name);
                }

            default:
                throw new UnknownChannelException(method);
        }
    }

    @Override
    public Channel create(String url) throws UnknownObjectException {
        if (url.equals(getPrefix()))
            return new SMSChannel(this, url);
        else
            throw new UnknownObjectException(url);
    }

    @Override
    public Channel createPlaceholder(String url) {
        return new PlaceholderChannel(this, url, "text messaging");
    }

    @Override
    public String getName() {
        return SMSChannel.ID;
    }
}
