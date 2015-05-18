package edu.stanford.braincat.rulepedia.channels.generic;

import android.support.annotation.Nullable;
import android.util.ArrayMap;

import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

import java.io.Serializable;
import java.util.Map;

import edu.stanford.braincat.rulepedia.model.Value;

/**
 * Created by gcampagn on 5/15/15.
 */
public class JSUtil {
    public static NativeObject parametersToJavascript(Map<String, Value> params) {
        NativeObject object = new NativeObject();

        for (Map.Entry<String, Value> e : params.entrySet())
            ScriptableObject.putProperty(object, e.getKey(), valueToJavascript(e.getValue()));

        return object;
    }

    public static Map<String, Value> javascriptToParameters(NativeObject object) {
        Map<String, Value> result = new ArrayMap<>();

        for (Map.Entry<Object, Object> e : object.entrySet())
            result.put(e.getKey().toString(), javascriptToValue(e.getValue()));

        return result;
    }

    public static Value javascriptToValue(Object object) {
        if (object == null)
            throw new NullPointerException();
        if (object instanceof String)
            return new Value.Text((String) object);
        else if (object instanceof Boolean)
            return new Value.Text(object.toString());
        else if (object instanceof Number)
            return new Value.Number((Number) object);
        else
            return new Value.Text(object.toString());
    }

    public static Object valueToJavascript(@Nullable Value value) {
        if (value == null) {
            return null;
        } else if (value instanceof Value.Text) {
            return ((Value.Text) value).getText();
        } else if (value instanceof Value.Number) {
            return ((Value.Number) value).getNumber();
        } else if (value instanceof Value.DirectObject) {
            return ((Value.DirectObject) value).getObject().getUrl();
        } else if (value instanceof Value.Picture) {
            return value.toString();
        } else {
            // what else?
            return value.toString();
        }
    }

    public static void parseExtras(Map<String, Serializable> parsed, ScriptableObject extras) {
        for (Object id : ScriptableObject.getPropertyIds(extras)) {
            Object value = ScriptableObject.getProperty(extras, id.toString());
            Serializable serializable;
            if (value == null)
                continue;
            if (value instanceof String || value instanceof Boolean || value instanceof Number)
                serializable = (Serializable) value;
            else
                serializable = value.toString();
            parsed.put(id.toString(), serializable);
        }
    }
}
