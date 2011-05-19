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
    public void set(int idx_, Set<T> comm_, Graph<T, FriendLink> graph_) {

	// Fill gaps in the timeline if there are any.
	for (int i = _timeLine.size(); i < idx_; i++) {
	    _timeLine.add(null);
	}

	if (null != comm_ && !comm_.isEmpty()) {
	    Community<T> newComm = new Community<T>(comm_, graph_);
	    _timeLine.add(new ArrayList<Community<T>>());
	    _timeLine.get(_timeLine.size() - 1).add(newComm);
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

	if (time_ < 1) {
	    return;
	}

	if (time_ > _timeLine.size() + 1) {
	    for (int i = _timeLine.size(); i < time_; i++) {
		List<Community<T>> aux = new ArrayList<Community<T>>();
		_timeLine.add(aux);
	    }
	}

	_timeLine.get(time_ - 1).add(comm_);

	// comm_.buildSpanTraces();

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

	for (Community<T> curCom = comm_; curCom.getPredecessor() != null; curCom = curCom.getPredecessor()) {
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
	for (Community<T> pred = currCom.getPredecessor(); pred != null; pred = pred.getPredecessor()) {
	    Set<T> currMem = currCom.getAllNodes();
	    Set<T> predMembers = pred.getAllNodes();

	    int intersect = 0;
	    if (currMem.retainAll(predMembers)) {
		intersect = currMem.size();
	    }
	    stability += ((double) intersect / (predMembers.size() + currMem.size() - intersect));
	    currCom = pred;
	}

	if (comm_.getMaxPredPathLen() > 0) {
	    stability = (stability / comm_.getMaxPredPathLen());
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
	// for (List<Community<T>> trace : _traces) {
	// sumLength += trace.size();
	// if (trace.size() > maxLength) {
	// maxLength = trace.size();
	// }
	// }

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
	    // TODO Auto-generated catch block
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
     * TODO Purpose
     * 
     * @params
     * @return boolean
     * @author biggie
     */
    private boolean isPredecessor(Community<T> currCom_, Community<T> pred_) {
	boolean isPred = false;
	List<T> currComCores = currCom_.getCoreNodes();

	if (null == pred_.getPredecessors()) {
	    isPred = currComCores.retainAll(pred_.getAllNodes());
	} else {

	    for (Community<T> predpred : pred_.getPredecessors()) {
		isPred = isPredecessor(currCom_, predpred);
		if (isPred) {
		    break;
		}
	    }

	}
	return isPred;
	// for (Community<T> ancestor : ancestors) {
	// // Get nodes from ancestor
	// Set<T> nodesAncestor = ancestor.getAllNodes();
	// // check if curr core nodes exist in ancestor
	// boolean hasFound = currComCores.retainAll(nodesAncestor);
	// // check in the ancestors ancestors.
	// }
	//
	// while (!added && pointer < ancestors.size()) {
	// Community<T> ancestor = ancestors.get(pointer);
	// pointer++;
	// Set<T> nodesAncestor = ancestor.getAllNodes();
	// boolean found = false;
	// int secondPointer = 0;
	//
	// while (!found && (secondPointer < currComCores.size())) {
	// T newCore = currComCores.get(secondPointer);
	// secondPointer++;
	// found |= nodesAncestor.contains(newCore);
	// }
	//
	// if (found) {
	// createComLink(currCom_, pred);
	// added = true;
	// } else {
	// List<Community<T>> olderAncestors = ancestor.getPredecessors();
	// if (!olderAncestors.isEmpty()) {
	// ancestors.addAll(olderAncestors);
	// }
	// }
	// }
	// return isPred;
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

		    List<Community<T>> maxTrace = getMaxPredPath(comm);
		    int length = maxTrace.size();
		    int pos = maxTrace.indexOf(comm);
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

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return List<List<Community<T>>>
     * @author antonio
     */
    private List<List<Community<T>>> getPaths(Community<T> _comm) {
	List<List<Community<T>>> paths = new ArrayList<List<Community<T>>>();
	for (List<Community<T>> path : _traces) {
	    if (path.contains(_comm)) {
		paths.add(path);
	    }
	}

	return paths;
    }

}
