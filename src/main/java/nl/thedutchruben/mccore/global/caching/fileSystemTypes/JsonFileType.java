package nl.thedutchruben.mccore.global.caching.fileSystemTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;

import nl.thedutchruben.mccore.global.caching.CachingFileSystem;
import nl.thedutchruben.mccore.global.caching.CachingObject;

public class JsonFileType extends CachingFileSystem {

    @Override
    public CompletableFuture<Void> saveToFileSystem(String key, CachingObject cachingObject) {
        return CompletableFuture.supplyAsync(() -> {
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> removeFromFileSystem(CachingObject cachingObject) {
        return CompletableFuture.supplyAsync(() -> {
            return null;
        });
    }

    @Override
    public CompletableFuture<List<CachingObject>> getAllFromFileSystem() {
        return CompletableFuture.supplyAsync(() -> {
            List<CachingObject> list = new ArrayList<>();
            return list;
        });
    }

}
