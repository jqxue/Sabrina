package edu.stanford.braincat.rulepedia.channels.sms;

import java.util.Map;

import edu.stanford.braincat.rulepedia.channels.interfaces.Messaging;
import edu.stanford.braincat.rulepedia.exceptions.TriggerValueTypeException;
import edu.stanford.braincat.rulepedia.exceptions.UnknownChannelException;
import edu.stanford.braincat.rulepedia.exceptions.UnknownObjectException;
import edu.stanford.braincat.rulepedia.model.Action;
import edu.stanford.braincat.rulepedia.model.ChannelFactory;
import edu.stanford.braincat.rulepedia.model.ObjectPool;
import edu.stanford.braincat.rulepedia.model.Value;

/**
 * Created by gcampagn on 5/1/15.
 */
public class SMSActionFactory extends ChannelFactory<Action> {
    @Override
    public String getName() {
        return SMSChannel.ID;
    }

    @Override
    public Action createChannel(String method, ObjectPool.Object object, Map<String, Value> params) throws
            UnknownObjectException, UnknownChannelException {
        SMSChannel sms = (SMSChannel) object;

        switch (method) {
            case Messaging.SEND_MESSAGE:
                return new SMSSendMessageAction(sms, params.get("destination"), params.get("content"));

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
                    return Value.Object.class;
                case Messaging.MESSAGE:
                    return Value.Text.class;
                default:
                    throw new TriggerValueTypeException("unknown parameter " + name);
            }

            default:
                throw new UnknownChannelException(method);
        }
    }
}
