/*
*    Team Smoliv
*    Cole Briggs, Chris Axten, Erioluwa Soyemi, Grace Kelly Osena
*    CPSC 433 F24
*/

package com.scheduler.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

import com.scheduler.Debug;
import com.scheduler.model.Event;
import com.scheduler.model.Instance;
import com.scheduler.model.Schedule;
import com.scheduler.model.Slot;

public class ORTree {

    /**
     * The template Schedule the ORTree attempts to follow, or null for
     * an empty template.
     */
    private final Schedule template;

    /**
     * The Schedule being built by the ORTree.
     */
    private final Schedule schedule;

    /**
     * The order of events to schedule.
     */
    private final ArrayList<Event> orderedEvents;

    /**
     * Flag to terminate the search.
     */
    private boolean terminate = false;

    /**
     * Creates an ORTree instance with a given template.
     *
     * @param instance The main search instance.
     * @param template The template Schedule to attempt to follow.
     */
    public ORTree(Instance instance, Schedule template) {
        this.template = template;
        schedule = new Schedule(instance);
        orderedEvents = schedule.getSchedulingOrder();
    }

    /**
     * Creates an ORTree instance with an empty template.
     *
     * @param instance The main search instance.
     */
    public ORTree(Instance instance) {
        this(instance, null);
    }

    /**
     * The main search loop of the ORTree. Returns a complete and valid
     * schedule for the instance of this ORTree object.
     *
     * @return A complete and valid Schedule, based on the template, if
     * applicable.
     */
    public Schedule runSearch() {

        if (template != null) {
            // if using a template, check if it is complete and valid
            if (template.complete() && template.valid()) {
                // if the template happens to be a complete and valid schedule,
                // simply end the search and return it
                Debug.msg4("used template");
                return template;
            }
        }

        Debug.msg4("Going into recsearch to make a schedule");
        if (!recSearch()) {
            throw new IllegalStateException("No valid schedule found.");
        }

        if (!schedule.valid()) {
            throw new IllegalStateException("Invalid schedule created.");
        }

        if (!schedule.complete()) {
            throw new IllegalStateException("Incomplete schedule created.");
        }

        return schedule;
    }

    /**
     * Wrapper method for the recursive search method.
     *
     * @return True if a valid schedule was found, false otherwise.
     */
    private boolean recSearch() {
        return recSearch(0);
    }

    /**
     * Recursive search method for the ORTree.
     *
     * @param nextEventIndex The index of the next event to schedule.
     * @return True if a valid schedule was found, false otherwise.
     */
    private boolean recSearch(int nextEventIndex) {

        // If all events have been scheduled, return true
        if (nextEventIndex >= orderedEvents.size()) {
            Debug.msg4("All events scheduled, returning true from recSearch");

            return true;
        }

        // Get the next event to schedule
        Event event = orderedEvents.get(nextEventIndex);
        Debug.msg4("recSearching at index " + nextEventIndex + ": " + event);

        // Create stack with all candidate slots for the event
        Stack<Slot> candidateSlots = new Stack<>();

        // Add all candidate slots to the stack
        candidateSlots.addAll(schedule.getCandidateSlots(event));

        // Randomize order
        Collections.shuffle(candidateSlots);

        // If a template is being used, put the template's assigned event at the top of the stack
        if (template != null) {
            candidateSlots.add(template.getSlotFromEvent(event));
        }

        // While there are candidate slots to try
        while (!candidateSlots.empty()) {

            // Get the next candidate slot
            Slot slot = candidateSlots.pop();

            // Try to schedule the event in the slot
            if (schedule.assign(event, slot)) {
                // If the event was scheduled, try to schedule the next event
                if (recSearch(nextEventIndex + 1)) {
                    return true;
                } else if (terminate) {

                    if (nextEventIndex > 0) {
                        schedule.clearAssignment(event);
                        return false;
                    } else {
                        terminate = false;
                        candidateSlots.add(slot);
                    }

                }

                Random random = new Random();

                int chance = random.nextInt(100);

                if (chance < 5) {
                    terminate = true;
                }

                // If the event could not be scheduled, unschedule it
                schedule.clearAssignment(event);
            }

        }

        return false;
    }
}
