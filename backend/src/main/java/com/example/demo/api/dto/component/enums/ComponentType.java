package com.example.demo.api.dto.component.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum ComponentType {
    CARD,
    INPUT,
    BUTTON,
    LABEL;

    @JsonCreator
    public static ComponentType fromString(String value) {
        try {
            return ComponentType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unknown component type: '" + value + "'. Allowed: " +
                            Arrays.toString(ComponentType.values())
            );
        }
    }
}
