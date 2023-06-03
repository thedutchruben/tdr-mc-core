package nl.thedutchruben.mccore.global.caching;

import nl.thedutchruben.mccore.global.caching.fileSystemTypes.JsonFileType;

import java.util.HashMap;
import java.util.Map;

public class CachingManager {
    private Map<String, CachingObject> cachingMap = new HashMap<>();
    private CachingFileSystem cachingFileSystem;

    public CachingManager() {
        this.cachingFileSystem = new JsonFileType();
        this.cachingFileSystem.getAllFromFileSystem().whenCompleteAsync((aList, throwable) -> {
            aList.forEach(data -> {
                cachingMap.put(data.getKey(), data);
            });
        });

    }

    /**
     * Get the caching object based of the key
     *
     * @param key
     * @return
     */
    public CachingObject getCachingObject(String key) {
        CachingObject object = cachingMap.get(key);
        if(object == null){
            return null;
        }
        if (object.isValid()) {
            return object;
        }
        return null;
    }

    /**
     * Add caching object
     *
     * @param key
     * @param object
     */
    public void addCachingObject(String key, CachingObject object) {
        if (cachingMap.get(key) != null) {
            if (cachingMap.get(key).isValid()) {
                return;
            }
        }
        cachingMap.put(key, object);
        if (object.isPersistent()) {
            object.saveToDisk();
        }
    }

    public CachingFileSystem getCachingFileSystem() {
        return cachingFileSystem;
    }

}
