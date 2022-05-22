package nl.thedutchruben.mccore.database.types;

import nl.thedutchruben.mccore.database.Database;
import nl.thedutchruben.mccore.database.DatabaseConfig;

import java.util.concurrent.ExecutionException;

public class MysqlDatabase extends Database
{


    /**
     * Startup of the database
     *
     * @param databaseConfig
     */
    @Override
    public void connect(DatabaseConfig databaseConfig) {

    }

    /**ß
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
