package edu.stanford.thingengine.sabrina.channels.omlet;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import edu.stanford.thingengine.sabrina.channels.Util;
import edu.stanford.thingengine.sabrina.exceptions.UnknownObjectException;
import edu.stanford.thingengine.sabrina.model.Contact;
import edu.stanford.thingengine.sabrina.model.ContactPool;

/**
 * Created by gcampagn on 5/26/15.
 */
public class OmletMessage {
    private final long objectId;
    private final String objectType;
    private final long feedId;
    private Long cachedSenderId;
    private String cachedText;
    private String cachedImageUrl;

    private OmletMessage(long objectId, String objectType, long feedId) {
        this.objectId = objectId;
        this.objectType = objectType;
        this.feedId = feedId;
    }

    public String getType() {
        return objectType;
    }

    public String getFeedUri() {
        return OmletChannel.FEED_CONTENT_URI + feedId;
    }

    public Contact getSender() throws UnknownObjectException {
        return ContactPool.get().getObject(getFeedUri());
    }

    public boolean cacheAll(Context ctx) {
        try {
            try (Cursor queryCursor = ctx.getContentResolver().query(Uri.parse(OmletChannel.CONTENT_URI),
                    new String[]{"senderId", "text", "fullsizeHash"},
                    "Id = ?",
                    new String[]{String.valueOf(objectId)}, null)) {
                if (!queryCursor.moveToFirst())
                    return false;

                cachedSenderId = queryCursor.getLong(0);
                cachedText = queryCursor.getString(1);
                byte[] blob = queryCursor.getBlob(2);
                if (blob != null)
                    cachedImageUrl = "content://mobisocial.osm/blobs/" + Util.bytesToHexString(blob).toLowerCase();
                else
                    cachedImageUrl = null;
            }

            return true;
        } catch(RuntimeException e) {
            return false;
        }
    }

    public Long getSenderId(Context ctx) {
        if (cachedSenderId != null)
            return cachedSenderId;

        if (ctx == null)
            return null;

        try (Cursor queryCursor = ctx.getContentResolver().query(Uri.parse(OmletChannel.CONTENT_URI),
                new String[]{"senderId"},
                "Id = ?",
                new String[]{String.valueOf(objectId)}, null)) {
            if (!queryCursor.moveToFirst())
                return null;

            cachedSenderId = queryCursor.getLong(0);
            return cachedSenderId;
        }
    }

    @Nullable
    public String getText(Context ctx) {
        if (cachedText != null)
            return cachedText;

        if (ctx == null)
            return null;

        try (Cursor queryCursor = ctx.getContentResolver().query(Uri.parse(OmletChannel.CONTENT_URI),
                new String[]{"text"},
                "Id = ?",
                new String[]{String.valueOf(objectId)}, null)) {
            if (!queryCursor.moveToFirst())
                return null;

            cachedText = queryCursor.getString(0);
            return cachedText;
        }
    }

    @Nullable
    public String getPicture(Context ctx) {
        if (cachedImageUrl != null)
            return cachedImageUrl;

        if (ctx == null)
            return null;

        try (Cursor queryCursor = ctx.getContentResolver().query(Uri.parse(OmletChannel.CONTENT_URI),
                new String[]{"fullsizeHash"},
                "Id = ?",
                new String[]{String.valueOf(objectId)}, null)) {
            if (!queryCursor.moveToFirst())
                return null;

            cachedImageUrl = "content://mobisocial.osm/blobs/" + Util.bytesToHexString(queryCursor.getBlob(0)).toLowerCase();
            return cachedImageUrl;
        }
    }

    public static OmletMessage fromBundle(Bundle bundle) {
        return new OmletMessage(bundle.getLong("mobisocial.intent.extra.OBJECT_ID"),
                bundle.getString("mobisocial.intent.extra.OBJECT_TYPE"),
                bundle.getLong("mobisocial.intent.extra.FEED_ID"));
    }
}
