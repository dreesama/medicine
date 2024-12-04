package com.company.medicine.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum UnitType implements EnumClass<String> {

    TABLET("tablet"),
    CAPSULE("capsule"),
    ML("ml"),
    MG("mg"),
    GRAM("g"),
    PIECE("piece"),
    SACHET("sachet"),
    VIAL("vial"),
    AMPULE("ampule");
    private final String id;

    UnitType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static UnitType fromId(String id) {
        for (UnitType at : UnitType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}