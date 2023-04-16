package nl.thedutchruben.mccore.global.caching;

import java.util.List;

import nl.thedutchruben.mccore.global.caching.CachingObject;

/**
 * Abstract file system so i can implement thins like redis cache later
 */
public abstract class CachingFileSystem {

    /**
     * Save the object to the file system
     * 
     * @param key
     * @param cachingObject
     */
    public abstract void saveToFileSystem(String key, CachingObject cachingObject);

    public abstract void removeFromFileSystem(CachingObject cachingObject);

    public abstract List<CachingObject> getAllFromFileSystem();
}
