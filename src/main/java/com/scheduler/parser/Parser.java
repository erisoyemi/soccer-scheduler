/*
*    Team Smoliv
*    Cole Briggs, Chris Axten, Erioluwa Soyemi, Grace Kelly Osena
*    CPSC 433 F24
*/

package com.scheduler.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.scheduler.Debug;
import com.scheduler.model.Event;
import com.scheduler.model.Instance;
import com.scheduler.model.Slot;

/*
 * The Parser class is responsible for parsing a file containing scheduling information
 * and populating an Instance object with the parsed data.
 */
public class Parser {

    /*
     * Parses the specified file and returns an Instance object populated with the parsed data.
     *
     * @param filename the name of the file to parse
     * @return an Instance object populated with the parsed data
     * @throws Exception if an I/O error occurs while reading the file
     * @throws IllegalArgumentException if the file contains an unknown section or a section header is missing
     */

    public static Instance parseFile(File inputFile, int wMinFilled, int wPref, int wPair, int wSecDiff, int penGameMin, int penPracticeMin, int penNotPaired, int penSection) throws Exception {

        Debug.msg("Parsing file: " + inputFile.getName());

        Instance instance = new Instance(wMinFilled,  wPref,  wPair,  wSecDiff,  penGameMin,  penPracticeMin,  penNotPaired, penSection);

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {

            String line;
            String currentSection = null;

            while ((line = reader.readLine()) != null) {

                line = line.trim(); // Remove extra spaces

                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }

                // Check for section headers
                if (line.endsWith(":")) {
                    currentSection = line.substring(0, line.length() - 1).toLowerCase();
                    continue;
                }

                // Parse each section
                if (currentSection == null) {
                    throw new IllegalArgumentException("Section header is missing before line: " + line);
                }
                
                // Parse the line based on the current section
                switch (currentSection) {

                    case "name" -> { continue; }

                    case "game slots" -> parseSlot(instance, line, true);

                    case "practice slots" -> parseSlot(instance, line, false);

                    case "games" -> parseEvent(instance, line, true);

                    case "practices" -> parseEvent(instance, line, false);

                    case "not compatible" -> parseNotCompatible(instance, line);

                    case "unwanted" -> parseUnwanted(instance, line);

                    case "preferences" -> parsePreference(instance, line);

                    case "pair" -> parsePair(instance, line);

                    case "partial assignments" -> parsePartialAssignment(instance, line);

                    default -> throw new IllegalArgumentException("Unknown input file section: " + currentSection);
                }
            }
        }

        return instance;
    }

    /**
     * Parses a "slot" line and adds it to the Instance object data.
     *
     * @param instance the Instance object to populate with the parsed data
     * @param line the line to parse
     * @param isGameSlot true if the slot is a game slot, false if it is a practice slot
     */
     private static void parseSlot(Instance instance, String line, boolean isGameSlot) {

        String[] parts = line.split(",\\s*"); // Split on commas and ignore spaces

        String day = parts[0];
        String startTime = parts[1]; 
        int max = Integer.parseInt(parts[2]);
        int min = Integer.parseInt(parts[3]);

        instance.addSlot(new Slot(isGameSlot, day, startTime, max, min));

    }

    /**
     * Parses an "event" line and adds it to the Instance object data.
     *
     * @param line the line to parse
     * @param isGame true if the event is a game, false if it is a practice
     */
    private static void parseEvent(Instance instance, String line, boolean isGame) {

        try {

            Event event = new Event(line, isGame);
            
            instance.addEvent(event);

        } catch (Exception e) {
            System.err.println("Error parsing event: " + line);
        }
    }

    /**
     * Parses a "not compatible" line and adds it to the Instance object data.
     *
     * @param line the line to parse
     */
    private static void parseNotCompatible(Instance instance, String line) {

        String[] parts = line.split(",\\s*"); // Split on commas and ignore spaces

        String event1 = parts[0];
        String event2 = parts[1];

        instance.addIncompatible(event1, event2);

    }

    /**
     * Parses an "unwanted" line and adds it to the Instance object data.
     *
     * @param line the line to parse
     */
    private static void parseUnwanted(Instance instance, String line) {
        
        String[] parts = line.split(",\\s*"); // Split on commas and ignore spaces

        String eventId = parts[0];

        String day = parts[1];
        String startTime = parts[2];

        instance.addUnwanted(eventId, day, startTime);

    }

    /**
     * Parses a "preferences" line and adds it to the Instance object data.
     *
     * @param line the line to parse
     */
    private static void parsePreference(Instance instance, String line) {
        
        String[] parts = line.split(",\\s*"); // Split on commas and ignore spaces

        String day = parts[0];
        String startTime = parts[1];

        String eventId = parts[2];

        int weight = Integer.parseInt(parts[3]);

        instance.addPreference(eventId, day, startTime, weight);
    }

    /**
     * Parses a "pair" line and adds it to the Instance object data.
     *
     * @param line the line to parse
     */
    private static void parsePair(Instance instance, String line) {

        String[] parts = line.split(",\\s*"); // Split on commas and ignore spaces

        String event1 = parts[0];
        String event2 = parts[1];

        instance.addPair(event1, event2);
    }

    /**
     * Parses a "partial assignment" line adds it to the Instance object data.
     *
     * @param line the line to parse
     */
    private static void parsePartialAssignment(Instance instance, String line) throws Exception {
        
        String[] parts = line.split(",\\s*"); // Split on commas and ignore spaces

        String eventId = parts[0];
        String day = parts[1];
        String startTime = parts[2];

        instance.addPartialAssignment(eventId, day, startTime);
    }
}
