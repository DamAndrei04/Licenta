package com.uibuilder.mas.agent.util;

import java.util.Collection;
import java.util.List;

/**
 * Metode utilitare pentru lucrul cu colecții (verificări de golire și acces sigur la liste).
 */
public class CollectionUtils {

    private CollectionUtils() {
        // Clasă utilitară — nu se instanțiază
    }

    /**
     * Verifică dacă o colecție este {@code null} sau goală.
     *
     * @param collection colecția verificată
     * @return {@code true} dacă este {@code null} sau goală, altfel {@code false}
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Verifică dacă o colecție nu este {@code null} și nu este goală.
     *
     * @param collection colecția verificată
     * @return {@code true} dacă este nevidă, altfel {@code false}
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Accesează un element dintr-o listă în mod sigur, fără a arunca excepție la index invalid.
     *
     * @param list lista din care se citește
     * @param index poziția elementului dorit
     * @param <T> tipul elementelor listei
     * @return elementul de la poziția dată sau {@code null} dacă lista este {@code null} sau
     *         indexul este în afara limitelor
     */
    public static <T> T safeGet(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }
}
