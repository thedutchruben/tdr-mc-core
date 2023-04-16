package nl.thedutchruben.mccore.global.caching.fileSystemTypes;

import com.google.gson.Gson;

import nl.thedutchruben.mccore.global.caching.CachingFileSystem;
import nl.thedutchruben.mccore.global.caching.CachingObject;

public class JsonFileType extends CachingFileSystem {

    @Override
    public void saveToFileSystem(String key, CachingObject cachingObject) {
        new Gson().toJson(cachingObject);
    }

}
