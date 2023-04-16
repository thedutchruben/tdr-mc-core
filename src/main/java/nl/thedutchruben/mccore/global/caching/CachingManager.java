package nl.thedutchruben.mccore.global.caching;

import java.util.HashMap;
import java.util.Map;
import nl.thedutchruben.mccore.global.caching.CachingObject;

public class CachingManager {
    private Map<String, CachingObject> cachingMap = new HashMap<>();

    public CachingManager() {
        // TODO: Load from storage
    }

    /**
     * Get the caching object based of the key
     * 
     * @param key
     * @return
     */
    public CachingObject getCachingObject(String key) {
        return cachingMap.get(key);
    }

}
