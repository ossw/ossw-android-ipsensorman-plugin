package com.althink.android.ossw.plugins.ipsensorman;

/**
 * Created by krzysiek on 14/06/15.
 */
public enum IpSensorManPluginFunction {

    NONE(1, "none");

    private final int id;
    private final String name;

    private IpSensorManPluginFunction(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static IpSensorManPluginFunction resolveById(int functionId) {
        for (IpSensorManPluginFunction function : IpSensorManPluginFunction.values()) {
            if (function.getId() == functionId) {
                return function;
            }
        }
        return null;
    }
}
