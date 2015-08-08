package edu.stanford.thingengine.sabrina.channels.android;

import edu.stanford.thingengine.sabrina.model.Contact;
import edu.stanford.thingengine.sabrina.model.ContactFactory;
import edu.stanford.thingengine.sabrina.model.PlaceholderContact;

/**
 * Created by gcampagn on 5/2/15.
 */
public class TelephoneContactFactory extends ContactFactory {
    public static final String ID = "telephone-contact";

    public TelephoneContactFactory() {
        super("tel:");
    }

    @Override
    public Contact create(String url) {
        return new TelephoneContact(this, url);
    }

    @Override
    public Contact createPlaceholder(String url) {
        return new PlaceholderContact(this, url, "a phone number");
    }

    @Override
    public String getName() {
        return ID;
    }
}
