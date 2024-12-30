/*
*    Team Smoliv
*    Cole Briggs, Chris Axten, Erioluwa Soyemi, Grace Kelly Osena
*    CPSC 433 F24
*/

package com.scheduler.model;

/**
 * Represents a slot in the soccer league scheduling problem.
 * A slot is a time period during which an Event (game or practice) can be scheduled.
 */
public class Slot {

    /*
     * The identifier of the slot
     */
    private final String id;

    private final String printString;

    /*
     * True if this slot is a game slot, false if it is a practice slot.
     */
    private final boolean isGameSlot;

    /*
     * The day of the slot
     */
    private final String day;

    /*
     * The start time of the slot in minutes since midnight
     */
    private final int startTime;
    
    /*
     * The maximum number of events that can be scheduled in this slot (hard constraint)
     */
    private final int max;
    
    /*
     * The minimum number of events that can be scheduled in this slot (soft constraint)
     */
    private final int min;

    /*
     * Constructor for the Slot class.
     */
    public Slot(boolean isGameSlot, String day, String startTime, int max, int min) {

        this.id = day + " " + startTime + " " + (isGameSlot ? "game" : "practice");

        this.printString = day + ", " + startTime;

        this.isGameSlot = isGameSlot;
        this.day = day;
        this.startTime = timeToMin(startTime);

        // No games can be scheduled at this time due to league wide admin meeting, so set any such game slot to max 0 games
        if (isGameSlot && day.equals("TU") && startTime.equals("11:00")) {
            this.max = 0;
        } else {
            this.max = max;
        }

        this.min = min;
    }

    /*
     * Returns the identifier of the slot
     */
    public String getId() {
        return id;
    }

    /*
     * Returns true if this slot is a game slot
     */
    public boolean isGameSlot() {
        return isGameSlot;
    }

    /*
     * Returns true if this slot is a practice slot
     */
    public boolean isPracticeSlot() {
        return !isGameSlot;
    }

    /*
     * Returns the type of the slot (game or practice)
     */
    public String getType() {
        return isGameSlot ? "game" : "practice";
    }

    /*
     * Returns the day of the slot
     */
    public String getDay() {
        return day;
    }

    /*
     * Returns the start time of the slot (in minutes since midnight).
     */
    public int getStartTime() {
        return startTime;
    }

    /*
     * Returns the end time of the slot (in minutes since midnight).
     */
    public int getEndTime() {
        return startTime + duration();
    }

    /*
     * Returns the duration of the slot in minutes
     */
    public int duration() {

        if (day.equals("TU") && isGameSlot()) {
            return 90;
        }

        else if (day.equals("FR") && isPracticeSlot()) {
            return 120;
        }

        return 60;
    }

    /*
     * Converts a time string to minutes since midnight
     */
    public static int timeToMin(String time) {

        String[] parts = time.split(":");

        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        return hour * 60 + minute;

    }

    /*
     * Returns the maximum number of events that can be scheduled in this slot (hard constraint)
     */
    public int getMax() {

        // This is when the meeting happens, disallowing any games to occur at this time
        if (isGameSlot && day.equals("TU") && (startTime == timeToMin("11:00"))) {
            return 0;
        }

        return max;
    }

    /**
     * Returns the minimum number of events that can be scheduled in this slot (soft constraint)
     * 
     * @return the minimum number of events
     */
    public int getMin() {
        return min;
    }

    /**
     * Returns true if this slot overlaps with another slot.
     * 
     * @param other the other slot to compare
     * @return true if the slots overlap
     */
    public boolean overlaps(Slot other) {

        if (other == null) {
            return false;
        }

        // If this is the same object, it overlaps
        if (this.equals(other)) {
            return true;
        }

        // Otherwise, we will check if the day and time overlap
        boolean dayOverlaps = false;
        boolean timeOverlaps = false;

        // Check if day overlaps
        if (this.getDay().equals(other.getDay())) {
            dayOverlaps = true; // the same day overlaps itself always
        } else if (this.getDay().equals("MO") && this.isPracticeSlot() && other.getDay().equals("FR")) {
            dayOverlaps = true; // 
        } else if (other.getDay().equals("MO") && other.isPracticeSlot() && this.getDay().equals("FR")) {
            dayOverlaps = true;
        }

        // Check if time overlaps
        if (other.getStartTime() <= this.getStartTime() && this.getStartTime() < other.getEndTime()) {
            timeOverlaps = true;
        } else if (this.getStartTime() <= other.getStartTime() && other.getStartTime() < this.getEndTime()) {
            timeOverlaps = true;
        }

        return dayOverlaps && timeOverlaps;
    }

    /*
     * Returns true if the slot is in the evening (after 6:00 PM).
     */
    public boolean isEvening() {
        return startTime >= timeToMin("18:00");
    }

    /*
     * Returns true if the slot is a special practice slot (Tuesday at 6:00 PM).
     */
    public boolean isSpecialPracticeSlot() {
        return day.equals("TU") && isPracticeSlot() && startTime == timeToMin("11:00");
    }

    /*
     * Generates a hash code for the slot from its id.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /*
     * Returns the string representation of the slot.
     */
    @Override
    public String toString() {
        return printString;
    }

}
