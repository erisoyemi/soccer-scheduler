/*
*    Team Smoliv
*    Cole Briggs, Chris Axten, Erioluwa Soyemi, Grace Kelly Osena
*    CPSC 433 F24
*/

package com.scheduler.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.scheduler.Debug;

/*
 * Represents a schedule of events for a soccer league.
 */
public class Schedule {

    /*
     * The instance the schedule is for.
     */
    private final Instance instance;

    /*
     * HashMap used to track the slot that each event is assigned to.
     */
    private final HashMap<Event, Slot> schedule;

    /*
     * HashMap used to track all events assigned to each slot.
     */
    private final HashMap<Slot, HashSet<Event>> slotContents;

    /*
     * Is this schedule a partial assignment?
     */
    private final boolean isPartialAssignment;

    private int evalValue;

    /*
     * Constructor for the Schedule class.
     * 
     * @param instance the instance to create the schedule for
     */
    public Schedule(Instance instance, boolean isPartialAssignment) {

        evalValue = -1;

        this.instance = instance;
        this.isPartialAssignment = isPartialAssignment;

        schedule = new HashMap<>();

        slotContents = new HashMap<>();

        for (Slot slot : instance.getSlots().values()) {
            slotContents.put(slot, new HashSet<>());
        }
        
    }

    /*
     * Constructor for the Schedule class that defaults to a non-partial assignment.
     */
    public Schedule(Instance instance) {
        this(instance, false);
    }

    /*
     * Get the schedule HashMap of the schedule.
     */
    public HashMap<Event, Slot> getSchedule() {
        return schedule;
    }

    /*
     * Get the instance of the schedule.
     */
    public HashSet<Event> getAllEvents() {
        return new HashSet<>(instance.getEvents().values());
    }

    /*
     * Get all events that have been assigned.
     */
    public HashSet<Event> getAssignedEvents() {

        HashSet<Event> assignedEvents = new HashSet<>();

        // Get all assigned events
        for (Event event : schedule.keySet()) {
            assignedEvents.add(event);
        }

        if (!isPartialAssignment) {
            // Also include partial assignment
            for (Event event : instance.getPartialSchedule().getAssignedEvents()) {
                assignedEvents.add(event);
            }
        }

        return assignedEvents;
    }

    /*
     * Get all events that have not yet been assigned to a slot.
     */
    public HashSet<Event> getUnassignedEvents() {

        Debug.msg4("Getting unassigned events...");

        ArrayList<Event> unscheduledEvents = new ArrayList<>(getAllEvents());

        int size = unscheduledEvents.size();

        Debug.msg4("size: " + size);

        for (int i = 0; i < size; i++) {

            Event event = unscheduledEvents.get(i);

            if (instance.getPartialSchedule().getSchedule().containsKey(event)) {
                unscheduledEvents.remove(event);
                size--;
            }

        }

        unscheduledEvents.removeAll(getAssignedEvents());

        return new HashSet<>(unscheduledEvents);
    }

    /*
     * Get the next event to schedule.
     */
    public ArrayList<Event> getSchedulingOrder() {

        HashSet<Event> eveningGames = new HashSet<>();

        HashSet<Event> eveningPractices = new HashSet<>();

        HashSet<Event> remainingGames = new HashSet<>();

        HashSet<Event> remainingPractices = new HashSet<>();

        ArrayList<Event> unassignedEvents = new ArrayList<>(getUnassignedEvents());

        for (int i = 0; i < unassignedEvents.size(); i++) {

            Event event = unassignedEvents.get(i);

            Debug.msg5("putting event in order: " + event);

            if (event.isEvening()) {
                if (event.isGame()) {
                    eveningGames.add(event);
                } else {
                    eveningPractices.add(event);
                }
            } else {
                if (event.isGame()) {
                    remainingGames.add(event);
                } else {
                    remainingPractices.add(event);
                }
            }
            
        }

        ArrayList<Event> eventOrder = new ArrayList<>();

        eventOrder.addAll(eveningGames);
        eventOrder.addAll(eveningPractices);
        eventOrder.addAll(remainingGames);
        eventOrder.addAll(remainingPractices);

        return eventOrder;
    }

