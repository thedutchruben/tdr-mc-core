package nl.thedutchruben.mccore.database.types;

import nl.thedutchruben.mccore.database.Database;
import nl.thedutchruben.mccore.database.DatabaseConfig;
import nl.thedutchruben.mccore.database.configs.MongoDatabaseConfig;

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
    @Override
    public void connect(DatabaseConfig databaseConfig) throws ExecutionException, InterruptedException {
        this.config = (MongoDatabaseConfig) databaseConfig;
        if(!checkMongoFile()){
            downloadMongoDriver().get();
        }

        //todo create connection
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
