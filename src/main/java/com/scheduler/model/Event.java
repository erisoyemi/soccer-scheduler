/*
*    Team Smoliv
*    Cole Briggs, Chris Axten, Erioluwa Soyemi, Grace Kelly Osena
*    CPSC 433 F24
*/

package com.scheduler.model;

import java.util.HashMap;
import java.util.HashSet;

import com.scheduler.Debug;

/**
 * Represents an event in the soccer league scheduler.
 * Each event is either a game or practice.
 */
public class Event {

    /*
     * The identifier of the event.
     */
    private final String id;

    /*
     * The league of the event.
     */
    private final Division division;

    /*
     * True if the event is a game, false if it is a practice.
     */
    private final boolean isGame;

    public final int printTabs;

    /*
     * A list of events that are not compatible with this event.
     */
    private final HashSet<Event> incompatibleEvents;

    /*
     * A list of dates that are unwanted for this event.
     */
    private final HashSet<Slot> unwantedSlots;

    /*
     * A list of slot preferences for this event.
     */
    private final HashMap<Slot, Integer> preferences;

    /*
     * A list of events that are paired with this event.
     */
    private final HashSet<Event> pairs;

    /*
     * Constructor for the Event class.
     * 
     * @param id the identifier of the event
     * @param isGame true if the event is a game, false if it is a practice
     * @throws Exception if the event identifier is invalid format for the type of event
     */
    public Event(String id, boolean isGame) throws Exception {

        // Parts of string, split by spaces in id
        String[] idParts = id.split("\\s+");

        if (idParts.length < 2) {
            throw new Exception("Invalid event identifier: " + id);
        }

        // Break down the id into league, tier, and div
        String league = idParts[0];
        String tier = idParts[1];
        String div;

        // Check format depending on event type
        // If game, check the one possible valid format
        if (isGame) {
            if (idParts.length == 4 && idParts[2].equals("DIV")) {
                div = idParts[3];
                printTabs = 2;
            }
            else {
                throw new Exception("Invalid game identifier: " + id);
            }
        }
        // If practice, check the three possible valid formats
        else {
            // If it is 6 parts, then the 3rd part must be "DIV"
            if (idParts.length == 6 && idParts[2].equals("DIV")) {
                div = idParts[3];
                printTabs = 1;
            }

            // If it is 4 parts, the 3rd part must not be "DIV" as this is for all divs
            else if (idParts.length == 4 && !idParts[2].equals("DIV")) {
                div = "all";
                printTabs = 2;
            }

            // If it is 2 parts, then it must be a special case for CMSA U12T1S and U13T1S
            else if (idParts.length == 2 && league.equals("CMSA") && (tier.equals("U12T1S") || tier.equals("U13T1S"))) {
                div = "all";
                printTabs = 3;
            }

            else {
                throw new Exception("Invalid practice identifier: " + id);
            }
        }

        this.division = new Division(league, tier, div);
        this.id = id;
        this.isGame = isGame;
        this.incompatibleEvents = new HashSet<>();
        this.unwantedSlots = new HashSet<>();
        this.preferences = new HashMap<>();
        this.pairs = new HashSet<>();
    }

    /*
     * Returns the identifier of the event.
     */
    public String getId() {
        return id;
    }

    /*
     * Returns the division of the event.
     */
    public Division getDivision() {
        return division;
    }

    /*
     * Returns true if the event is a game, false if it is a practice.
     */
    public boolean isGame() {
        return isGame;
    }

    /*
     * Returns true if the event is a practice, false if it is a game.
     */
    public boolean isPractice() {
        return !isGame;
    }

    /*
     * Returns the String type of the event (game or practice).
     */
    public String getEventType() {
        return isGame ? "game" : "practice";
    }

    /*
     * Adds an event to the list of events that are not compatible with this event.
     * 
     * @param otherEvent the other event
     */
    public void setIncompatible(Event otherEvent) {
        if (!otherEvent.equals(this)) {
            incompatibleEvents.add(otherEvent);

            if (!otherEvent.getIncompatibleEvents().contains(this)) {
                otherEvent.setIncompatible(this);
            }

        }
    }

    /*
     * Get HashSet of all other events that are not compatible with this event
     */
    public HashSet<Event> getIncompatibleEvents() {
        return incompatibleEvents;
    }

    /*
     * Returns true if the event is compatible with the other event.
     * 
     * @param otherEvent the other event
     */
    public boolean isIncompatible(Event otherEvent) {
        return incompatibleEvents.contains(otherEvent);
    }

