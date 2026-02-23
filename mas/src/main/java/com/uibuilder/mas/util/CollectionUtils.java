package com.uibuilder.mas.util;

import java.util.Collection;
import java.util.List;

/**
 * Collection utility methods.
 */
public class CollectionUtils {
    
    private CollectionUtils() {
        // Utility class
    }
    
    /**
     * Check if collection is null or empty.
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    /**
     * Check if collection is not null and not empty.
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
    
    /**
     * Safe get from list (returns null if index out of bounds).
     */
    public static <T> T safeGet(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }
}
