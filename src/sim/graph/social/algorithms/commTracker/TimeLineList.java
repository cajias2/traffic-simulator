package sim.graph.social.algorithms.commTracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sim.graph.social.link.FriendLink;
import edu.uci.ics.jung.graph.Graph;

public class TimeLineList<T> {
    private final List<List<Community<T>>> _timeLine;
    private final List<List<Community<T>>> _traces;
    private final List<List<List<Double>>> _metrics;

    /**
     * 
     * TODO Purpose
     * 
     * @param
     * @author antonio
     */
    public TimeLineList(int size_) {
	_timeLine = new ArrayList<List<Community<T>>>(size_);
	_traces = new ArrayList<List<Community<T>>>();
	_metrics = new ArrayList<List<List<Double>>>();
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    public void add(int idx_, Set<T> comm_, Graph<T, FriendLink> graph_) {

	// Fill gaps in the timeline if there are any.
	if (idx_ + 1 > _timeLine.size()) {
	    for (int i = _timeLine.size(); i < idx_; i++) {
		_timeLine.add(null);
	    }
	    _timeLine.add(new ArrayList<Community<T>>());
	}
	// Add value
	if (null != comm_ && !comm_.isEmpty()) {
	    Community<T> newComm = new Community<T>(comm_, graph_);
	    _timeLine.get(idx_).add(newComm);
	    buildCommTrace(newComm);
	}

    }

    /**
     * TEST
     * 
     * @param pos_
     * @return
     * @author ANTONIO
     */
    public void set(int time_, Community<T> comm_) {

	if (_timeLine.isEmpty() || (time_ == _timeLine.size() + 1)) {
	    List<Community<T>> aux = new ArrayList<Community<T>>();
	    _timeLine.add(aux);
	}

	if (time_ >= 1) {
	    if (time_ > _timeLine.size() + 1) {
		for (int i = _timeLine.size(); i < time_; i++) {
		    List<Community<T>> aux = new ArrayList<Community<T>>();
		    _timeLine.add(aux);
		}
	    }
	    _timeLine.get(time_ - 1).add(comm_);
	}
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return List<Community<T>>
     * @author antonio
     */
    public List<Community<T>> getSnapshot(int pos_) {
	return _timeLine.get(pos_);
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return List<Community<T>>
     * @author antonio
     */
    public List<Community<T>> getMaxPredPath(Community<T> comm_) {

	List<Community<T>> maxPath = new LinkedList<Community<T>>();
	maxPath.add(comm_);

	for (Community<T> curCom = comm_; curCom.getPredessor() != null; curCom = curCom.getPredessor()) {
	    maxPath.add(curCom);
	}
	return maxPath;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return double
     * @author antonio
     */
    public double getMemberStability(Community<T> comm_) {

	double stability = 0;
	Community<T> currCom = comm_;
	for (Community<T> pred = currCom.getPredessor(); pred != null; pred = pred.getPredessor()) {
	    Set<T> currMem = currCom.getAllNodes();
	    Set<T> predMembers = pred.getAllNodes();

	    int intersect = 0;
	    if (currMem.retainAll(predMembers)) {
		intersect = currMem.size();
	    }
	    stability += ((double) intersect / (predMembers.size() + currMem.size() - intersect));
	    currCom = pred;
	}

	if (comm_.getTimelineLen() > 0) {
	    stability = (stability / comm_.getTimelineLen());
	}
	return stability;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    public void writeMetrics() {

	computeMetrics();

	int numCommunities = Community.count();
	int numSnapshots = _timeLine.size();
	int numTraces = _traces.size();
	int sumSize = 0, sumCores = 0;

	for (List<Community<T>> snapshot : _timeLine) {
	    if (null != snapshot) {
		for (Community<T> comm : snapshot) {
		    sumSize += comm.getSize();
		    sumCores += comm.getCoreNodes().size();
		}
	    }
	}

	int maxLength = 0;
	int sumLength = 0;

	BufferedWriter _outWrt = null;

	File outDir = new File(System.getProperty("user.dir") + "/output");
	if (!outDir.exists())
	    outDir.mkdir();

	FileWriter outFileWrt;
	try {
	    outFileWrt = new FileWriter(outDir.getAbsolutePath() + "/Metrics-" + System.currentTimeMillis() + ".txt");
	    System.out.println("Writing results");
	    _outWrt = new BufferedWriter(outFileWrt);

	    _outWrt.write("Average community number per snapshot:\t" + (double) numCommunities / numSnapshots + "\n");
	    _outWrt.write("Evolution trace number:\t" + numTraces + "\n");
	    _outWrt.write("Average evolution trace length:\t" + (double) sumLength / numTraces + "\n");
	    _outWrt.write("Max evolution trace length:\t" + (double) maxLength + "\n");
	    _outWrt.write("Average community size:\t" + (double) sumSize / numCommunities + "\n");
	    _outWrt.write("Core number per community:\t" + (double) sumCores / numCommunities + "\n");

	    _outWrt.write("\n");
	    _outWrt.write("\n");
	    _outWrt.write("Snap\tSize\tAge\t\tEvolTce\tStability\n");
	    int snapshot = 1;

	    for (List<List<Double>> snapshotValues : _metrics) {
		for (List<Double> commValue : snapshotValues) {
		    _outWrt.write(snapshot + "\t");
		    for (Double value : commValue) {
			_outWrt.write(value + "\t");
		    }
		    _outWrt.write("\n");
		}
		snapshot++;
	    }

	    _outWrt.close();

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    private void buildCommTrace(Community<T> comm_) {
	List<Community<T>> previousSnapshot = null;

	if (!_timeLine.isEmpty()) {
	    if (_timeLine.size() > 1) {
		previousSnapshot = _timeLine.get(_timeLine.size() - 2);
	    }
	    List<Community<T>> predecessors = null;
	    if (null != previousSnapshot) {
		predecessors = findPredCandidates(comm_, previousSnapshot);
	    }
	    if (null != predecessors) {
		buildCommTrace(comm_, predecessors);
	    }

	} else {
	    System.out.println("*****Empty Snapshot****");
	}

    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author biggie
     */
    private void buildCommTrace(Community<T> currCom_, List<Community<T>> parentComs_) {
	for (Community<T> pred : parentComs_) {
	    Set<Community<T>> ancestors = pred.getPredecessors();

	    if (null == ancestors) {
		createComLink(currCom_, pred);
	    } else if (isPredecessor(currCom_, pred)) {
		createComLink(currCom_, pred);
	    }
	}
    }

    /**
     * Checks whether community B is a predecessor of community A. A predecesor
     * should:<br/>
     * (1) Have at least one core node that is currently a node.<br/>
     * AND <br/>
     * (2) Have a node somewhere in it's past that is now a core node. <br/>
     * 
     * This method checks (2)
     * 
     * @param commA_
     * @param commB_
     * @return boolean true if (2).
     * @author biggie
     */
    private boolean isPredecessor(Community<T> commA_, Community<T> commB_) {
	boolean isPred = false;

	if (null == commB_.getPredecessors()) {
	    isPred = hasCores(commB_, commA_);
	} else {
	    for (Community<T> bPred : commB_.getPredecessors()) {
		isPred = isPredAtAll(commA_, bPred);
		if (isPred) {
		    break;
		}
	    }
	}
	return isPred;
    }

    /**
     * Checks if any of the cores of community A are in community B
     * 
     * @param commA_
     * @param commB_
     * @return boolean true is found. false otherwise
     * @author biggie
     */
    private boolean hasCores(Community<T> commA_, Community<T> commB_) {
	boolean isPred = false;
	List<T> aCorNods = commA_.getCoreNodes();
	for (T corNod : aCorNods) {
	    if (commB_.getAllNodes().contains(corNod)) {
		isPred = true;
		break;
	    }
	}
	return isPred;
    }

    /**
     * Recurs down the timeline to find the first predecesor it can find.
     * 
     * @param commA_
     *            community to check
     * @param commB_
     *            predecessor currently looking at.
     * @return boolean true if a predecessor was found.
     * @author biggie
     */
    private boolean isPredAtAll(Community<T> commA_, Community<T> commB_) {
	boolean isPred = false;
	if (hasCores(commA_, commB_)) {
	    isPred = true;
	} else if (null != commB_.getPredecessors()) {
	    for (Community<T> bPred : commB_.getPredecessors()) {
		isPredAtAll(commA_, bPred);
	    }
	}
	return isPred;
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return List<Community<T>>
     * @author biggie
     */
    private List<Community<T>> findPredCandidates(Community<T> currCom_, List<Community<T>> previousSnapshot) {

	List<Community<T>> predecessors = new ArrayList<Community<T>>();
	Set<T> newNodes = currCom_.getAllNodes();

	for (Community<T> read : previousSnapshot) {
	    List<T> readCores = read.getCoreNodes();
	    for (T node : readCores) {
		if (newNodes.contains(node) && null != predecessors && !predecessors.contains(read)) {
		    predecessors.add(read);
		}
	    }
	}
	if (predecessors.isEmpty()) {
	    predecessors = null;
	}
	return predecessors;
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author biggie
     */
    private void createComLink(Community<T> currCom_, Community<T> prevCom_) {
	currCom_.addPredecessor(prevCom_);
	prevCom_.addSuccessor(currCom_);
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    private void computeMetrics() {

	for (List<Community<T>> snapshot : _timeLine) {
	    if (null != snapshot) {
		List<List<Double>> commValues = new ArrayList<List<Double>>();

		for (Community<T> comm : snapshot) {
		    int size = comm.getSize();
		    int age = comm.getAge();
		    Double stability = getMemberStability(comm);

		    int length = comm.getTimelineLen();
		    int totalLen = (comm.getOldestPred() == null) ? comm.getOldestPred().getTimelineLen() : length;
		    int pos = totalLen - length + 1;
		    int evolTrace = length - pos;

		    if (null != stability) {
			List<Double> values = new ArrayList<Double>();
			values.add((double) size);
			values.add((double) age);
			values.add((double) evolTrace);
			values.add(stability);
			commValues.add(values);
		    }
		}
		_metrics.add(commValues);
	    }
	}
    }
}
