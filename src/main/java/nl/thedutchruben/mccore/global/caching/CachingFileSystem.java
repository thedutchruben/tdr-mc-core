package nl.thedutchruben.mccore.global.caching;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    public abstract CompletableFuture<Void> saveToFileSystem(String key, CachingObject cachingObject);

    /**
     * Remove the object from the file system
     * 
     * @param cachingObject
     */
    public abstract CompletableFuture<Void> removeFromFileSystem(CachingObject cachingObject);

    /**
     * Returns a list with saved caching object
     * 
     * @return
     */
    public abstract CompletableFuture<List<CachingObject>> getAllFromFileSystem();
}
