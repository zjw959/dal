package logic.hero;

import java.util.HashMap;
import java.util.Map;

import logic.constant.EPropertyType;

public class Property {
    private final Map<EPropertyType, Integer> properties;

    public Property() {
        properties = new HashMap<EPropertyType, Integer>();
        reset();
    }

    public void reset() {
        for (EPropertyType type : EPropertyType.values()) {
            properties.put(type, 0);
        }
    }

    public int getProperty(EPropertyType type) {
        return properties.get(type);
    }

    public void setProperty(EPropertyType type, int val) {
        properties.put(type, val);
    }

    public void addTo(Property other) {
        for (EPropertyType type : EPropertyType.values()) {
            setProperty(type, getProperty(type) + other.getProperty(type));
        }
    }

    public Property negate() {
        for (EPropertyType type : EPropertyType.values()) {
            setProperty(type, -getProperty(type));
        }
        return this;
    }

    public void addProperty(EPropertyType type, int val) {
        setProperty(type, getProperty(type) + val);
    }
}
