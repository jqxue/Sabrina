package edu.stanford.thingengine.sabrina.model;

import java.util.HashMap;
import java.util.Map;

import edu.stanford.thingengine.sabrina.channels.ibeacon.IBeaconDevice;

/**
 * Created by braincat on 5/30/15.
 */
public class DeviceDatabase {
    private final Map<String, IBeaconDevice> devices;

    private final static DeviceDatabase instance = new DeviceDatabase();

    private DeviceDatabase() {
        devices = new HashMap<>();
    }

    public static DeviceDatabase get() {
        return instance;
    }

    public boolean addDevice(IBeaconDevice ibd) {
        if(devices.containsKey(ibd.uuid)) {
            return false;
        }

        devices.put(ibd.uuid, ibd);
        
        return true;
    }
}
