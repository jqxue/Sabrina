package edu.stanford.thingengine.sabrina.model;

/**
 * Created by gcampagn on 5/30/15.
 */
public abstract class DeviceFactory extends ObjectPool.ObjectFactory<Device> {
    protected DeviceFactory(String prefix) {
        super(prefix);
    }
}
