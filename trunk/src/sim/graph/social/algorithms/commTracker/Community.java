package sim.graph.social.algorithms.commTracker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sim.graph.social.link.FriendLink;
import edu.uci.ics.jung.graph.Graph;

/**
 * 
 * TODO Purpose
 * 
 * @author antonio
 * @param <T>
 * @date May 17, 2011
 */
public class Community<T> {
    private static int count_ = 0;
    private final int ID;
    private final Set<T> _members;
    private List<T> _core;
    private Set<Community<T>> _predecessors;
    private Set<Community<T>> _successors;
    private int _maxPredPathLen = 0;
    private Community<T> _maxPredecessor;
    private Community<T> _predListTop;

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return int
     * @author antonio
     */
    public static int count() {
	return count_;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @param
     * @author antonio
     */
    public Community(Set<T> comm_, Graph<T, FriendLink> graph_) {
	ID = count_++;
	_members = comm_;
	// _traces = new ArrayList<List<Community<T>>>();
	_core = getCommunityCores(_members, graph_);
	_predecessors = null;
	_successors = null;
	_maxPredPathLen = 0;
	_predListTop = this;
    }

    /**
     * 
     * Test.
     * 
     * @param
     * @author antonio
     */
    public Community() {
	ID = count_++;
	_members = new HashSet<T>();
	_predecessors = null;
	_successors = null;
	_maxPredPathLen = 0;
	_maxPredecessor = null;
	_predListTop = this;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    public void addPredecessor(Community<T> pred_) {
	if (null == _predecessors) {
	    _predecessors = new HashSet<Community<T>>();
	}
	if (_predecessors.add(pred_) && ((pred_.getMaxPredPathLen() + 1) > _maxPredPathLen)) {
	    _maxPredecessor = pred_;
	    _maxPredPathLen = pred_.getMaxPredPathLen() + 1;
	    _predListTop = pred_.getPredListTop();
	}
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return Community<T>
     * @author biggie
     */
    public Community<T> getPredListTop() {
	return _predListTop;
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    public void addSuccessor(Community<T> succ_) {
	if (null == _successors) {
	    _successors = new HashSet<Community<T>>();
	}
	_successors.add(succ_);

    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return int
     * @author antonio
     */
    public int getSize() {
	return _members.size();
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return int
     * @author antonio
     */
    public int getAge() {
	return _predListTop.getMaxPredPathLen() - getMaxPredPathLen() + 1;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return int
     * @author antonio
     */
    public int getMaxPredPathLen() {
	return _maxPredPathLen;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return Community
     * @author antonio
     */
    public final Community<T> getMaxSuccessor() {
	return _maxPredecessor;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return List<Community>
     * @author antonio
     */
    public final Set<Community<T>> getPredecessors() {
	return _predecessors;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return List<Community>
     * @author antonio
     */
    public final Set<Community<T>> getSuccessors() {
	return _successors;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return List<Integer>
     * @author antonio
     */
    public final List<T> getCoreNodes() {
	return _core;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return List<Integer>
     * @author antonio
     */
    public final Set<T> getAllNodes() {
	return _members;
    }


    /**
     * Returns predecessor with longest span trace by default.
     * 
     * @return Community<T>
     * @author biggie
     */
    public Community<T> getPredecessor() {
        return _maxPredecessor;
    }

    // /**
    // *
    // * TODO Purpose
    // *
    // * @params
    // * @return void
    // * @author antonio
    // */
    // public void buildSpanTraces() {
    // if (!_predecessors.isEmpty()) {
    // for (Community<T> predecessor : _predecessors) {
    // List<List<Community<T>>> predTraces = predecessor.getTraces();
    //
    // if (!predTraces.isEmpty()) {
    // for (List<Community<T>> trace : predTraces) {
    // List<Community<T>> newTrace = new ArrayList<Community<T>>();
    // newTrace.addAll(trace);
    // newTrace.add(predecessor);
    // _traces.add(newTrace);
    // }
    //
    // } else {
    // _traces.add(new ArrayList<Community<T>>());
    // int size = _traces.size();
    // _traces.get(size - 1).add(predecessor); // TODO only add
    // // node. Not lists
    // }
    //
    // }
    // }
    //
    // List<Community<T>> traceSpan = getMaxTraceSpan();
    // _age = traceSpan.size() + 1;
    // }

    /**
     * 
     */
    @Override
    public String toString() {
	String community = "_" + ID + "( ";

	for (T node : _core) {
	    community += node + " ";
	}
	community += "| ";
	for (T node : _members) {
	    community += node + " ";
	}
	community += ")";

	return community;
    }

    /**
     * 
     * Uses node degrees to calculate node centrality in a community. Returns a
     * list of core nodes.
     * 
     * @param comNodSet_
     *            Set of nodes in a given community
     * @param graph_
     *            The graph object where community was found. Used to check
     *            degrees.
     * @return List<T> core node list.
     * @author antonio
     */
    private List<T> getCommunityCores(Set<T> comNodSet_, Graph<T, FriendLink> graph_) {
	List<T> coreNodList = null;
	List<T> comNodeList = new ArrayList<T>(comNodSet_);

	if (isSameDegComm(comNodeList, graph_)) {
	    coreNodList = comNodeList;
	} else {
	    int[] centralDeg = calculateNodCentrality(comNodeList, graph_);

	    coreNodList = new ArrayList<T>();
	    for (int i = 0; i < comNodSet_.size(); i++) {
		if (centralDeg[i] >= 0)
		    coreNodList.add(comNodeList.get(i));
	    }
	}
	return coreNodList;
    }

    /**
     * Calculates centrality of nodes in a given community by comparing
     * sequencially comparing their degrees. Centrality is calculated through a
     * voting scheme, where lower-deg nodes vote higher-deg nodes up, highers
     * vote lowers down. .
     * 
     * @param comNodList_
     *            List of nodes in a given community
     * @param graph_
     *            The graph object where community was found. Used to check
     *            degrees.
     * @return int[] Returns an array of nodes with their centrality calculated
     * @author biggie
     */
    private int[] calculateNodCentrality(List<T> comNodList_, Graph<T, FriendLink> graph_) {
	int[] centralDeg = new int[comNodList_.size()];
	for (int i = 0; i < centralDeg.length; i++) {
	    centralDeg[i] = 0;
	}

	for (int i = 0; i < comNodList_.size(); i++) {
	    for (int j = (i + 1); j < comNodList_.size(); j++) {
		T node1 = comNodList_.get(i);
		T node2 = comNodList_.get(j);

	    FriendLink edge1 = graph_.findEdge(node1, node2);
		FriendLink edge2 = graph_.findEdge(node2, node1);
		if ((edge1 != null) || (edge2 != null)) {
		    int grado1 = graph_.degree(node1);
		    int grado2 = graph_.degree(node2);

		if (grado1 < grado2) {
			centralDeg[i] = centralDeg[i] - Math.abs((grado1 - grado2));
			centralDeg[j] = centralDeg[j] + Math.abs((grado1 - grado2));
		    } else {
			centralDeg[i] = centralDeg[i] + Math.abs((grado1 - grado2));
			centralDeg[j] = centralDeg[j] + Math.abs((grado1 - grado2));
		}
	    }
	}
	}
	return centralDeg;
    }

    /**
     * Returns true if all nodes in the community have the same degree. False
     * otherwise.
     * 
     * @param comNodList
     *            Community to check
     * @param graph_
     *            The graph object where community was found. Used to check
     *            degrees.
     * @return boolean
     * @author biggie
     */
    private boolean isSameDegComm(List<T> comNodList, Graph<T, FriendLink> graph_) {
	boolean sameDegree = true;
	boolean firstNode = true;
	int degree = 0;

	for (T node : comNodList) {
	    if (!sameDegree) {
		break;
	    }
	    if (firstNode) {
		degree = graph_.degree(node);
		firstNode = false;
	    } else
		sameDegree = (graph_.degree(node) == degree);
	}
	return sameDegree;
    }
}