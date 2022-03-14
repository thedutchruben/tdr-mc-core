package nl.thedutchruben.mccore.database;

import nl.thedutchruben.mccore.database.configs.YamlDatabaseConfig;

import java.util.concurrent.ExecutionException;

public abstract class Database {

    /**
     * Startup of the database
     */
    public abstract void connect(DatabaseConfig databaseConfig) throws ExecutionException, InterruptedException;

    /**
     * Remove the connection of the database
     */
    public abstract void disconnect();

    public abstract String getDisplayName();
}
