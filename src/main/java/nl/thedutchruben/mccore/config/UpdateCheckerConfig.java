package nl.thedutchruben.mccore.config;

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