    /*
     * Testing function to print the events that are not compatible with this event.
     */
    public void printIncompatibleEvents() {
        System.out.println("Event " + id + " is not compatible with the following events:");
        for (Event event : incompatibleEvents) {
            System.out.println(event);
        }
        System.out.println("\n");
    }

    /*
     * Adds a date to the list of dates that are unwanted for this event.
     * 
     * @param unwantedDate the date that is unwanted
     */
    public void setUnwanted(Slot slot) {
        unwantedSlots.add(slot);
    }

    /*
     * Get HashSet of all dates that are unwanted for this event
     */
    public HashSet<Slot> getUnwanteds() {
        return unwantedSlots;
    }

    /*
     * Returns true if the date is unwanted for this event.
     * 
     * @param date the date to check
     */
    public boolean isUnwanted(Slot slot) {
        return unwantedSlots.contains(slot);
    }

    /*
     * Testing function to print the dates that are unwanted for this event.
     */
    public void printUnwantedSlots() {
        for (Slot slot : unwantedSlots) {
            System.out.println(this + " is unwanted on " + slot);
        }
        System.out.println("\n");
    }

    /*
     * Sets a slot preference for the event.
     * 
     * @param slot the slot to set a preference for
     * @param weight the weight of the preference
     */
    public void setPreference(Slot slot, int weight) {
        preferences.put(slot, weight);
    }
        public void printPreferences() {
        System.out.println("Event " + id + " has the following preferences:");
        for (Slot slot : preferences.keySet()) {
            System.out.println(slot.getDay() + " at " + slot.getStartTime() + " with weight " + preferences.get(slot));
        }
        System.out.println("\n");
    }

    /**
     * Gets the map of preferences for this event.
     *
     * @return  The HashMap of preference values for each slot for this event.
     */
    public HashMap<Slot, Integer> getPreferences() {
        return preferences;
    }

    /*
     * Sets an event pair for the event.
     * 
     * @param otherEvent the other event to set as a pair
     */
    public void setPair(Event otherEvent) {
        pairs.add(otherEvent);

        if (!otherEvent.getPairs().contains(this)) {
            otherEvent.setPair(this);
        }
    }
        public void printPairs() {
        System.out.println("Event " + id + " is paired with the following events:");

        for (Event event : pairs) {
            System.out.println(event);
        }
        System.out.println("\n");
    }

    /*
     * Get HashSet of all other events that this event is paired with
     */
    public HashSet<Event> getPairs() {
        return pairs;
    }

    /*
     * Returns true if the event is for the same division as the other event.
     * 
     * @param otherEvent the other event
     */
    public boolean sameDivision(Event otherEvent) {
        return division.same(otherEvent.getDivision());
    }

    /*
     * Returns true if the event needs to be scheduled in the evening.
     */
    public boolean isEvening() {
        return division.isEvening();
    }

    /*
     * Returns true if the event is for U15 to U19 divisions.
     */
    public boolean isU15toU19() {
        return division.isU15toU19();
    }

    /*
     * Returns true if the event is for U12 to U14 divisions.
     */
    public boolean isSpecialPractice() {
        if (division.getLeague().equals("CMSA") && (division.getTier().equals("U12T1S") || division.getTier().equals("U13T1S"))) {
            return true;
        }

        return false;
    }

    public boolean isCompatible(Slot slot) {

        // Check if slot is unwanted
        if (isUnwanted(slot)) {

            Debug.msg("\t\t\tEvent " + id + " is unwanted on " + slot);

            return false;
        }

        // Check if slot is game or practice
        if (isGame() != slot.isGameSlot()) {

            Debug.msg("\t\t\tEvent " + id + " is a " + getEventType() + " and slot is a " + slot.getType());

            return false;
        }

        // Check if slot is evening if the event is evening
        if (isEvening() && !slot.isEvening()) {

            Debug.msg("\t\t\tEvent " + id + " is an evening event and slot is not evening");

            return false;
        }

        if (isSpecialPractice() && !slot.isSpecialPracticeSlot()) {

            Debug.msg("\t\t\tEvent " + id + " is a special practice and slot is not a special practice slot");

            return false;
        }


        return true;

    }

    /*
     * Returns a string representation of the event.
     */
    @Override
    public String toString() {
        return id;
    }

    /*
     * Returns true if the event is equal to the other object.
     */
    @Override
    public boolean equals(Object obj) {
        
        if (obj instanceof Event other) {
            return id.equals(other.id);
        }
        
        return false;
    }

    /*
     * Returns the hash code of the event.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