    /*
     * Get a random reassignable (mutable) event.
     */
    public Event getRandomMutableEvent() {

            ArrayList<Event> assignedEvents = new ArrayList<>(getAssignedEvents());
            
            if (assignedEvents.size() < 0) {
                System.err.println("Error: Attempted to get mutable event but no mutable events are assigned yet.");
                return null;
            } if (isPartialAssignment) {
                System.err.println("Error: Attempted to get mutable events from a partial assignment.");
                return null;
            }

            Collections.shuffle(assignedEvents);
            int originalSize = assignedEvents.size();

            for (int i = 0; i < originalSize; i++) {

                Event event = assignedEvents.get(i);

                if (!instance.getPartialSchedule().getSchedule().containsKey(event)) {
                    return event;
                }

            }

            System.err.println("Error: Attempted to get mutable event but no mutable events could be found.");
            return null;

    }

    /*
     * Get all events that have been assigned to a given slot.
     * 
     * @param slot the slot to get the events for
     * @returns a HashSet of events assigned to the slot
     */
    public HashSet<Event> getEventsFromSlot(Slot slot) {

        HashSet<Event> events = new HashSet<>();

        // Add events from the slot to the list
        if (slotContents.containsKey(slot)) {
            events.addAll(slotContents.get(slot));
        }

        // If this is not a partial assignment, also include all events from the partial schedule
        if (!isPartialAssignment) {
            
            HashSet<Event> paEvents = instance.getPartialSchedule().getEventsFromSlot(slot);

            if (paEvents != null) {
                events.addAll(paEvents);
            }

        }

        return events;
    }

    /*
     * Get the slot that an event is assigned to.
     * 
     * @param event the event to get the slot for
     * @returns the slot the event is assigned to
     */
    public Slot getSlotFromEvent(Event event) {

        // If event is in this schedule, return the slot
        if (schedule.containsKey(event)) {
            return schedule.get(event);
        }

        // If this is not a partial assignment, also check the partial schedule
        if (!isPartialAssignment) {
            return instance.getPartialSchedule().getSlotFromEvent(event);
        }

        return null;
    }

    /*
     * Get all slots.
     */
    public HashSet<Slot> getAllSlots() {
        return new HashSet<>(instance.getSlots().values());
    }

    /*
     * Get all game slots.
     */
    public HashSet<Slot> getGameSlots() {

        HashSet<Slot> gameSlots = new HashSet<>();

        for (Slot slot : getAllSlots()) {
            if (slot.isGameSlot()) {
                gameSlots.add(slot);
            }
        }
        return gameSlots;
    }

    /*
     * Get all practice slots.
     */
    public HashSet<Slot> getPracticeSlots() {

        HashSet<Slot> practiceSlots = new HashSet<>();

        for (Slot slot : getAllSlots()) {
            if (slot.isPracticeSlot()) {
                practiceSlots.add(slot);
            }
        }
        return practiceSlots;
    }

    /*
     * Get all slots that are not yet full.
     */
    public HashSet<Slot> getAllAvailableSlots() {

        HashSet<Slot> availableSlots = new HashSet<>();

        // Add available slots to the list
        for (Slot slot : getAllSlots()) {
            if (slotAvailable(slot)) {
                availableSlots.add(slot);
            }
        }

        // Return list of available slots
        return availableSlots;
    }

    /*
     * Get all game slots that are not yet full.
     */
    public HashSet<Slot> getAvailableGameSlots() {

        HashSet<Slot> availableGameSlots = new HashSet<>();

        // Add available game slots to the list
        for (Slot slot : getGameSlots()) {
            if (slotAvailable(slot)) {
                availableGameSlots.add(slot);
            }
        }

        // Return list of available game slots
        return availableGameSlots;
    }

    /*
     * Get all practice slots that are not yet full.
     */
    public HashSet<Slot> getAvailablePracticeSlots() {

        HashSet<Slot> availablePracticeSlots = new HashSet<>();

        // Add available practice slots to the list
        for (Slot slot : getPracticeSlots()) {
            if (slotAvailable(slot)) {
                availablePracticeSlots.add(slot);
            }
        }

        // Return list of available practice slots
        return availablePracticeSlots;
    }
    
    /*
     * Check if a slot is available (not full)
     * 
     * @returns True if the slot is not full, false otherwise.
     * @param slot the slot to check
     */
    public boolean slotAvailable(Slot slot) {

        // If the slot is null, has <1 max, or not in the instance, return false
        if (slot == null || slot.getMax() < 1 || !instance.getSlots().containsValue(slot)) {
            return false;
        }

        // Fetch existing events in the slot
        HashSet<Event> events = getEventsFromSlot(slot);
        
        // If the slot has no record in the slotContents HashMap, it is available
        if (events == null) {
            return true;
        }

        // If the slot has not yet reached its max, return true
        else if (events.size() < slot.getMax()) {
            return true;
        }

        // Otherwise, return false
        return false;
    }

