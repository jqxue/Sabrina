package edu.stanford.thingengine.sabrina.channels.omlet;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;

import edu.stanford.thingengine.sabrina.channels.interfaces.SendMessageAction;
import edu.stanford.thingengine.sabrina.exceptions.RuleExecutionException;
import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;
import edu.stanford.thingengine.sabrina.model.Channel;
import edu.stanford.thingengine.sabrina.model.Contact;
import edu.stanford.thingengine.sabrina.model.Value;
import mobisocial.osm.IOsmService;

/**
 * Created by gcampagn on 5/14/15.
 */
public class OmletSendMessageAction extends SendMessageAction {
    public OmletSendMessageAction(Channel channel, Value destination, Value message) {
        super(channel, destination, message);
    }

    @Override
    protected void sendMessage(Context ctx, Contact contact, String message) throws RuleExecutionException {
        IOsmService service = ((OmletChannel)getChannel()).getService();

        if (service == null)
            throw new RuleExecutionException("Omlet service not available");

        try {
            service.sendText(Uri.parse(contact.getUrl()), message);
        } catch(RemoteException e) {
            throw new RuleExecutionException(e);
        }
    }

    @Override
    public void resolve() throws UnknownObjectException {
        Channel newChannel = getChannel().resolve();
        if (!(newChannel instanceof OmletChannel))
            throw new UnknownObjectException(newChannel.getUrl());
        setChannel(newChannel);
    }
}
