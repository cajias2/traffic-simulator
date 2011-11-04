package sim.graph.algorithms.social.commTracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public class TimeLineList<V, E> {

    private static final String OUTPUT_PATH = System.getProperty("user.dir") + "/output";
    private static final String OUT_METRICS_FILEPATH = OUTPUT_PATH + "/Metrics-";

    private final List<List<Community<V, E>>> _timeLine;

    /**
     * TODO Purpose
     * 
     * @param
     * @author antonio
     */
    public TimeLineList(int size_) {
	_timeLine = new ArrayList<List<Community<V, E>>>(size_);
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    public void add(int idx_, Set<V> comm_, Graph<V, E> graph_) {

	// Fill gaps in the timeline if there are any.
	if (idx_ + 1 > _timeLine.size()) {
	    for (int i = _timeLine.size(); i < idx_; i++) {
		_timeLine.add(null);
	    }
	    _timeLine.add(new ArrayList<Community<V, E>>());
	}
	// Add value
	if (null != comm_ && !comm_.isEmpty()) {
	    Community<V, E> newComm = new Community<V, E>(comm_, graph_);
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
    public void set(int time_, Community<V, E> comm_) {

	if (_timeLine.isEmpty() || (time_ == _timeLine.size() + 1)) {
	    List<Community<V, E>> aux = new ArrayList<Community<V, E>>();
	    _timeLine.add(aux);
	}

	if (time_ >= 1) {
	    if (time_ > _timeLine.size() + 1) {
		for (int i = _timeLine.size(); i < time_; i++) {
		    List<Community<V, E>> aux = new ArrayList<Community<V, E>>();
		    _timeLine.add(aux);
		}
	    }
	    _timeLine.get(time_ - 1).add(comm_);
	}
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return List<Community<V, E>>
     * @author antonio
     */
    public List<Community<V, E>> getSnapshot(int pos_) {
	return _timeLine.get(pos_);
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return List<Community<V, E>>
     * @author antonio
     */
    public List<Community<V, E>> getMaxPredPath(Community<V, E> comm_) {

	List<Community<V, E>> maxPath = new LinkedList<Community<V, E>>();
	maxPath.add(comm_);

	for (Community<V, E> curCom = comm_; curCom.getMainPred() != null; curCom = curCom.getMainPred()) {
	    maxPath.add(curCom);
	}
	return maxPath;
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    public void writeMetrics() {

	File outDir = new File(OUTPUT_PATH);
	if (!outDir.exists())
	    outDir.mkdir();

	try {
	    String fileNameBase = OUT_METRICS_FILEPATH + System.currentTimeMillis();

	    BufferedWriter outWrt = new BufferedWriter(new FileWriter(fileNameBase + "_PerTrace.txt"));
	    BufferedWriter sizeWrt = new BufferedWriter(new FileWriter(fileNameBase + "_PerCom" + ".txt"));
	    // BufferedWriter stabWrt = new BufferedWriter(new
	    // FileWriter(fileNameBase + "_StabSpan" + ".txt"));

	    echoHeader(_timeLine, outWrt);
	    computeMetrics(_timeLine, outWrt);
	    outWrt.close();
	    printSizeAgePerCom(_timeLine, sizeWrt);
	    sizeWrt.close();
	    // printStabSpanPerCom(_timeLine, stabWrt);
	    // stabWrt.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * @param outWrt
     * @throws IOException
     */
    private void echoHeader(List<List<Community<V, E>>> timeline_, BufferedWriter outWrt) throws IOException {

	double comPerSnap = Community.count() / timeline_.size();
	int numTraces = 0;
	double avgComSize;
	double avgTraceLen;
	double avgCoreComm;
	double sumSize = 0;
	double sumCores = 0;
	double maxLength = 0;
	double sumLength = 0;

	for (List<Community<V, E>> snapshot : timeline_) {
	    if (null != snapshot) {
		for (Community<V, E> comm : snapshot) {
		    if (comm.isTimelineFirst()) {
			numTraces++;
			sumLength = sumLength + comm.getTotalTimeLineLen();
			if (comm.getTotalTimeLineLen() > maxLength) {
			    maxLength = comm.getTotalTimeLineLen();
			}
		    }

		    sumSize = sumSize + comm.getSize();
		    sumCores = sumCores + comm.getCoreNodes().size();
		}
	    }
	}
	avgTraceLen = sumLength / numTraces;
	avgComSize = sumSize / Community.count();
	avgCoreComm = sumCores / Community.count();

	outWrt.write("Average community number per snapshot:\t" + comPerSnap + "\n");
	outWrt.write("Evolution trace number:\t" + numTraces + "\n");
	outWrt.write("Average evolution trace length:\t" + avgTraceLen + "\n");
	outWrt.write("Max evolution trace length:\t" + maxLength + "\n");
	outWrt.write("Average community size:\t" + avgComSize + "\n");
	outWrt.write("Core number per community:\t" + avgCoreComm + "\n");
	outWrt.write("\n\n");
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    private void buildCommTrace(Community<V, E> comm_) {
	List<Community<V, E>> previousSnapshot = null;

	if (!_timeLine.isEmpty()) {
	    if (_timeLine.size() > 1) {
		previousSnapshot = _timeLine.get(_timeLine.size() - 2);
	    }
	    List<Community<V, E>> predecessors = null;
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
    private void buildCommTrace(Community<V, E> currCom_, List<Community<V, E>> parentComs_) {
	for (Community<V, E> pred : parentComs_) {
	    Set<Community<V, E>> ancestors = pred.getPredecessors();

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
     * This method checks (2)
     * 
     * @param commA_
     * @param commB_
     * @return boolean true if (2).
     * @author biggie
     */
    private boolean isPredecessor(Community<V, E> commA_, Community<V, E> commB_) {
	boolean isPred = false;

	if (null == commB_.getPredecessors()) {
	    isPred = hasCores(commB_, commA_);
	} else {
	    for (Community<V, E> bPred : commB_.getPredecessors()) {
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
    private boolean hasCores(Community<V, E> commA_, Community<V, E> commB_) {
	boolean isPred = false;
	List<V> aCorNods = commA_.getCoreNodes();
	for (V corNod : aCorNods) {
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
    private boolean isPredAtAll(Community<V, E> commA_, Community<V, E> commB_) {
	boolean isPred = false;
	if (hasCores(commA_, commB_)) {
	    isPred = true;
	} else if (null != commB_.getPredecessors()) {
	    for (Community<V, E> bPred : commB_.getPredecessors()) {
		isPredAtAll(commA_, bPred);
	    }
	}
	return isPred;
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return List<Community<V, E>>
     * @author biggie
     */
    private List<Community<V, E>> findPredCandidates(Community<V, E> currCom_, List<Community<V, E>> previousSnapshot) {

	List<Community<V, E>> predecessors = new ArrayList<Community<V, E>>();
	Set<V> newNodes = currCom_.getAllNodes();

	for (Community<V, E> read : previousSnapshot) {
	    List<V> readCores = read.getCoreNodes();
	    for (V node : readCores) {
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
    private void createComLink(Community<V, E> currCom_, Community<V, E> prevCom_) {
	currCom_.addPredecessor(prevCom_);
	prevCom_.addSuccessor(currCom_);
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     * @param outWrt_
     * @throws IOException
     */
    private static <V, E> void computeMetrics(List<List<Community<V, E>>> snapshotList_, BufferedWriter outWrt_)
	    throws IOException {
	outWrt_.write("Snap\tSize\tAge\tEvolTce\tStability\n");
	for (int i = 0; i < snapshotList_.size(); i++) {
	    List<Community<V, E>> snapshot = snapshotList_.get(i);
	    if (null != snapshot) {
		for (Community<V, E> comm : snapshot) {
		    if (!comm.isTimelineLast()) {
			String outStr = i + "";
			outStr = outStr + "\t" + comm.getSize();
			outStr = outStr + "\t" + comm.getAge();
			outStr = outStr + "\t" + comm.getEvolTrace();
			outStr = outStr + "\t" + comm.getMemberStability();
			outStr = outStr + "\n";
			outWrt_.write(outStr);
			outWrt_.flush();
		    }
		}
	    }
	}
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     * @param outWrt_
     * @throws IOException
     */
    private static <V, E> void printSizeAgePerCom(List<List<Community<V, E>>> snapshotList_, BufferedWriter outWrt_)
	    throws IOException {
	outWrt_.write("Community\tSize\tAge\tStab\n");
	for (int i = 0; i < snapshotList_.size(); i++) {
	    List<Community<V, E>> snapshot = snapshotList_.get(i);
	    if (null != snapshot) {
		for (Community<V, E> comm : snapshot) {
		    if (comm.isTimelineLast()) {
			String sizeOverTime = comm.getID() + "\t" + comm.getSize() + "\t" + comm.getTotalTimeLineLen()
				+ "\t" + comm.getAvgMemberStability() + "\n";
			outWrt_.write(sizeOverTime);
			outWrt_.flush();
		    }
		}
	    }
	}
    }

    /**
     * @param <V>
     * @param snapshotList_
     * @param outWrt_
     * @throws IOException
     */
    private static <V, E> void printStabSpanPerCom(List<List<Community<V, E>>> snapshotList_, BufferedWriter outWrt_)
	    throws IOException {
	outWrt_.write("Community\tStability\tSpan\n");
	for (int i = 0; i < snapshotList_.size(); i++) {
	    List<Community<V, E>> snapshot = snapshotList_.get(i);
	    if (null != snapshot) {
		for (Community<V, E> comm : snapshot) {
		    if (comm.isTimelineLast()) {
			String sizeOverTime = comm.getID() + "\t" + comm.getAvgMemberStability() + "\t" + comm.getAge()
				+ "\n";
			outWrt_.write(sizeOverTime);
			outWrt_.flush();
		    }
		}
	    }
	}
    }

    /**
     * Gets the last one.. traverses back throu main timeline, and reverts list
     * before returning
     * 
     * @param <V>
     * @param comm_
     * @return
     */
    private static <V, E> String commSizeOverTime(Community<V, E> comm_) {

	String tabs = "\t\t\t\t\t\t\t\t\t\t\t\t\t";
	String sizeOverTime = "Comunity_" + comm_.getID() + "\t";
	List<Integer> values = new LinkedList<Integer>();

	for (Community<V, E> curComm = comm_; curComm != null; curComm = curComm.getMainPred()) {
	    values.add(curComm.getSize());
	}
	Collections.reverse(values);
	for (Integer val : values) {
	    sizeOverTime = sizeOverTime + "\t" + val;
	}
	return sizeOverTime + tabs + "\n";
    }

}
