package com.company.medicine.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum PackageType implements EnumClass<String> {

    BOX("Box"),
    BOTTLE("Bottle"),
    BLISTER_PACK("Blister Pack"),
    VIAL("Vial"),
    AMPULE("Ampule"),
    TUBE("Tube"),
    INHALER("Inhaler"),
    SYRINGE("Syringe"),
    SACHET("Sachet");

    private final String id;

    PackageType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static PackageType fromId(String id) {
        for (PackageType at : PackageType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}