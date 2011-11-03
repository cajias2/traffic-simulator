package sim.graph.algorithms.social.commTracker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

/**
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
    private Set<Community<T>> _predList;
    private Set<Community<T>> _succList;
    private long _bckwdTimelineLen = 0;
    private long _fwdTimelineLen = -1;
    private long _evolTrace = -1;
    private Community<T> _mainPred;
    private Community<T> _mainTimelineFirst;
    private Double _memberStability;

    /**
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
     * TODO Purpose
     * 
     * @param
     * @author antonio
     */
    public Community(Set<T> comm_, Graph<T, Number> graph_) {
	ID = count_++;
	_members = comm_;
	_core = getCommunityCores(_members, graph_);
	_predList = null;
	_succList = null;
	_bckwdTimelineLen = 0;
	_mainTimelineFirst = this;
	_memberStability = Double.NaN;
	if (null == _members) {
	    System.err.println("Here");
	}
    }

    /**
     * Test.
     * 
     * @param
     * @author antonio
     */
    public Community() {
	ID = count_++;
	_members = null;
	_predList = null;
	_succList = null;
	_bckwdTimelineLen = 0;
	_mainPred = null;
	_mainTimelineFirst = this;
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    public void addPredecessor(Community<T> pred_) {
	if (null == _predList) {
	    _predList = new HashSet<Community<T>>();
	}
	if (_predList.add(pred_) && ((pred_.getBckwdTimelineLen() + 1) > _bckwdTimelineLen)) {
	    _mainPred = pred_;
	    _bckwdTimelineLen = pred_.getBckwdTimelineLen() + 1;
	    if (null != pred_.getTimelineFirst()) {
		_mainTimelineFirst = pred_.getTimelineFirst();
	    } else {
		_mainTimelineFirst = pred_;
	    }

	}
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return Community<T>
     * @author biggie
     */
    public Community<T> getTimelineFirst() {
	return _mainTimelineFirst;
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    public void addSuccessor(Community<T> succ_) {
	if (null == _succList) {
	    _succList = new HashSet<Community<T>>();
	}
	_succList.add(succ_);
    }

    /**
     * @param memberStability
     *            the memberStability to set
     */
    public void setMemberStability(Double memberStability) {
	this._memberStability = memberStability;
    }

    /**
     * @return the memberStability
     */
    public Double getMemberStability() {

	if (Double.isNaN(_memberStability) && null != _succList) {
	    Set<T> succUnion = new HashSet<T>();

	    for (Community<T> com : _succList) {
		succUnion.addAll(com.getAllNodes());
	    }

	    Set<T> intersectMembers = new HashSet<T>(_members);
	    Set<T> unionMembers = new HashSet<T>(_members);
	    intersectMembers.retainAll(succUnion);
	    unionMembers.addAll(succUnion);
	    _memberStability = ((intersectMembers.size() + 0.0) / (unionMembers.size() + 0.0));
	}

	return _memberStability;
    }

    /**
     * Number of members in the community
     * 
     * @params
     * @return int
     * @author antonio
     */
    public int getSize() {
	return _members.size();
    }

    public int getID() {
	return ID;
    }

    /**
     * Age of the community. How many snapshots the community has existed for.
     * Definition 3--see {@link http://arxiv.org/abs/0804.4356v1}
     * 
     * @params
     * @return int
     * @author antonio
     */
    public long getAge() {
	return getBckwdTimelineLen() + 1;
    }

    /**
     * Evolution trace.
     * The number of
     * Definition 1--see {@link http://arxiv.org/abs/0804.4356v1}
     * 
     * @return
     */
    public long getEvolTrc() {
	return _fwdTimelineLen;
    }

    /**
     * Returns a list of all predecessors.
     * 
     * @params
     * @return List<Community>
     * @author antonio
     */
    public final Set<Community<T>> getPredecessors() {
	return _predList;
    }

    /**
     * Returns a list of all successors
     * 
     * @params
     * @return List<Community>
     * @author antonio
     */
    public final Set<Community<T>> getSuccessors() {
	return _succList;
    }

    /**
     * Returns the core nodes of the community.
     * See sectio 3 {@link http://arxiv.org/abs/0804.4356v1}
     * 
     * @params
     * @return List<Integer>
     * @author antonio
     */
    public final List<T> getCoreNodes() {
	return _core;
    }

    /**
     * Get Member nodes.
     * 
     * @params
     * @return List<Integer>
     * @author antonio
     */
    public final Set<T> getAllNodes() {
	return _members;
    }

    /**
     * Total timeline length
     * 
     * @params
     * @return int
     * @author biggie
     */
    public long getTotalTimeLineLen() {

	return _bckwdTimelineLen + getFwdTimelineLen();
    }

    /**
     * @return
     */
    public long getFwdTimelineLen() {
	if (_fwdTimelineLen < 0) {
	    _fwdTimelineLen = findFwdTimelineLen(_succList);
	}
	return _fwdTimelineLen;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return long
     * @author biggie
     */
    public long getEvolTrace() {
	if (_evolTrace < 0) {
	    _evolTrace = findEvolTrace(_succList);
	}
	return _evolTrace;
    }

    /**
     * @param succList_
     * @return
     */
    private long findEvolTrace(Set<Community<T>> succList_) {
	long evolutionTrace = 0;
	if (null != succList_) {
	    for (Community<T> succ : succList_) {
		evolutionTrace = evolutionTrace + succ.getEvolTrace() + 1;
	    }
	}
	return evolutionTrace;
    }

    /**
     * Returns predecessor with longest span trace by default.
     * 
     * @return Community<T>
     * @author biggie
     */
    public Community<T> getMainPred() {
	return _mainPred;
    }

    /**
     * Overwrite toString
     */
    @Override
    public String toString() {

	return "_" + ID + "(" + _core + "| " + _members + ")";
    }

    /**
     * Is this community the first of its timeline trace?
     * 
     * @params
     * @return boolean
     * @author biggie
     */
    public boolean isTimelineFirst() {
	return null == _predList || _predList.isEmpty();
    }

    /**
     * Is this community the last of its timeline trace?
     * 
     * @params
     * @return boolean
     * @author biggie
     */
    public boolean isTimelineLast() {
	return null == _succList;
    }

    /**
     * @return
     */
    public Double getAvgMemberStability() {
        Double totalMemberStab = 0.0;
        for (Community<T> currCom = getMainPred(); null != currCom; currCom = currCom.getMainPred()) {
            totalMemberStab = totalMemberStab + currCom.getMemberStability();
        }
        return totalMemberStab / getAge();
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return int
     * @author antonio
     */
    private long getBckwdTimelineLen() {
        return _bckwdTimelineLen;
    }

    /**
     * DFS down the timeline to find longest path.
     * 
     * @param currPath_
     * @param succList_
     * @return int longest path length
     * @author biggie
     */
    private long findFwdTimelineLen(Set<Community<T>> succList_) {
	long longestPathLen = 0;
        if (null != succList_) {
            for (Community<T> succ : succList_) {
		long pathLen = 1 + succ.getFwdTimelineLen();// (longestPathLen,
        						   // succ.getSuccessors());
        	if (longestPathLen < pathLen) {
        	    longestPathLen = pathLen;
        	}
            }
        }
        return longestPathLen;
    }

    /**
     * Uses node degrees to calculate node centrality in a community. Returns a
     * list of core nodes. <br/>
     * See section 5 {@link http://arxiv.org/abs/0804.4356v1}
     * 
     * @param comNodSet_
     *            Set of nodes in a given community
     * @param graph_
     *            The graph object where community was found. Used to check
     *            degrees.
     * @return List<T> core node list.
     * @author antonio
     */
    private List<T> getCommunityCores(Set<T> comNodSet_, Graph<T, Number> graph_) {
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
     * vote lowers down. <br/>
     * See section 5 {@link http://arxiv.org/abs/0804.4356v1}
     * 
     * @param comNodList_
     *            List of nodes in a given community
     * @param graph_
     *            The graph object where community was found. Used to check
     *            degrees.
     * @return int[] Returns an array of nodes with their centrality calculated
     * @author biggie
     */
    private int[] calculateNodCentrality(List<T> comNodList_, Graph<T, Number> graph_) {
        int[] centralDeg = new int[comNodList_.size()];
        for (int i = 0; i < centralDeg.length; i++) {
            centralDeg[i] = 0;
        }
    
        for (int i = 0; i < comNodList_.size(); i++) {
            for (int j = (i + 1); j < comNodList_.size(); j++) {
        	T node1 = comNodList_.get(i);
        	T node2 = comNodList_.get(j);
    
		Number edge1 = graph_.findEdge(node1, node2);
		Number edge2 = graph_.findEdge(node2, node1);
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
    private boolean isSameDegComm(List<T> comNodList, Graph<T, Number> graph_) {
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