    /*
     * Get all slots that are compatible with an event.
     * 
     * @param event the event to get compatible slots for
     * @returns a HashSet of slots that are compatible with the event
     */
    public HashSet<Slot> getCandidateSlots(Event event) {
            
            HashSet<Slot> candidateSlots = new HashSet<>();

            HashSet<Slot> typeSlots = event.isGame() ? getGameSlots() : getPracticeSlots();

            for (Slot slot : typeSlots) {

                Debug.msg("Checking candidacy of slot: " + slot);

                Debug.msg("\t\tevent: " + event);

                if (event.isCompatible(slot)) {

                    Debug.msg("Slot is compatible.");

                    if (isPotentialAssignmentValid(event, slot)) {
                        Debug.msg("Slot is valid.");
                        candidateSlots.add(slot);

                    } else {
                        Debug.msg("Slot is not valid.");
                    }
                }
            }
    
            return candidateSlots;
    }

    /*
     * Gets a random compatible slot for an event.
     * 
     * @param event the event to get a slot for
     * @returns a random compatible slot for the event
     */
    public Slot getRandomCandidateSlot(Event event) {

        Debug.msg("Getting random candidate slot for event: " + event);

        HashSet<Slot> compatibleSlots = getCandidateSlots(event);

        if (!compatibleSlots.isEmpty()) {


            int randomIndex = (int) (Math.random() * compatibleSlots.size());

            Slot randomSlot = (Slot) compatibleSlots.toArray()[randomIndex];

            Debug.msg("Random candidate slot: " + randomSlot);

            return randomSlot;
        }

        Debug.msg("No compatible slots found for event: " + event);
        return null;
    }

    /*
     * Assign an event to a slot. Specify whether the assignment must be valid.
     * 
     * @returns True if the assignment was successful, false otherwise.
     * 
     * @param event the event to assign
     * @param slot the slot to assign the event to
     * @param requireValid true if the assignment must be valid, false if it can be invalid
     */
    public boolean assign(Event event, Slot slot, boolean requireValid) {

        // If slot doesn't exist, fail and return false
        if (!instance.getSlots().containsValue(slot)) {
            Debug.msg4("Error: Attempted to assign an event to a slot that is not in the instance: " + slot);
            return false;
        }
        // If event doesn't exist, fail and return false
        else if (!instance.getEvents().containsValue(event)) {
            Debug.msg4("Error: Attempted to assign an event that is not in the instance. " + event);
            return false;
        }

        // Ensure the slot is initialized in the slotContents HashMap
        slotContents.putIfAbsent(slot, new HashSet<>());

        // If the event is already in a slot, remove it from that previous slot before assigning it to the new one
        Slot prevSlot = schedule.get(event);
        if (prevSlot != null) {
            slotContents.get(prevSlot).remove(event);
        }

        // If this won't result in a valid assignment, then undo the changes and return
        if (requireValid && !isPotentialAssignmentValid(event, slot)) {

            // Undo changes by reassigning back to previous slot if necessary
            if (prevSlot != null) {
                slotContents.get(prevSlot).add(event);
            }

            // Return false
            return false;
        }

        // Otherwise, if all checks pass, add the event to the slot and return true
        schedule.put(event, slot);
        slotContents.get(slot).add(event);
        return true;
    }

    /*
     * Assign an event to a slot (only permit if it is a valid assignment).
     * 
     * @returns True if the assignment was successful, false otherwise.
     * 
     * @param event the event to assign
     * @param slot the slot to assign the event to
     */
    public boolean assign(Event event, Slot slot) {
        return assign(event, slot, true);
    }

    /*
     * Clear an event's assignment so it is no longer assigned to any slot.
     * 
     * @param event the event to unassign
     */
    public void clearAssignment(Event event) {
            
            // Get the slot the event is assigned to
            Slot slot = schedule.get(event);
    
            // If the event is assigned to a slot, remove it from the slot
            if (slot != null) {
                slotContents.get(slot).remove(event);
            }
    
            // Remove the event from the schedule
            schedule.remove(event);
    }

