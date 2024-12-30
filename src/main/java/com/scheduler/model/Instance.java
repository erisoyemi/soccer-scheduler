/*
*    Team Smoliv
*    Cole Briggs, Chris Axten, Erioluwa Soyemi, Grace Kelly Osena
*    CPSC 433 F24
*/

package com.scheduler.model;

import java.util.HashMap;
import java.util.HashSet;

import com.scheduler.Debug;

/*
 * Represents an instance of the soccer league scheduling problem.
 * An instance contains all the information needed to schedule games and practices.
 */
public class Instance {

    private final int wMinFilled;
    private final int wPref;
    private final int wPair;
    private final int wSecDiff;
    private final int penGameMin;
    private final int penPracticeMin;
    private final int penNotPaired;
    private final int penSection;

    /* 
     * All gameSlots and practiceSlots stored in a HashMap
     */
    final private HashMap<String, Slot> slots;

    /* 
     * All games stored in a HashMap
     * key: the event id
     * value: the event object
     */
    final private HashMap<String, Event> events;

    /*
     * The partial assignment of events to slots.
     */
    final private HashMap<Event, Slot> partialAssignment = new HashMap<>();

    /*
     * The partial schedule of the instance.
     */
    final private Schedule partialSchedule;

    private String name;

    /*
     * Constructor for the Instance class.
     */
    public Instance(int wMinFilled, int wPref, int wPair, int wSecDiff, int penGameMin, int penPracticeMin, int penNotPaired, int penSection) {

        Debug.msg("Creating instance with weights: " + wMinFilled + " " + wPref + " " + wPair + " " + wSecDiff + " " + penGameMin + " " + penPracticeMin + " " + penNotPaired + " " + penSection);

        this.wMinFilled = wMinFilled;
        this.wPref = wPref;
        this.wPair = wPair;
        this.wSecDiff = wSecDiff;
        this.penGameMin = penGameMin;
        this.penPracticeMin = penPracticeMin;
        this.penNotPaired = penNotPaired;
        this.penSection = penSection;

        this.slots = new HashMap<>();

        this.events = new HashMap<>();

        this.partialSchedule = new Schedule(this, true);

        Debug.msg("Instance created.");

    }

   
    /* 
     * Get all the HashMap of slots in the instance.
     */
    public HashMap<String, Slot> getSlots() {
        return slots;
    }

    /*
     * Print all the slots in the instance.
     */
    public void printSlots() {

        System.out.println("Slots:");

        for (Slot slot : slots.values()) {
            System.out.println(slot.getId() + " " + slot.getType());
        }

    }

    /* 
     * Get all the HashMap of gameSlots in the instance.
     */
    public HashMap<String, Slot> getGameSlots() {

        HashMap<String, Slot> gameSlots = new HashMap<>();

        for (Slot slot : slots.values()) {
            if (slot.isGameSlot()) {
                gameSlots.put(slot.getId(), slot);
            }
        }

        return gameSlots;
    }

    /* 
     * Get all the HashMap of practiceSlots in the instance.
     */
    public HashMap<String, Slot> getPracticeSlots() {

        HashMap<String, Slot> gameSlots = new HashMap<>();

        for (Slot slot : slots.values()) {
            if (slot.isPracticeSlot()) {
                gameSlots.put(slot.getId(), slot);
            }
        }

        return gameSlots;
    }

    /*
     * Get a slot by its day, time, and type.
     * 
     * @param slotId the identifier of the slot
     * @returns the slot with the given identifier, or null if it does not exist
     */
    public Slot getSlot(String day, String startTime, String type) {

        Slot slot = slots.get(day + " " + startTime + " " + type);

        if (slot == null) {
            System.err.println("Slot " + day + " " + startTime + " " + type + " does not exist.");
        }

        return slot;
    }

    /*
     * Get all the HashMap of all events (keyed by id) in the instance.
     */
    public HashMap<String, Event> getEvents() {
        return events;
    }

    /*
     * Get an event by its identifier.
     * 
     * @param eventId the identifier of the event
     * @returns the event with the given identifier, or null if it does not exist
     */
    public Event getEvent(String eventId) {

        Event event = events.get(eventId);

        if (event == null) {
            System.err.println("Event " + eventId + " does not exist.");
        }

        return event;
    }

    /*
     * Get all the HashMap of all games in the instance.
     */
    public HashMap<String, Event> getGames() {

        HashMap<String, Event> games = new HashMap<>();

        for (Event event : events.values()) {
            if (event.isGame()) {
                games.put(event.getId(), event);
            }
        }

        return games;
    }

    /* 
     * Get all the HashMap of all practices in the instance.
     */
    public HashMap<String, Event> getPractices() {

        HashMap<String, Event> practices = new HashMap<>();

        for (Event event : events.values()) {
            if (event.isPractice()) {
                practices.put(event.getId(), event);
            }
        }

        return practices;
    }

    /*
     * Get the partial schedule of the instance.
     */
    public Schedule getPartialSchedule() {
        return partialSchedule;
    }

