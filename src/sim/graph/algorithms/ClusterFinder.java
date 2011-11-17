/**
 * 
 */
package sim.graph.algorithms;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import sim.app.social.db.DBManager;
import sim.graph.UndirectedSparseDynamicGraph;
import sim.graph.utils.Edge;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 *
 */
public class ClusterFinder {
    /**
     * @param simIDs_
     */
    public static void findSimClusters(List<Integer> simIDs_, int k_) {
	DBManager dbMgr = new DBManager();
	for (Integer simID : simIDs_) {
	    clusterGraph(k_, dbMgr, simID);
	}
    }

    /**
     * @param k_
     * @param dbMgr
     * @param simID
     */
    private static void clusterGraph(int k_, DBManager dbMgr, Integer simID) {
	UndirectedSparseDynamicGraph dynGraph = new UndirectedSparseDynamicGraph(simID, dbMgr);
	dynGraph.init();
	while (dynGraph.nextStep()) {
	    Collection<Set<Integer>> comms = clusterGraph((Graph) dynGraph, k_);
	    for (Set<Integer> comm : comms) {
		dbMgr.addCommunityToBatch(simID, dynGraph.getCurrentStep(), comm);
	    }
	    dbMgr.insertCommMembers();
	}
    }

    /**
     * @param dynGraph_
     */
    private static Collection<Set<Integer>> clusterGraph(Graph<Integer, Edge> dynGraph_, int k_) {
	BronKerboschKCliqueFinder<Integer, Edge> kFinder = new BronKerboschKCliqueFinder<Integer, Edge>(dynGraph_);
	Collection<Set<Integer>> maxKClique = kFinder.getAllMaxKCliques(k_);
	CPMCommunityFinder<Integer> cpmFinder = new CPMCommunityFinder<Integer>(maxKClique);
	return cpmFinder.findCommunities(k_);
    }
}
