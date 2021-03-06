package edu.stanford.thingengine.sabrina.channels.omlet;

import android.content.Context;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import edu.stanford.thingengine.sabrina.channels.SingleEventTrigger;
import edu.stanford.thingengine.sabrina.channels.interfaces.Messaging;
import edu.stanford.thingengine.sabrina.exceptions.RuleExecutionException;
import edu.stanford.thingengine.sabrina.exceptions.TriggerValueTypeException;
import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;
import edu.stanford.thingengine.sabrina.model.Channel;
import edu.stanford.thingengine.sabrina.model.Contact;
import edu.stanford.thingengine.sabrina.model.ObjectPool;
import edu.stanford.thingengine.sabrina.model.Trigger;
import edu.stanford.thingengine.sabrina.model.Value;

/**
 * Created by gcampagn on 6/3/15.
 */
public class OmletMessageReceivedTrigger extends SingleEventTrigger<OmletMessageEventSource> {
    private volatile Channel channel;
    private OmletMessage receivedMessage;
    private final String contentContains;
    private volatile Contact senderMatches;

    public OmletMessageReceivedTrigger(Channel channel, @Nullable Value contentContains, @Nullable Value senderMatches) throws TriggerValueTypeException, UnknownObjectException {
        this.channel = channel;

        if (contentContains != null)
            this.contentContains = ((Value.Text) contentContains.resolve(null)).getText();
        else
            this.contentContains = null;
        if (senderMatches != null)
            this.senderMatches = (Contact) ((Value.DirectObject) senderMatches.resolve(null)).getObject();
        else
            this.senderMatches = null;
    }

    public Channel getChannel() {
        return channel;
    }

    public Collection<ObjectPool.Object> getPlaceholders() {
        Collection<ObjectPool.Object> result = new HashSet<>();

        Channel currentChannel = channel;
        if (currentChannel.isPlaceholder())
            result.add(currentChannel);
        Contact currentSenderMatcher = senderMatches;
        if (currentSenderMatcher != null && currentSenderMatcher.isPlaceholder())
            result.add(currentSenderMatcher);

        return result;
    }

    @Override
    public void update(Context ctx) {
        if (!getSource().checkEvent()) {
            receivedMessage = null;
            return;
        }

        receivedMessage = getSource().getLastMessage();
        assert receivedMessage != null;

        if (!receivedMessage.getType().equals("text")) {
            receivedMessage = null;
            return;
        }

        String text = receivedMessage.getText(ctx);
        if (text == null) {
            receivedMessage = null;
            return;
        }

        if (contentContains != null) {
            if (!text.contains(contentContains)) {
                receivedMessage = null;
                return;
            }
        }

        if (senderMatches != null) {
            try {
                Contact sender = receivedMessage.getSender();
                if (!sender.equals(senderMatches)) {
                    receivedMessage = null;
                }
            } catch (UnknownObjectException e) {
                receivedMessage = null;
            }
        }
    }

    @Override
    public boolean isFiring() {
        return receivedMessage != null;
    }

    @Override
    public String toHumanString() {
        return "a text message is received";
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(Trigger.OBJECT, channel.getUrl());
        json.put(Trigger.TRIGGER, Messaging.MESSAGE_RECEIVED);

        JSONArray jsonParams = new JSONArray();
        if (senderMatches != null) {
            try {
                jsonParams.put(new Value.Contact(senderMatches.getUrl()).toJSON(Messaging.SENDER_MATCHES));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        if (contentContains != null)
            jsonParams.put(new Value.Text(contentContains).toJSON(Messaging.CONTENT_CONTAINS));
        json.put(Trigger.PARAMS, jsonParams);
        return json;
    }

    @Override
    public void resolve() throws UnknownObjectException {
        Channel newChannel = channel.resolve();
        if (!(newChannel instanceof OmletChannel))
            throw new UnknownObjectException(newChannel.getUrl());
        Contact newSenderMatches = senderMatches != null ? senderMatches.resolve() : null;
        senderMatches = newSenderMatches;

        setSource(((OmletChannel) newChannel).getEventSource());
        channel = newChannel;
        senderMatches = newSenderMatches;
    }

    @Override
    public void typeCheck(Map<String, Class<? extends Value>> context) throws TriggerValueTypeException {
        context.put(Messaging.SENDER, Value.Contact.class);
        context.put(Messaging.MESSAGE, Value.Text.class);
    }

    @Override
    public void updateContext(Map<String, Value> context) throws RuleExecutionException {
        try {
            context.put(Messaging.SENDER, new Value.DirectObject<>(receivedMessage.getSender()));
        } catch (UnknownObjectException e) {
            throw new RuntimeException(e);
        }

        context.put(Messaging.MESSAGE, new Value.Text(receivedMessage.getText(null), true));
    }
}
