package com.example.demo.api.dto.component.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

/**
 * Tipurile de componente UI suportate de aplicație (card, câmp de intrare, buton,
 * etichetă). Folosit la (de)serializarea componentelor și la persistarea lor.
 */
public enum ComponentType {
    CARD,
    INPUT,
    BUTTON,
    LABEL;

    /**
     * Convertește un șir de caractere (insensibil la majuscule) în valoarea de
     * enumerare corespunzătoare. Folosit de Jackson la deserializarea din JSON.
     *
     * @param value numele tipului de componentă (ex. „button”, „CARD”)
     * @return valoarea de enumerare corespunzătoare
     * @throws IllegalArgumentException dacă valoarea nu corespunde niciunui tip cunoscut
     */
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
