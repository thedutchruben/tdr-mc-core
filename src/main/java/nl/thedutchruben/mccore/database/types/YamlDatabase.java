package nl.thedutchruben.mccore.database.types;

import nl.thedutchruben.mccore.Mccore;
import nl.thedutchruben.mccore.database.Database;
import nl.thedutchruben.mccore.database.DatabaseConfig;
import nl.thedutchruben.mccore.database.configs.YamlDatabaseConfig;
import nl.thedutchruben.mccore.utils.config.FileManager;

public class YamlDatabase extends Database {
    private YamlDatabaseConfig databaseConfig;
    private FileManager fileManager;
    /**
     * Startup of the database
     */
    @Override
    public void connect(DatabaseConfig config) {
        databaseConfig = (YamlDatabaseConfig) config;
//        fileManager = Mccore.getInstance().ge
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

    public YamlDatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }
}
