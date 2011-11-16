/**
 * 
 */
package sim.graph;

import java.util.List;

import sim.app.social.db.DBManager;
import sim.graph.utils.Edge;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * The *DynamicGraph classes have the ability to apply changes to their edge
 * connections by fetching updates.
 * 
 * @author biggie
 * @param <V>
 * @param <E>
 */
public class UndirectedSparseDynamicGraph extends UndirectedSparseGraph<Integer, Edge<Integer>> {

    /**
     * 
     */
    private static final long serialVersionUID = -6660510849690382021L;

    private final int SIM_ID;
    private final DBManager _dbMgr;
    private int _currentStep;
    
    public UndirectedSparseDynamicGraph(int simID_, DBManager dbManager_) {
	super();
	SIM_ID = simID_;
	_dbMgr = dbManager_;
	_currentStep = 0;
    }
    
    /**
     * Initializes the graph.
     */
    public void init() {
	int nodeCount = _dbMgr.getAgentCount(SIM_ID);
	for (int i = 0; i < nodeCount; i++) {
	    addVertex(i);
	}
	List<Edge<Integer>> edges = _dbMgr.getEdges(SIM_ID, _currentStep);
	for (Edge<Integer> e : edges) {
	    addEdge(e, e.v1, e.v2);
	}
    }

    /**
     * Moves the simulation one step forward in time. Returns false if could not
     * moved forward anymore.
     * 
     * @return
     */
    public boolean nextStep() {
	boolean movedFwd = false;
	for (Edge<Integer> e : _dbMgr.getEdges(SIM_ID, ++_currentStep)) {
	    movedFwd = true;
	    if (e.isCreate()) {
		addEdge(e, e.v1, e.v2);
	    } else {
		removeEdge(findEdge(e.v1, e.v2));
	    }
	}
	if (!movedFwd) {
	    _currentStep--;
	}
	return movedFwd;
    }
}
