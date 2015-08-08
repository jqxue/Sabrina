package edu.stanford.thingengine.sabrina.channels.email;

import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;
import edu.stanford.thingengine.sabrina.model.Contact;
import edu.stanford.thingengine.sabrina.model.ContactFactory;
import edu.stanford.thingengine.sabrina.model.PlaceholderContact;

/**
 * Created by gcampagn on 5/29/15.
 */
public class EmailContactFactory extends ContactFactory {
    public static final String ID = "email-contact";

    public EmailContactFactory() {
        super("mailto:");
    }

    @Override
    public Contact create(String url) throws UnknownObjectException {
        return new EmailContact(this, url);
    }

    @Override
    public Contact createPlaceholder(String url) {
        return new PlaceholderContact(this, url, "an email address");
    }

    @Override
    public String getName() {
        return ID;
    }
}
