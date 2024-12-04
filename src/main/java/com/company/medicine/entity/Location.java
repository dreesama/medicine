package com.company.medicine.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum Location implements EnumClass<String> {

    STORAGE1("storage1"),
    STORAGE2("storage2"),
    STORAGE3("storage3");

    private final String id;

    Location(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static Location fromId(String id) {
        for (Location at : Location.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}