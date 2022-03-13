package nl.thedutchruben.mccore.config;

public class UpdateCheckerConfig {
    private String permission;
    private boolean showOps;
    private boolean showConsole;
    private int checkTime;

    public UpdateCheckerConfig(String permission, boolean showOps, boolean showConsole, int checkTime) {
        this.permission = permission;
        this.showOps = showOps;
        this.showConsole = showConsole;
        this.checkTime = checkTime;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isShowOps() {
        return showOps;
    }

    public boolean isShowConsole() {
        return showConsole;
    }

    public int getCheckTime() {
        return checkTime;
    }
}
