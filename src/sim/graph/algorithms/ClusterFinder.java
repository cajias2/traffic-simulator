/**
 * 
 */
package sim.graph.algorithms;

import java.util.Collection;
import java.util.Set;

import sim.app.social.db.DBManager;
import sim.graph.UndirectedSparseDynamicGraph;
import sim.graph.algorithms.social.commTracker.TimeLineList;
import sim.graph.utils.Edge;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author biggie
 *
 */
public class ClusterFinder {
    private final DBManager _dbMgr;
    private TimeLineList<Integer, Edge> _timeLine;
    private final int K_SIZE;

    /**
     * @param dbMgr_
     * @param kSize_
     */
    public ClusterFinder(DBManager dbMgr_, int kSize_) {
	_dbMgr = dbMgr_;
	K_SIZE = kSize_;

    }

    /**
     * @param dbMgr_
     */
    public ClusterFinder(DBManager dbMgr_) {
	this(dbMgr_, 4);
    }

    /**
     * @param k_
     * @param dbMgr
     * @param simID_
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void clusterSim(Integer simID_) {
	UndirectedSparseDynamicGraph dynGraph = new UndirectedSparseDynamicGraph(simID_, _dbMgr);
	_timeLine = new TimeLineList<Integer, Edge>();
	dynGraph.init();
	while (dynGraph.nextStep()) {
	    Collection<Set<Integer>> comms = clusterGraph((Graph) dynGraph);
	    for (Set<Integer> comm : comms) {
		_timeLine.add(dynGraph.getCurrentStep(), comm, (Graph) dynGraph);
		_dbMgr.addCommunityToBatch(simID_, dynGraph.getCurrentStep(), comm);
	    }
	    _dbMgr.insertCommMembers();
	}
    }

    /**
     * @param dynGraph_
     */
    private Collection<Set<Integer>> clusterGraph(Graph<Integer, Edge> dynGraph_) {
	BronKerboschKCliqueFinder<Integer, Edge> kFinder = new BronKerboschKCliqueFinder<Integer, Edge>(dynGraph_);
	Collection<Set<Integer>> maxKClique = kFinder.getAllMaxKCliques(K_SIZE);
	CPMCommunityFinder<Integer> cpmFinder = new CPMCommunityFinder<Integer>(maxKClique);
	return cpmFinder.findCommunities(K_SIZE);
    }

    /**
     * 
     */
    public void writeMetrics() {
	_timeLine.writeMetrics();
    }
}
