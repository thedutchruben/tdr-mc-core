package nl.thedutchruben.mccore.global.caching.fileSystemTypes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.global.caching.CachingFileSystem;
import nl.thedutchruben.mccore.global.caching.CachingObject;
import nl.thedutchruben.mccore.utils.config.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JsonFileType extends CachingFileSystem {
    private final FileManager fileManager = new FileManager(Mccore.getInstance().getJavaPlugin());
    private final Gson gson = new GsonBuilder()
            .disableHtmlEscaping().setPrettyPrinting().create();

    @Override
    public CompletableFuture<Void> save(String key, CachingObject cachingObject) {
        return CompletableFuture.supplyAsync(() -> {
            fileManager.getConfig("caching/" + key + ".json").get().set("", gson.toJson(cachingObject));
            fileManager.getConfig("caching/" + key + ".json").save();
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> remove(CachingObject cachingObject) {
        return CompletableFuture.supplyAsync(() -> {
            fileManager.getConfig("caching/" + cachingObject.getKey() + ".json").file.delete();
            return null;
        });
    }

    @Override
    public CompletableFuture<List<CachingObject>> loadALl() {
        return CompletableFuture.supplyAsync(() -> {
            List<CachingObject> list = new ArrayList<>();
            if(Mccore.getInstance().getJavaPlugin() == null) return list;
            for (File file : new File(Mccore.getInstance().getJavaPlugin().getDataFolder(), "caching/").listFiles()) {
                list.add(gson.fromJson(fileManager.getConfig("caching/" + file.getName() + ".json").get().getString(""),
                        CachingObject.class));
            }
            return list;
        });
    }

}
