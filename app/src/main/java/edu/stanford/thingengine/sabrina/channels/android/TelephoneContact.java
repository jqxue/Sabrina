package edu.stanford.thingengine.sabrina.channels.android;

import edu.stanford.thingengine.sabrina.model.Contact;

/**
 * Created by gcampagn on 5/2/15.
 */
public class TelephoneContact extends Contact {
    public TelephoneContact(TelephoneContactFactory factory, String url) {
        super(factory, url);
    }

    @Override
    public String toHumanString() {
        return getAddress();
    }

    public String getAddress() {
        return getUrl().substring("tel:".length());
    }
}
