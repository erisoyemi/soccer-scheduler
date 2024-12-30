/*
*    Team Smoliv
*    Cole Briggs, Chris Axten, Erioluwa Soyemi, Grace Kelly Osena
*    CPSC 433 F24
*/

package com.scheduler.search;

import java.util.LinkedList;
import java.util.Random;

import com.scheduler.Debug;
import com.scheduler.model.Event;
import com.scheduler.model.Instance;
import com.scheduler.model.Schedule;
import com.scheduler.model.Slot;

public class GeneticAlgorithm {

	// INITIALIZE NECESSARY VARIABLES AND CONSTANTS
	
	/*
	 * The object that stores the schedules that are created.
	 */
	public BSTree pool;
	
	/*
	 * Minimum amount of schedules required in the pool.
	 */
	public final int minSchedules = 100;
	
	/*
	 * Maximum amount of schedules required in the pool.
	 */
	public final int maxSchedules = 1000;
	
	/*
	 * The amount of schedules to be deleted from the pool should a schedule deletion be necessary.
	 */
	public final int deleteSchedules = 900;
	
	/*
	 * Pool state identifiers which determines if the pool is not within the range of the min and max schedules. 
	 */
	public boolean caseOverflow;
	public boolean caseIncomplete;
	
	/*
	 * Flag which indicates if the algorithm has fulfilled its goal and can proceed to ending further logical operations.
	 */
	private boolean terminate = false;

	private int runCounter = 0;

	
	// POOL FITNESS HISTORY TRACKERS
	
    /*
     * LinkedList object for efficiently storing the recent fitness values of the pool being developed.
     */
    LinkedList<Integer> fitnessHistory = new LinkedList<>();
    
    /*
     * Constant value identifying the margin of difference between fitness history values to determine plateau.
     */
    final int fitnessMarginThreshold = 1;
    
    /*
     * Constant value identifying the range of historical fitness data to track for.
     */
    final int fitnessHistoryWindow = 5;
    
	/*
	 * The Instance containing relevant context of the problem.
	 */
	private Instance Instance;
	
	/**
	 * Creates a Genetic Algorithm instance using the instance. This creates an empty pool of schedules.
	 * 
	 * @param instance The initial instance containing facts and relevant data needed to run a Genetic Algorithm search.
	 */
	public GeneticAlgorithm(Instance instance) {
		this.Instance = instance;
		this.pool = new BSTree();
	}
	