    /*
     * Returns true if the potential assignment won't cause any conflicts.
     */
    public boolean isPotentialAssignmentValid(Event event, Slot potentialSlot) {
    
            // Check if event is scheduled in a slot of the correct type (game vs practice slot)
            if (event.isGame() != potentialSlot.isGameSlot()) {

                Debug.msg("Validity check failed: Game/Practice mismatch.");

                return false;
            }

            // Check if event is scheduled in an evening slot if necessary
            if (event.isEvening() && !potentialSlot.isEvening()) {

                Debug.msg("Validity check failed: Evening mismatch.");

                return false;
            }
    
            // Check if slot is overfilled past max
            if (slotContents.get(potentialSlot).size() >= potentialSlot.getMax()) {

                Debug.msg("Validity check failed: Slot is full.");

                Debug.msg("Slot: " + potentialSlot + " \t\t# of Events: " + slotContents.get(potentialSlot).size() + " \t\tMax: " + potentialSlot.getMax());
                //Slot contents
                for (Event e : slotContents.get(potentialSlot)) {
                    Debug.msg("Event: " + e);
                }

                return false;
            }
    
            // For all incompatible events
            for (Event incompatibleEvent : event.getIncompatibleEvents()) {
    
                // Get the slot of the incompatible event
                Slot incompatibleEventSlot = getSlotFromEvent(incompatibleEvent);
    
                // If the incompatible event has been scheduled, return whether their slots overlap
                if (incompatibleEventSlot != null) {

                    boolean incompatibleOverlap = incompatibleEventSlot.overlaps(potentialSlot);

                    if (incompatibleOverlap) {

                        Debug.msg("Validity check failed: Incompatible overlap with " + incompatibleEvent + " (in slot " + incompatibleEventSlot + ")");

                        return false;
                    }
                }

            }

            // If none of those issues were found for any of the events, return true because the schedule would be valid
            return true;
    }

    /*
     * Returns true if all scheduled events are not conflicting.
     */
    public boolean valid() {

        // Check each event that has been scheduled
        for (Event event : schedule.keySet()) {

            // Get slot
            Slot slot = schedule.get(event);

            // Check if event is scheduled in a slot of the correct type (game vs practice slot)
            if (event.isGame() != slot.isGameSlot()) {

                Debug.msg("Validity check failed: Game/Practice mismatch for " + event + " in " + slot);

                return false;
            }

            // Check if event is scheduled in evening (if necessary)
            if (event.isEvening() && !slot.isEvening()) {

                Debug.msg("Validity check failed: Evening mismatch for " + event + " in " + slot);

                return false;
            }

            // Check if slot is overfilled past max
            if (slotContents.get(slot).size() > slot.getMax()) {

                Debug.msg("Validity check failed: Slot is overfilled for " + event + " in " + slot);

                return false;
            }

            // For all incompatible events
            for (Event incompatibleEvent : event.getIncompatibleEvents()) {

                // Get the slot of the incompatible event
                Slot incompatibleEventSlot = schedule.get(incompatibleEvent);

                if (incompatibleEventSlot != null) {
                    // If they overlap, return false as this is not valid
                    if (incompatibleEventSlot.overlaps(slot)) {

                        Debug.msg("Validity check failed: Incompatible overlap with " + incompatibleEvent + " in " + incompatibleEventSlot + " for " + event + " in " + slot);

                        return false;
                    }
                }

            }
        }

        // If none of those issues were found for any of the events, return true because the schedule is valid
        return true;

    }

    /*
     * Returns true if all events in the instance have been scheduled.
     */
    public boolean complete() {

        // For each event in the instnace
        for (Event event : instance.getEvents().values()) {

            // If the event is not in the schedule, return false
            if (!schedule.containsKey(event) && !instance.getPartialSchedule().getSchedule().containsKey(event)) {
                return false;
            }
        }

        // If none of the events were missing, return true
        return true;
    }

    /*
     * Evaluate the schedule based on the instance's evaluation criteria.
     */
    public int eval() {

        if (evalValue < 0) {

            evalValue = evalMinFilled() * instance.getWMinFilled()
            + evalPref()      * instance.getWPref()
            + evalPair()      * instance.getWPair()
            + evalSecDiff()   * instance.getWSecDiff();
 
        }

        Debug.msg("Evaluating schedule...");

        return evalValue;
    }

