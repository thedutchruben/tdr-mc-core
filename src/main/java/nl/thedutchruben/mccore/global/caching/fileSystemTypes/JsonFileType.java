package nl.thedutchruben.mccore.global.caching.fileSystemTypes;

import java.util.List;

import com.google.gson.Gson;

import nl.thedutchruben.mccore.global.caching.CachingFileSystem;
import nl.thedutchruben.mccore.global.caching.CachingObject;

public class JsonFileType extends CachingFileSystem {

    @Override
    public void saveToFileSystem(String key, CachingObject cachingObject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveToFileSystem'");
    }

    @Override
    public void removeFromFileSystem(CachingObject cachingObject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeFromFileSystem'");
    }

    @Override
    public List<CachingObject> getAllFromFileSystem() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllFromFileSystem'");
    }

}
