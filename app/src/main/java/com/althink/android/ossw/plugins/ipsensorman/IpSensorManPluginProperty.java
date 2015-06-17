package com.althink.android.ossw.plugins.ipsensorman;

/**
 * Created by krzysiek on 14/06/15.
 */
public enum IpSensorManPluginProperty {
    HEART_RATE(1, "heartRate"), CYCLING_SPEED(2, "cyclingSpeed"), CYCLING_CADENCE(3, "cyclingCadence");

    private int id;
    private String name;

    private IpSensorManPluginProperty(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static IpSensorManPluginProperty resolveByName(String propertyName) {
        for (IpSensorManPluginProperty property : IpSensorManPluginProperty.values()) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