    /*
     * Set the name of the instance.
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * Add a slot to the instance.
     * 
     * @param slot the slot to add
     */
    public void addSlot(Slot slot) {
        slots.put(slot.getId(), slot);
    }

    /*
     * Add an event to the instance.
     * 
     * @param event the event to add
     */
    public void addEvent(Event event) {

        // Debug.msg("Adding event " + event.getId());
        
        // If event is already in the instance, print error and don't add it again
        if (events.containsKey(event.getId())) {
            System.err.println("Event " + event.getId() + " already exists.");

        }
        
        // Otherwise, add the event to the instance
        else {
            events.put(event.getId(), event);

            // Check all other events and add incompatibility relationship when necessary
            for (Event otherEvent : events.values()) {

                // If one of these events is a game
                if (event.isGame() || otherEvent.isGame()) {

                    // A game can't be scheduled at same time as any other event in same division
                    if (event.getDivision().same(otherEvent.getDivision())) {
                        addIncompatible(event.getId(), otherEvent.getId());
                    }

                    // Games in the U15, U16, U17, U18, and U19 divisions are incompatible
                    else if (event.isGame() && event.isU15toU19() && otherEvent.isGame() && otherEvent.isU15toU19()) {
                        addIncompatible(event.getId(), otherEvent.getId());
                    }
                }

                // Otherwise both are practices, in which case they are incompatible if they are same league and tier and one is for all divs
                else if ((event.getDivision().getDiv().equals("all") || event.getDivision().getDiv().equals("all")) && event.getDivision().same(otherEvent.getDivision())) {
                    addIncompatible(event.getId(), otherEvent.getId());
                }

            } 

            // If this is a special game CMSA U12T1 or U13T1
            if (event.isGame() && event.getDivision().getLeague().equals("CMSA")) {
                String gameTier = event.getDivision().getTier();
                if (gameTier.equals("U12T1") || gameTier.equals("U13T1")) {

                    // Add the required special practice
                    try {
                        Event specialPractice = new Event("CMSA " + gameTier + "S", false);
                        addEvent(specialPractice);

                        Slot specialSlot = getSlot("TU", "18:00", "practice");

                        if (specialSlot == null) {
                            System.err.println("Error: Required slot does not exist for special practice event " + specialPractice.getId());
                        }

                        partialAssignment.put(specialPractice, specialSlot);

                        boolean success = partialSchedule.assign(specialPractice, specialSlot);

                        if (!success) {
                            System.err.println("Error: Required slot does not exist for special practice event " + specialPractice.getId());
                        }

                    } catch (Exception e) {
                        System.err.println("Error creating special practice event for " + event.getId());
                    }

                }
            }

            // Finally, put the event into the HashMap of all events
            events.put(event.getId(), event);
        }
    }

    /*
     * Add a not-compatible relationship between two events.
     * 
     * @param event1Id the identifier of the first event
     * @param event2Id the identifier of the second event
     */
    public void addIncompatible(String event1Id, String event2Id) {

        Event event1 = getEvent(event1Id);
        Event event2 = getEvent(event2Id);

        // Ignore and return if either event is null
        if (event1 == null || event2 == null) return;

        event1.setIncompatible(event2);
        event2.setIncompatible(event1);
    }
        public void printIncompatibiles() {

            for (Event event : events.values()) {
                event.printIncompatibleEvents();
            }

        }

    /*
     * Add an unwanted date to an event in the instance.
     * 
     * @param eventId the identifier of the event
     * @param date the date of the unwanted slot
     * @param time the time of the unwanted slot
     */
    public void addUnwanted(String eventId, String day, String startTime) {

        // Get the event and only proceed if it is not null
        Event event = getEvent(eventId);
        if (event == null) return;

        Slot slot;

        // Ensure that slot type matches
        if (event.isGame()) {
            slot = getGameSlots().get(day + " " + startTime + " game");
        } else {
            slot = getPracticeSlots().get(day + " " + startTime + " practice");
        }

        // Only add unwanted slot if it exists in the instance
        if (slot != null) {
            event.setUnwanted(slot);
        }

    }

        public void printUnwanteds() {

            System.out.println("Unwanted:");

            for (Event event : events.values()) {
                event.printUnwantedSlots();
            }

        }   

    /*
     * Add a preference for a timeslot to an event in the instance.
     * 
     * 
     */
    public void addPreference(String eventId, String day, String startTime, int weight) {

        Event event = getEvent(eventId);
        if (event == null) return;

        Slot slot;

        // Ensure that slot type matches
        if (event.isGame()) {
            slot = getGameSlots().get(day + " " + startTime + " game");
        } else {
            slot = getPracticeSlots().get(day + " " + startTime + " practice");
        }

        // Only add preference if slot exists in the instance
        if (slot != null) {
            event.setPreference(slot, weight);
        }
    }

        public void printPreferences() {

            for (Event event : events.values()) {
                event.printPreferences();
            }

        }

