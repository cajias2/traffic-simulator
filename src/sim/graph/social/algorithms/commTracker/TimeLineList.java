package sim.graph.social.algorithms.commTracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
    public TimeLineList() {
	_timeLine = new ArrayList<List<Community<T>>>();
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
    public void set(int time_, Set<T> comm_, Graph<T, FriendLink> graph_) {

	if (_timeLine.isEmpty()) {
	    List<Community<T>> aux = new ArrayList<Community<T>>();
	    _timeLine.add(aux);
	}

	if (time_ < 1) {
	    return;
	}

	if (time_ >= _timeLine.size() + 1) {
	    for (int i = _timeLine.size(); i < time_; i++) {
		List<Community<T>> aux = new ArrayList<Community<T>>();
		_timeLine.add(aux);
	    }
	}

	if (!comm_.isEmpty()) {
	    Community<T> newComm = new Community<T>(comm_, graph_);
	    _timeLine.get(time_ - 1).add(newComm);
	    searchPredecessors(newComm);
	    newComm.buildSpanTraces();
	}

    }

    /**
     * ANTONIO TEST
     * 
     * @param pos_
     * @return
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

	comm_.buildSpanTraces();

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
    public List<Community<T>> getMaxPath(Community<T> comm_) {
    
        List<List<Community<T>>> paths = getPaths(comm_);
    
        int maxPathLength = 0;
        int maxPathPosition = -1;
        int index = 0;
	for (List<Community<T>> path : paths) {
            int size = path.size();
            if (size > maxPathLength) {
        	maxPathLength = size;
        	maxPathPosition = index;
            }
            index++;
        }
    
        return paths.get(maxPathPosition);
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
	List<List<Community<T>>> paths = getPaths(comm_);
        double cumStability = 0;
        int count = 0;
    
	for (List<Community<T>> path : paths) {
            double result = 0;
    
            int index = path.indexOf(comm_);
	    List<Community<T>> correctPath = path.subList(index, path.size());
    
            int length = correctPath.size();
    
            if (length > 1) {
    
        	for (int i = 0; i < length - 1; i++) {
		    Community<T> pred = correctPath.get(i);
		    Community<T> succ = correctPath.get(i + 1);
    
        	    List<T> predMembers = pred.getAllNodes();
		    List<T> succMembers = succ.getAllNodes();
        	    int intersect = 0;
    
        	    if (predMembers.size() <= succMembers.size()) {
			for (T member : predMembers) {
        		    if (succMembers.contains(member))
        			intersect++;
        		}
        	    } else {
			for (T member : succMembers) {
        		    if (predMembers.contains(member))
        			intersect++;
        		}
        	    }
    
        	    result += ((double) intersect / (predMembers.size() + succMembers.size() - intersect));
        	}
    
        	if (length > 1) {
        	    result /= (length - 1);
        	} else {
        	    result = 0;
        	}
    
        	cumStability += result;
        	count++;
            }
        }
    
        if (count > 0)
            return cumStability / count;
    
        return -1;
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

	int numCommunities = Community.getNumCommunities();
	int numSnapshots = _timeLine.size();
	int numTraces = _traces.size();
	int sumSize = 0, sumCores = 0;

	for (List<Community<T>> snapshot : _timeLine) {
	    for (Community<T> comm : snapshot) {
		sumSize += comm.getSize();
		sumCores += comm.getCoreNodes().size();
	    }
	}

	int maxLength = 0;
	int sumLength = 0;
	for (List<Community<T>> trace : _traces) {
	    sumLength += trace.size();
	    if (trace.size() > maxLength) {
		maxLength = trace.size();
	    }
	}

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
	    _outWrt.write("Snapshot\tSize\tAge\tEvoltion Trace\tStability\n");
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
    private void searchPredecessors(Community<T> comm_) {
        List<T> newCores = comm_.getCoreNodes();
        List<T> newNodes = comm_.getAllNodes();
    
        if (_timeLine.size() > 1) {
            int currentTime = _timeLine.size() - 1;
	    List<Community<T>> previousSnapshot = _timeLine.get(currentTime - 1);
    
            List<Community<T>> predecessors = new ArrayList<Community<T>>();
            for (Community<T> read : previousSnapshot) {
		List<T> readCores = read.getCoreNodes();
		for (T node : readCores) {
        	    if (newNodes.contains(node) && (!predecessors.contains(read))) {
        		predecessors.add(read);
        	    }
        	}
            }
    
            for (Community<T> pred : predecessors) {
		List<Community<T>> ancestors = pred.getPredecessors();
    
        	if (!ancestors.isEmpty()) {
        	    boolean added = false;
        	    int pointer = 0;
        	    while (!added && pointer < ancestors.size()) {
			Community<T> ancestor = ancestors.get(pointer);
        		pointer++;
			List<T> nodesAncestor = ancestor.getAllNodes();
        		boolean found = false;
        		int secondPointer = 0;
    
        		while (!found && (secondPointer < newCores.size())) {
			    T newCore = newCores.get(secondPointer);
        		    secondPointer++;
    
        		    found |= nodesAncestor.contains(newCore);
        		}
    
        		if (found) {
        		    comm_.addPredecessor(pred);
        		    pred.addSuccessor(comm_);
        		    added = true;
        		} else {
			    List<Community<T>> olderAncestors = ancestor.getPredecessors();
        		    if (!olderAncestors.isEmpty())
        			ancestors.addAll(olderAncestors);
        		}
        	    }
        	} else {
        	    comm_.addPredecessor(pred);
        	    pred.addSuccessor(comm_);
        	}
            }
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
    private void computeMetrics() {

	buildTraces();
	for (List<Community<T>> snapshot : _timeLine) {
	    List<List<Double>> commValues = new ArrayList<List<Double>>();

	    for (Community<T> comm : snapshot) {
		int size = comm.getSize();
		int age = comm.getAge();
		double stability = getMemberStability(comm);

		List<Community<T>> maxTrace = getMaxPath(comm);
		int length = maxTrace.size();
		int pos = maxTrace.indexOf(comm);
		int evolTrace = length - pos;

		if (stability >= 0) {
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

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    private void buildTraces() {

	for (List<Community<T>> snapshot : _timeLine) {
	    for (Community<T> comm : snapshot) {
		List<List<Community<T>>> commPaths = comm.getTraces();

		if (!commPaths.isEmpty()) {
		    for (List<Community<T>> path : commPaths) {
			if (_traces.contains(path)) {
			    _traces.remove(path);
			}

			List<Community<T>> trace = new ArrayList<Community<T>>();
			trace.addAll(path);
			trace.add(comm);
			_traces.add(trace);
		    }
		} else {
		    List<Community<T>> trace = new ArrayList<Community<T>>();
		    trace.add(comm);
		    _traces.add(trace);
		}
	    }
	}
    }
}
