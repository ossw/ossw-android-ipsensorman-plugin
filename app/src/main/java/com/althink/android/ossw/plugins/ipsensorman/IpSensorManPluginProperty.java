package com.althink.android.ossw.plugins.ipsensorman;

import com.althink.android.ossw.plugins.api.PluginPropertyType;

/**
 * Created by krzysiek on 14/06/15.
 */
public enum IpSensorManPluginProperty {
    HEART_RATE(1, "heartRate", PluginPropertyType.INTEGER),
    CYCLING_SPEED(2, "cyclingSpeed", PluginPropertyType.FLOAT),
    CYCLING_CADENCE(3, "cyclingCadence", PluginPropertyType.FLOAT);

    private int id;
    private String name;
    private PluginPropertyType type;

    private IpSensorManPluginProperty(int id, String name, PluginPropertyType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public static IpSensorManPluginProperty resolveByName(String propertyName) {
        for (IpSensorManPluginProperty property : IpSensorManPluginProperty.values()) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PluginPropertyType getType() {
        return type;
    }
}
