/**
 * 
 */
package test.social;

import java.util.LinkedList;
import java.util.List;

import sim.agents.Agent;
import sim.app.social.SocialSimBatchRunner;

/**
 * @author biggie
 */
public class SimulationTest {
    private static final long SEED = 320320486;
    private static final String TOTAL_RUN_FLAG = "-rep";

    /**
     * @param args
     */
    public static void main(String[] args) {
	int totalSimRuns = 1;
	for (int i = 0; i < args.length; i++) {
	    if (TOTAL_RUN_FLAG.equals(args[i])) {
		totalSimRuns = Integer.parseInt(args[++i]);
	    }
	}
	long durTime = System.currentTimeMillis();
	List<Integer> simIDs = SimulationTest.runSim(args, totalSimRuns, SEED);
	System.out.println("New Simulations: " + simIDs);
	System.out.println("DURATION: " + (System.currentTimeMillis() - durTime) / 60000 + " mins");
    }

    /**
     * @param args
     * @param totalSimRuns
     */
    public static List<Integer> runSim(String[] args, int totalSimRuns, long seed_) {
	List<Integer> simIDs = new LinkedList<Integer>();
	for (int i = 0; i < totalSimRuns; i++) {
	    System.out.println("******\n* Run: " + i + "\n******\n");
	    SocialSimBatchRunner<Agent, String> sim = new SocialSimBatchRunner<Agent, String>(seed_);
	    sim.runSim(args);
	    simIDs.add(sim.getSimID());
	    System.out.println("Done.");
	}
	return simIDs;
    }

}