	/**
	 * The main search loop of the Genetic Algorithm.
	 * 
	 * This loop will continuously evaluate the state of the pool and algorithm and will call the necessary transition
	 * functions to be applied for every state.
	 * 
	 * The states being checked for includes:
	 * (1) the case where the pool has schedules more than the allowable maximum amount,
	 * (2) the case where the pool has schedules less than the minimum expected amount,
	 * (3) the amount of schedules in pool is neither under or over the expected min and max amounts, respectively, and
	 * (4) when the algorithm's termination flag has been triggered which breaks the search out of the loop and the function
	 *     returns a single valid schedule from the pool.
	 * 
	 * @return A complete and valid Schedule.
	 */
	public Schedule runSearch() {

		Debug.msg3("Running Genetic Algorithm");
		
		Schedule finalSchedule = null;
		
		while (!this.terminate) {

			this.fWert();

			Debug.msg3("Pool size: " + pool.size);

			Debug.msg3("overflow: " + this.caseOverflow);

			Debug.msg3("incomplete: " + this.caseIncomplete);
			
			if (this.caseOverflow) {
				Debug.msg3("Case overflow");

				pool.remove(deleteSchedules);
				runCounter++;
				
				// Check for plateauing threshold value in fitness sum history
				if (poolPlateau()) {
					this.terminate = true; 
				}
				
			} else if (this.caseIncomplete) {

				Debug.msg3("Case incomplete");
				
				Schedule newSchedule = Rand();
				Debug.msg3("got random");
				
				pool.add(newSchedule);
				
			} else { // If pool is NEITHER incomplete nor overflowing, Mutate or Crossover from existing schedules in pool
				
				Debug.msg3("Case other");

				String transition = fSelect(70); // Set the bias to 70-30 in favor of the crossover.
				
				if (transition.equals("Crossover")) {

					Debug.msg3("Cross");
					
					Schedule newSchedule = Crossover();
					pool.add(newSchedule);
					
				} else { // If Mutation has been selected over Crossover

					Debug.msg3("mutation");
					
					Schedule newSchedule = Mutation();
					pool.add(newSchedule);
					
				}
			}
		}
		
		finalSchedule = this.pool.getBest();
		return finalSchedule;
	}
	
	
	/**
	 * Function to evaluate the state of the pool and assist in determining which transition method to call to create
	 * new or modify pool.
	 * 
	 */
	public void fWert() {
		
        // Evaluate each case
        this.caseOverflow = (pool.size > this.maxSchedules);
        this.caseIncomplete = (pool.size < this.minSchedules);	
	}
	
	
	/**
	 * Function to select a transition between Mutation and Crossover
	 * 
	 * @param r refers to weight distribution of how much more likely is Crossover going to be selected over Mutation.
	 * @return String identifying which transition to choose between Mutation and Crossover.
	 */
	public String fSelect(int r) {
		Random random = new Random();
		
		// Decide between Mutation and Crossover
		int chance = random.nextInt(100);
		return (chance < r) ? "Crossover" : "Mutation";
	}
	
	
	/**
	 * Function to determine if the fitness sum of the pool has plateaued.
	 * 
	 * @return boolean indicating if the fitness sum has plateaued.
	 */
	public boolean plateau() {

		if (fitnessHistory.size() >= fitnessHistoryWindow) {
			int difference = fitnessHistory.getLast() - fitnessHistory.getFirst();
			if (difference <= fitnessMarginThreshold) {
				return true; 
			}
		}

		return false;
	}
	

	/**
	 * Function to determine if the fitness sum of the pool has plateaued.
	 * 
	 * @return boolean indicating if the fitness sum has plateaued.
	 */
	public boolean poolPlateau() {

		if (pool.size >= minSchedules) {
			// Calculate average fitness of pool

			int avg = pool.fitSum / pool.size;

			int worst = pool.getWorst().eval();

			Debug.msg3("Average fitness: " + avg);
			Debug.msg3("Best fitness: " + pool.getBest().eval());
			Debug.msg3("Worst fitness: " + worst);
			int difference = worst - pool.getBest().eval();	

			// if (difference <= fitnessMarginThreshold) {
			if (runCounter >= 5) {

				Debug.msg3("difference: " + difference);
				return true;
			}
		}

	return false;

	}
	
	// TRANSITION FUNCTIONS:

	private Schedule Rand() {

		Debug.msg3("HERE in Rand");
		ORTree ortree = new ORTree(Instance);
		Debug.msg3("HERE2 in Rand");

		return ortree.runSearch();
	}

	private Schedule Mutation() {

		Schedule template = pool.getBest();

		int k = 2;

		Event e;
		Slot t;

		while(k > 0) {

			Debug.msg3("HERE in Mutation");

			e = template.getRandomMutableEvent();

			if (e == null) {
				break;
			}

			t = template.getRandomCandidateSlot(e);

			if (t != null) {

				template.assign(e, t);

			}

			k = k - 1;
		}
		
		ORTree ortree = new ORTree(Instance, template);
		return ortree.runSearch();
	}

	private Schedule Crossover() {
		
		Schedule template = pool.getBest();

		int k = 2;

		Schedule template2 = pool.get2Best();
		Event e;
		Slot t;

		while(k > 0) {

			e = template.getRandomMutableEvent();

			if (e == null) {
				break;
			}

			t = template2.getSlotFromEvent(e);

			if (t != null) {

				template.assign(e, t);

			}

			k = k - 1;
		}
		
		ORTree ortree = new ORTree(Instance, template);
		return ortree.runSearch();
	}	
}
