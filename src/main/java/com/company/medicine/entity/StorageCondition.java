package com.company.medicine.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum StorageCondition implements EnumClass<String> {

    ROOM_TEMPERATURE("Room Temperature, 15-25°C"),
    REFRIGERATED("Refrigerated, 2-8°C"),
    FROZEN("Frozen, -20°C"),
    COOL_DRY("Cool and Dry, Below 30°C"),
    PROTECTED_FROM_LIGHT("Protected from Light, Store in dark place");

    private final String id;

    StorageCondition(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static StorageCondition fromId(String id) {
        for (StorageCondition at : StorageCondition.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}