    /**
     * Calculates and returns the penalty for any slots with fewer than
     * it's minimum slots, by summing the appropriate penalty for each
     * game or practice below the minimum in a given slot.
     *
     * @return  The penalty accrued by slots with less than their minimum
     *          events.
     */
    private int evalMinFilled() {

        int penalty = 0;

        for (Slot slot : getAllSlots()) {

            HashSet<Event> slotEvents = getEventsFromSlot(slot);

            if (slotEvents.size() < slot.getMin()) {

                Debug.msg("Slot under minimum: " + slot + " \t\t# of Events assigned: " + slotEvents.size() + " \t\tMin: " + slot.getMin());

                if (slot.isGameSlot()) {
                    penalty += (slot.getMin() - slotEvents.size())
                                * instance.getPenGameMin();
                } else {
                    penalty += (slot.getMin() - slotEvents.size())
                                * instance.getPenPracticeMin();
                }
            }
        }

        return penalty;
    }

    /**
     * Calculates and returns the penalty for the preferences for each
     * event.
     *
     * @return  The penalty accrued by all events for each slot it has
     *          a preference for, that its not assigned to.
     */
    private int evalPref() {

        int penalty = 0;

        for (Event event : getAllEvents()) {

            for (Slot slot : event.getPreferences().keySet()) {

                if (slot != getSlotFromEvent(event)) {

                    Debug.msg("Preference not fulfilled: Event: " + event + " Slot: " + slot + " Pref: " + event.getPreferences().get(slot));

                    penalty += (event.getPreferences().get(slot));

                }
            }
        }

        return penalty;
    }

    /**
     * Calculates and returns the penalty for every not paired pair
     * of events.
     *
     * @return  The penalty accrued for each unpaired pair of events.
     */
    private int evalPair() {

        int penalty = 0;

        HashSet<HashSet<Event>> exploredPairs = new HashSet<>();

        for (Event event : getAllEvents()) {

            for (Event pairedEvent : event.getPairs()) {

                HashSet<Event> pair = new HashSet<>();

                pair.add(event);
                pair.add(pairedEvent);

                // check if we already checked this pair
                if (!exploredPairs.contains(pair)) {

                    // if we haven't, add it to the explored pairs
                    exploredPairs.add(pair);

                    // then, if they don't overlap, add the penalty
                    if (!getSlotFromEvent(event).overlaps(getSlotFromEvent(pairedEvent))) {
                        penalty += instance.getPenNotPaired();

                        Debug.msg("Pair not paired: " + event + " (" + getSlotFromEvent(event) + ") and " + pairedEvent + " (" + getSlotFromEvent(pairedEvent) + ")");
                    }
                }
            }
        }
        return penalty;
    }

    /**
     * Calculates and returns the penalty for overlapping games of
     * different divisions of the same tier.
     *
     * @return  The penalty accrued for overlapping division overlap
     *          within tiers.
     */
    private int evalSecDiff() {

        int penalty = 0;

        // First, create a map of sets of events within each tier
        HashMap<String, ArrayList<Event>> tiers = new HashMap<>();
        for (Event event : instance.getEvents().values()) {
            if (!tiers.containsKey(event.getDivision().getTier())) {
                ArrayList<Event> tier = new ArrayList<>();
                tier.add(event);
                tiers.put(event.getDivision().getTier(), tier);
            } else {
                tiers.get(event.getDivision().getTier()).add(event);
            }
        }

        // Loop through every pair of events in each tier
        for (ArrayList<Event> tier : tiers.values()) {
            for (int i = 0; i < tier.size() - 1; i++) {
                for (int j = i+1; j < tier.size(); j++) {
                    if (getSlotFromEvent(tier.get(i)).overlaps(getSlotFromEvent(tier.get(j)))
                        && !tier.get(i).getDivision().same(tier.get(j).getDivision())) {
                        // If events i and j in the tier overlap, but are different
                        // divisions, then add the penalty
                        penalty += instance.getPenSection();
                    }
                }
            }
        }

        return penalty;
    }

    public void printSlotStatus(Slot slot) {
            System.out.println(slot + "(max =" + slot.getMax() + ")" + " : " + getEventsFromSlot(slot));

    }

    @Override
    public String toString() {
            
            StringBuilder sb = new StringBuilder();

            sb.append("Eval-value: ");
            sb.append(eval());
            sb.append("\n");

            int numEvents = 0;
            
            // Retrieve and sort events by id
            List<Event> sortedEvents = new ArrayList<>(getAllEvents());
            sortedEvents.sort(Comparator.comparing(Event::getId)); // Sort alphabetically by id
    
            for (Event event : sortedEvents) {
                
                numEvents++;
                sb.append(event);
                sb.append("\t".repeat(event.printTabs));
                sb.append(": ");
                sb.append(getSlotFromEvent(event));
                sb.append("\n");
            }

            // sb.append("Number of events: ");
            // sb.append(numEvents);
    
            return sb.toString();
    }
}
