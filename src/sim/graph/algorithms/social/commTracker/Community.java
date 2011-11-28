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
 * @param <V>
 * @param <E>
 * @date May 17, 2011
 */
public class Community<V, E> {
    private static int count_ = 0;
    private final int ID;
    private final Set<V> _members;
    private List<V> _core;
    private Set<Community<V, E>> _predList;
    private Set<Community<V, E>> _succList;
    private long _bckwdTimelineLen = 0;
    private long _fwdTimelineLen = -1;
    private long _evolTrace = -1;
    private Community<V, E> _mainPred;
    private Community<V, E> _mainTimelineFirst;
    private Double _memberStability;
    private Set<V> _pastMembers;
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
    public Community(Set<V> comm_, Graph<V, E> graph_) {
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
    public void addPredecessor(Community<V, E> pred_) {
	if (null == _predList) {
	    _predList = new HashSet<Community<V, E>>();
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
	if (null == _pastMembers) {
	    _pastMembers = new HashSet<V>();
	}
	_pastMembers.addAll(pred_.getCurrMembers());
	_pastMembers.addAll(pred_.getPastMembers());
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return Community<V, E>
     * @author biggie
     */
    public Community<V, E> getTimelineFirst() {
	return _mainTimelineFirst;
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    public void addSuccessor(Community<V, E> succ_) {
	if (null == _succList) {
	    _succList = new HashSet<Community<V, E>>();
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
	    Set<V> succUnion = new HashSet<V>();

	    for (Community<V, E> com : _succList) {
		succUnion.addAll(com.getCurrMembers());
	    }

	    Set<V> intersectMembers = new HashSet<V>(_members);
	    Set<V> unionMembers = new HashSet<V>(_members);
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
    public final Set<Community<V, E>> getPredecessors() {
	return _predList;
    }

    /**
     * Returns the core nodes of the community.
     * See sectio 3 {@link http://arxiv.org/abs/0804.4356v1}
     * 
     * @params
     * @return List<Integer>
     * @author antonio
     */
    public final List<V> getCoreMembers() {
	return _core;
    }

    /**
     * Get Member nodes.
     * 
     * @params
     * @return List<Integer>
     * @author antonio
     */
    public final Set<V> getCurrMembers() {
	return _members;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return Set<V>
     * @author biggie
     */
    public final Set<V> getPastMembers() {
	return _pastMembers;
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
    private long findEvolTrace(Set<Community<V, E>> succList_) {
	long evolutionTrace = 0;
	if (null != succList_) {
	    for (Community<V, E> succ : succList_) {
		evolutionTrace = evolutionTrace + succ.getEvolTrace() + 1;
	    }
	}
	return evolutionTrace;
    }

    /**
     * Returns predecessor with longest span trace by default.
     * 
     * @return Community<V, E>
     * @author biggie
     */
    public Community<V, E> getMainPred() {
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
     * @param v_
     * @return
     */
    public boolean isMember(V v_) {
	return _members.contains(v_);
    }

    /**
     * @return
     */
    public Double getAvgMemberStability() {
        Double totalMemberStab = 0.0;
	for (Community<V, E> currCom = getMainPred(); null != currCom; currCom = currCom.getMainPred()) {
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
    private long findFwdTimelineLen(Set<Community<V, E>> succList_) {
	long longestPathLen = 0;
        if (null != succList_) {
	    for (Community<V, E> succ : succList_) {
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
     * @return List<V> core node list.
     * @author antonio
     */
    private List<V> getCommunityCores(Set<V> comNodSet_, Graph<V, E> graph_) {
	List<V> coreNodList = null;
	List<V> comNodeList = new ArrayList<V>(comNodSet_);
    
        if (isSameDegComm(comNodeList, graph_)) {
            coreNodList = comNodeList;
        } else {
            int[] centralDeg = calculateNodCentrality(comNodeList, graph_);
    
	    coreNodList = new ArrayList<V>();
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
    private int[] calculateNodCentrality(List<V> comNodList_, Graph<V, E> graph_) {
        int[] centralDeg = new int[comNodList_.size()];
        for (int i = 0; i < centralDeg.length; i++) {
            centralDeg[i] = 0;
        }
    
        for (int i = 0; i < comNodList_.size(); i++) {
            for (int j = (i + 1); j < comNodList_.size(); j++) {
		V node1 = comNodList_.get(i);
		V node2 = comNodList_.get(j);
    
		E edge1 = graph_.findEdge(node1, node2);
		E edge2 = graph_.findEdge(node2, node1);
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
    private boolean isSameDegComm(List<V> comNodList, Graph<V, E> graph_) {
        boolean sameDegree = true;
        boolean firstNode = true;
        int degree = 0;
    
	for (V node : comNodList) {
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