    /*
     * Add a pair relationship between two events in the instance
     * 
     * @param event1Id the identifier of the first event
     * @param event2Id the identifier of the second event
     */
    public void addPair(String event1Id, String event2Id) {

        Event event1 = getEvent(event1Id);
        Event event2 = getEvent(event2Id);

        // Only add pair if both events exist
        if (event1 != null && event2 != null) {
            event1.setPair(event2);
            event2.setPair(event1);
        }
    }
        public void printPairs() {

            for (Event event : events.values()) {
                event.printPairs();
            }

        }

    /*
     * Add a partial assignment of an event to a slot in the instance.
     * 
     * @param eventId the identifier of the event
     * @param day the day of the slot
     * @param startTime the start time of the slot
     */
    public void addPartialAssignment(String eventId, String day, String startTime) throws Exception {

        // Get event and return with error message if null
        Event event = getEvent(eventId);
        if (event == null) {
            throw new Exception("Failed to add partial assignment. Event " + eventId + " does not exist.");
        }
        
        // Get slot and return with error message if null
        Slot slot = getSlot(day, startTime, event.isGame() ? "game" : "practice");
        if (slot == null) {
            throw new Exception("Failed to add partial assignment." + "No " + event.getEventType() + " slot on " + day + " at " + startTime + " found.");
        }

        if (partialAssignment.containsKey(event)) {
            throw new Exception("Failed to add partial assignment. Event " + eventId + " already has a partial assignment.");
        }

        partialAssignment.put(event, slot);

        boolean success = partialSchedule.assign(event, slot);

        if (!success) {
            System.err.println("Partial assignment failure. Failed to assign " + event.getId() + " to " + slot.getId());
        }
    }

    /*
     * Get list of all slots that are compatible with a given event.
     * 
     * NOTE: this does not consider any incompatibly assigned events or the fullness of the slots
     * It returns slots that pass the following criteria:
     * - The slot is not unwanted by this event
     * - The slot type is correct (game vs practice)
     * - The slot is in evening if necessary
     * - IF SPECIAL EVENT, the slot is 18:00 TU as necessary
     * 
     * @param event the event to check compatibility for
     * @returns a HashSet of all slots that are compatible with the event
     */
    public HashSet<Slot> getCompatibleSlots(Event event) {

        HashSet<Slot> compatibleSlots = new HashSet<>();

        for (Slot slot : slots.values()) {
            if (event.isCompatible(slot)) {
                compatibleSlots.add(slot);
            }
        }

        return compatibleSlots;
    }
  
    public int getWMinFilled() {
        return wMinFilled;
    }

    public int getWPref() {
        return wPref;
    }

    public int getWPair() {
        return wPair;
    }

    public int getWSecDiff() {
        return wSecDiff;
    }

    public int getPenGameMin() {
        return penGameMin;
    }

    public int getPenPracticeMin() {
        return penPracticeMin;
    }

    public int getPenNotPaired() {
        return penNotPaired;
    }

    public int getPenSection() {
        return penSection;
    }

    @Override
    public String toString() {
        // print in same format as input file
        StringBuilder sb = new StringBuilder();

        sb.append("Name:\n");
        sb.append(name).append("\n");


        sb.append("\nGame Slots:\n");
        for (Slot slot : getGameSlots().values()) {
            sb.append(slot.toString()).append("\n");
        }

        sb.append("\nPractice Slots:\n");
        for (Slot slot : getPracticeSlots().values()) {
            sb.append(slot.toString()).append("\n");
        }

        sb.append("\nGames:\n");
        for (Event event : getGames().values()) {
            sb.append(event.toString()).append("\n");
        }

        sb.append("\nPractices:\n");
        for (Event event : getPractices().values()) {
            sb.append(event.toString()).append("\n");
        }

        sb.append("\nUnwanted:\n");
        for (Event event : events.values()) {
            for (Slot slot : event.getUnwanteds()) {
                sb.append(event.getId()).append(" ").append(slot.getId()).append("\n");
            }
        }

        sb.append("\nPreferences:\n");
        for (Event event : events.values()) {
            for (Slot slot : event.getPreferences().keySet()) {
                sb.append(event.getId()).append(" ").append(slot.getId()).append(" ").append(event.getPreferences().get(slot)).append("\n");
            }
        }

        sb.append("\nPairs:\n");
        for (Event event : events.values()) {
            for (Event pair : event.getPairs()) {
                sb.append(event.getId()).append(" ").append(pair.getId()).append("\n");
            }
        }

        sb.append("\nPartial Assignments:\n");
        for (Event event : partialAssignment.keySet()) {
            Slot slot = partialAssignment.get(event);
            sb.append(event.getId()).append(" ").append(slot.getId()).append("\n");
        }
        
        
        return sb.toString();

    }


    public void printPartialSchedule() {

        for (Event event : partialAssignment.keySet()) {
            Slot slot = partialAssignment.get(event);
            System.out.println(event.getId() + " " + slot.getId());
        }
    }
}
