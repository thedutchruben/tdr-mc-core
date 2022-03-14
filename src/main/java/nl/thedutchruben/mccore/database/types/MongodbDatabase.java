package nl.thedutchruben.mccore.database.types;

import lombok.SneakyThrows;
import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.database.Database;
import nl.thedutchruben.mccore.database.DatabaseConfig;
import nl.thedutchruben.mccore.database.configs.MongoDatabaseConfig;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class MongodbDatabase extends Database {
    private MongoDatabaseConfig config;

    public CompletableFuture<Void> downloadMongoDriver(){
        return CompletableFuture.runAsync(() -> {

        });
    }

    public boolean checkMongoFile(){
        return false;
    }

    /**
     * Startup of the database
     *
     * @param databaseConfig
     */
    @SneakyThrows
    @Override
    public void connect(DatabaseConfig databaseConfig) throws ExecutionException, InterruptedException {
        this.config = (MongoDatabaseConfig) databaseConfig;
        if(!checkMongoFile()){
            downloadMongoDriver().get();
        }

        //todo create connection\
        URLClassLoader child = new URLClassLoader(
                new URL[] {new File(Mccore.getInstance().getJavaPlugin().getDataFolder().getName() + "/tdrmccore/MongoDriver.jar").toURI().toURL()},
                this.getClass().getClassLoader()
        );
        Class classToLoad = Class.forName("com.MyClass", true, child);
        Method method = classToLoad.getDeclaredMethod("myMethod");
        Object instance = classToLoad.newInstance();
        Object result = method.invoke(instance);
    }

    /**
     * Remove the connection of the database
     */
    @Override
    public void disconnect() {

    }

    @Override
    public String getDisplayName() {
        return null;
    }
}
