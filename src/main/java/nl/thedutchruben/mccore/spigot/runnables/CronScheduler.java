package nl.thedutchruben.mccore.spigot.runnables;

import java.util.*;
import java.util.concurrent.*;

public class CronScheduler {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Schedules a task to run based on a cron expression.
     *
     * @param cronExpression the cron expression defining the schedule
     * @param task the task to be executed
     */
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

    /**
     * Checks if the current time matches the cron expression components.
     *
     * @param currentTime the current time
     * @param cronComponents the components of the cron expression
     * @return true if the current time matches the cron expression, false otherwise
     */
    private boolean matchesCron(Calendar currentTime, CronComponents cronComponents) {
        return matchesField(currentTime.get(Calendar.MINUTE), cronComponents.minute) &&
                matchesField(currentTime.get(Calendar.HOUR_OF_DAY), cronComponents.hour) &&
                matchesField(currentTime.get(Calendar.DAY_OF_MONTH), cronComponents.dayOfMonth) &&
                matchesField(currentTime.get(Calendar.MONTH) + 1, cronComponents.month) && // Calendar months are 0-based
                matchesField(currentTime.get(Calendar.DAY_OF_WEEK), cronComponents.dayOfWeek);
    }

    /**
     * Checks if a value matches a cron field.
     *
     * @param value the value to check
     * @param field the cron field
     * @return true if the value matches the cron field, false otherwise
     */
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

    /**
     * Parses a cron expression string into its components.
     *
     * @param cronExpression the cron expression string to parse
     * @return a CronComponents object representing the parsed components
     * @throws IllegalArgumentException if the cron expression does not have 5 fields
     */
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

    /**
     * Parses a cron field string and returns a CronField object representing the field.
     *
     * @param field the cron field string to parse
     * @return a CronField object representing the parsed field
     */
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

    /**
     * Represents the components of a cron expression.
     */
    static class CronComponents {
        CronField minute;      // The minute field of the cron expression
        CronField hour;        // The hour field of the cron expression
        CronField dayOfMonth;  // The day of the month field of the cron expression
        CronField month;       // The month field of the cron expression
        CronField dayOfWeek;   // The day of the week field of the cron expression
    }

    /**
     * Represents a field in a cron expression with various possible values.
     */
    static class CronField {
        private boolean wildcard; // Indicates if the field is a wildcard ("*")
        private int rangeStart, rangeEnd; // Start and end values for a range
        private int[] listValues; // List of specific values
        private int step; // Step value for increments

        /**
         * Checks if the field is a wildcard.
         * @return true if the field is a wildcard, false otherwise.
         */
        public boolean isWildcard() { return wildcard; }

        /**
         * Sets the field as a wildcard.
         * @param wildcard true to set the field as a wildcard, false otherwise.
         */
        public void setWildcard(boolean wildcard) { this.wildcard = wildcard; }

        /**
         * Checks if the field is a range.
         * @return true if the field is a range, false otherwise.
         */
        public boolean isRange() { return rangeStart > 0 && rangeEnd > 0; }

        /**
         * Sets the range values for the field.
         * @param rangeStart the start value of the range.
         * @param rangeEnd the end value of the range.
         */
        public void setRange(int rangeStart, int rangeEnd) {
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;
        }

        /**
         * Checks if the field is a list of values.
         * @return true if the field is a list, false otherwise.
         */
        public boolean isList() { return listValues != null; }

        /**
         * Sets the list of values for the field.
         * @param listValues an array of specific values.
         */
        public void setListValues(int[] listValues) { this.listValues = listValues; }

        /**
         * Checks if the field is a step value.
         * @return true if the field is a step, false otherwise.
         */
        public boolean isStep() { return step > 0; }

        /**
         * Sets the step value for the field.
         * @param step the step value.
         */
        public void setStep(int step) { this.step = step; }

        /**
         * Gets the start value of the range.
         * @return the start value of the range.
         */
        public int getRangeStart() { return rangeStart; }

        /**
         * Gets the end value of the range.
         * @return the end value of the range.
         */
        public int getRangeEnd() { return rangeEnd; }

        /**
         * Gets the list of specific values.
         * @return an array of specific values.
         */
        public int[] getListValues() { return listValues; }

        /**
         * Gets the step value.
         * @return the step value.
         */
        public int getStep() { return step; }
    }
}