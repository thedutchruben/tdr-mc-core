package nl.thedutchruben.mccore.config;

/**
 * Created by Ruben on 7-6-2017.
 * Package: nl.thedutchruben.mccore.config
 * Project: TheDutchRuben_MCCore
 * Copyright (c) TheDutchRuben. All rights reserved.
 * <p>
 *     This class is used to check if there is a new version of the plugin.
 *     It is used in the config.yml file.
 *     The config.yml file is located in the config folder.
 * </p>
 *
 */
public class UpdateCheckerConfig {
    private String permission;
    private int checkTime;

    public UpdateCheckerConfig(String permission, int checkTime) {
        this.permission = permission;
        this.checkTime = checkTime;
    }

    public String getPermission() {
        return permission;
    }


    public int getCheckTime() {
        return checkTime;
    }
}
