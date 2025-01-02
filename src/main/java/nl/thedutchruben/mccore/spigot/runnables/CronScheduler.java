package nl.thedutchruben.mccore.spigot.runnables;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class CronScheduler {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void scheduleTask(String cronExpression, Runnable task) {
        // Convert cron expression into its components
        CronComponents cronComponents = parseCronExpression(cronExpression);

        // Schedule the task to run periodically
        scheduler.scheduleAtFixedRate(() -> {
            // Get the current time
            Calendar now = Calendar.getInstance();

            // Check if current time matches the cron expression
            if (matchesCron(now, cronComponents)) {
                task.run(); // Run the task if the cron expression matches
            }
        }, 0, 1, TimeUnit.MINUTES); // Check every minute
    }

    private boolean matchesCron(Calendar currentTime, CronComponents cronComponents) {
        return matchesField(currentTime.get(Calendar.MINUTE), cronComponents.minute) &&
                matchesField(currentTime.get(Calendar.HOUR_OF_DAY), cronComponents.hour) &&
                matchesField(currentTime.get(Calendar.DAY_OF_MONTH), cronComponents.dayOfMonth) &&
                matchesField(currentTime.get(Calendar.MONTH) + 1, cronComponents.month) && // Calendar months are 0-based
                matchesField(currentTime.get(Calendar.DAY_OF_WEEK), cronComponents.dayOfWeek);
    }

    private boolean matchesField(int value, CronField field) {
        if (field.isWildcard()) return true; // If it's "*", it matches all values
        if (field.isList()) {
            for (int listValue : field.getListValues()) {
                if (value == listValue) return true;
            }
        }
        if (field.isRange()) {
            if (value >= field.getRangeStart() && value <= field.getRangeEnd()) return true;
        }
        if (field.isStep()) {
            if (value % field.getStep() == 0) return true;
        }
        return false;
    }

    private CronComponents parseCronExpression(String cronExpression) {
        String[] parts = cronExpression.split("\\s+");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Cron expression must have 5 fields.");
        }

        CronComponents components = new CronComponents();
        components.minute = parseField(parts[0]);
        components.hour = parseField(parts[1]);
        components.dayOfMonth = parseField(parts[2]);
        components.month = parseField(parts[3]);
        components.dayOfWeek = parseField(parts[4]);

        return components;
    }

    private CronField parseField(String field) {
        CronField cronField = new CronField();

        // Handle the "*" wildcard
        if (field.equals("*")) {
            cronField.setWildcard(true);
            return cronField;
        }

        // Handle ranges (e.g., 1-5)
        if (field.contains("-")) {
            String[] range = field.split("-");
            cronField.setRange(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
            return cronField;
        }

        // Handle lists (e.g., 1,3,5)
        if (field.contains(",")) {
            String[] values = field.split(",");
            cronField.setListValues(Arrays.stream(values).mapToInt(Integer::parseInt).toArray());
            return cronField;
        }

        // Handle steps (e.g., */5)
        if (field.contains("/")) {
            String[] step = field.split("/");
            cronField.setStep(Integer.parseInt(step[1]));
            return cronField;
        }

        // Single value (e.g., 5)
        cronField.setListValues(new int[]{Integer.parseInt(field)});
        return cronField;
    }

    // Cron Components representing each field in the cron expression
    static class CronComponents {
        CronField minute;
        CronField hour;
        CronField dayOfMonth;
        CronField month;
        CronField dayOfWeek;
    }

    // Class representing each field's possible values (wildcard, range, list, step)
    static class CronField {
        private boolean wildcard;
        private int rangeStart, rangeEnd;
        private int[] listValues;
        private int step;

        public boolean isWildcard() { return wildcard; }
        public void setWildcard(boolean wildcard) { this.wildcard = wildcard; }

        public boolean isRange() { return rangeStart > 0 && rangeEnd > 0; }
        public void setRange(int rangeStart, int rangeEnd) {
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;
        }

        public boolean isList() { return listValues != null; }
        public void setListValues(int[] listValues) { this.listValues = listValues; }

        public boolean isStep() { return step > 0; }
        public void setStep(int step) { this.step = step; }

        public int getRangeStart() { return rangeStart; }
        public int getRangeEnd() { return rangeEnd; }

        public int[] getListValues() { return listValues; }

        public int getStep() { return step; }
    }
}
