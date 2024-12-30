/*
*    Team Smoliv
*    Cole Briggs, Chris Axten, Erioluwa Soyemi, Grace Kelly Osena
*    CPSC 433 F24
*/

package com.scheduler;

import java.io.File;
import java.io.IOException;

import com.scheduler.model.Instance;
import com.scheduler.model.Schedule;
import com.scheduler.parser.Parser;
import com.scheduler.search.GeneticAlgorithm;

public class Main {
    public static void main(String[] args) {

        if(args.length > 0){
            try {

                if (args.length < 9) {
                    System.err.println("Not enough arguments. Please provide the following arguments: <filename> <wMinFilled> <wPref> <wPair> <wSecDiff> <penGameMin> <penPracticeMin> <penNotPaired> <penSection>");
                    System.exit(1);
                }

                String fileName = args[0];
                File file = new File(fileName);
                
                int wMinFilled = Integer.parseInt(args[1]);
                int wPref = Integer.parseInt(args[2]);
                int wPair = Integer.parseInt(args[3]);
                int wSecDiff = Integer.parseInt(args[4]);
                int penGameMin = Integer.parseInt(args[5]);
                int penPracticeMin = Integer.parseInt(args[6]);
                int penNotPaired = Integer.parseInt(args[7]);
                int penSection = Integer.parseInt(args[8]);

                // Parse the input file
                Instance instance = Parser.parseFile(file, wMinFilled, wPref, wPair, wSecDiff, penGameMin,penPracticeMin, penNotPaired, penSection );
                // Run the genetic algorithm to find the optimal schedule

                Schedule optimalSchedule = new GeneticAlgorithm(instance).runSearch();

                // Print the schedule
                System.out.println(optimalSchedule);

            } catch (NumberFormatException e) {
                System.err.println("Number format error: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("IO error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        else {
            System.out.println("No command line arguments found.");
        }

    }

    
}
