package sim.graph.social.metrics.commTracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

import sim.graph.social.link.FriendLink;

public class TimeLineList {
	private List<List<Community>> _timeLine;
	private List<List<Community>> _traces;
	private int _lastSnapshot = 0;
	private List<List<List<Double>>> _metrics;
	
	public TimeLineList() {
		_timeLine = new ArrayList<List<Community>>();
		_traces = new ArrayList<List<Community>>();
		_metrics = new ArrayList<List<List<Double>>>();
	}

	public void set(int time_, Set<Integer> comm_, Graph<Integer,FriendLink> graph_) {

		if (_timeLine.isEmpty()) {
			List<Community> aux = new ArrayList<Community>();
			_timeLine.add(aux);	
			_lastSnapshot = time_;
		}

		if (time_ < 1){ 
			return;
		}
		
		if (time_ >= _timeLine.size() + 1) {
			for(int i= _timeLine.size(); i< time_; i++){
				List<Community> aux = new ArrayList<Community>();
				_timeLine.add(aux);
			}
		}
		
		if(_lastSnapshot < time_){
			for(int i = _lastSnapshot; i<time_; i++){
				computeMetrics(i-1);
			}
			_lastSnapshot = time_;
		}
		
		if(!comm_.isEmpty()){
			Community newComm = new Community(comm_, graph_);
			_timeLine.get(time_ - 1).add(newComm);
			searchPredecessors(newComm);
			newComm.buildSpanTraces();
		}
		
		
	}
	
	/**
	 * ANTONIO TEST
	 * @param pos_
	 * @return
	 */
	public void set(int time_, Community comm_) {

		if (_timeLine.isEmpty() || (time_ == _timeLine.size() + 1)) {
			List<Community> aux = new ArrayList<Community>();
			_timeLine.add(aux);
		}

		if (time_ < 1){ 
			return;
		}
		
		if (time_ > _timeLine.size() + 1) {
			for(int i= _timeLine.size(); i< time_; i++){
				List<Community> aux = new ArrayList<Community>();
				_timeLine.add(aux);
			}
		}
		
			_timeLine.get(time_ - 1).add(comm_);			
			
			comm_.buildSpanTraces();
		
	}

	public List<Community> getSnapshot(int pos_) {
		return _timeLine.get(pos_);
	}
	
	private void searchPredecessors(Community comm_) {
		List<Integer> newCores = comm_.getCoreNodes();
		List<Integer> newNodes = comm_.getAllNodes();

		if (_timeLine.size() > 1) {
			int currentTime = _timeLine.size() - 1;
			List<Community> previousSnapshot = _timeLine.get(currentTime - 1);

			List<Community> predecessors = new ArrayList<Community>();
			for (Community read : previousSnapshot) {
				List<Integer> readCores = read.getCoreNodes();
				for (Integer node : readCores) {
					if (newNodes.contains(node)
							&& (!predecessors.contains(read))) {
						predecessors.add(read);
					}
				}
			}

			for (Community pred : predecessors) {
				List<Community> ancestors = pred.getPredecessors();

				if (!ancestors.isEmpty()) {
					boolean added = false;
					int pointer = 0;
					while (!added && pointer < ancestors.size()) {
						Community ancestor = ancestors.get(pointer);
						pointer++;
						List<Integer> nodesAncestor = ancestor.getAllNodes();
						boolean found = false;
						int secondPointer = 0;

						while (!found && (secondPointer < newCores.size())) {
							int newCore = newCores.get(secondPointer);
							secondPointer++;

							found |= nodesAncestor.contains(newCore);
						}

						if (found) {
							comm_.addPredecessor(pred);
							pred.addSuccessor(comm_);
							added = true;
						} else {
							List<Community> olderAncestors = ancestor
									.getPredecessors();
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
	
	public void writeMetrics(){
		
		computeMetrics(_lastSnapshot-1);
		
		int numCommunities = Community.getNumCommunities();
		int numSnapshots = _timeLine.size();
//		 List<List<Community>> traces = buildTraces();
		 int numTraces = _traces.size();
		int sumSize = 0, sumCores = 0;
		
		for(List<Community> snapshot : _timeLine){
			for(Community comm : snapshot){
				sumSize += comm.getSize();
				sumCores += comm.getCoreNodes().size();
			}
		}
		
		int maxLength = 0;
		int sumLength = 0;
		for(List<Community> trace : _traces){
			sumLength += trace.size();
			if(trace.size()>maxLength){
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
		    
		    _outWrt.write("Average community number per snapshot:\t" + (double)numCommunities/numSnapshots + "\n");
		    _outWrt.write("Evolution trace number:\t" + numTraces + "\n");
		    _outWrt.write("Average evolution trace length:\t" + (double)sumLength/numTraces + "\n");
		    _outWrt.write("Max evolution trace length:\t" + (double)maxLength + "\n");
		    _outWrt.write("Average community size:\t" + (double)sumSize/numCommunities + "\n");
		    _outWrt.write("Core number per community:\t" + (double)sumCores/numCommunities + "\n");
		    
			_outWrt.write("\n");
			_outWrt.write("\n");
			_outWrt.write("Snapshot\tSize\tAge\tEvoltion Trace\tStability\n");
			int snapshot = 1;
			
			for(List<List<Double>> snapshotValues : _metrics){
				for(List<Double> commValue : snapshotValues){
					_outWrt.write(snapshot + "\t");
					for(Double value : commValue){
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
	
	public List<Community> getMaxPath(Community comm_){
		
		if(!comm_.getPredecessors().isEmpty()){
			List<List<Community>> paths = getPaths(comm_);
			
			int maxPathLength = 0;
			int maxPathPosition = -1;
			int index = 0;
			for(List<Community> path : paths){
				int size = path.size();
				if(size > maxPathLength){
					maxPathLength = size;
					maxPathPosition = index;
				}
				index++;
			}
	
			return paths.get(maxPathPosition);
		}
		
		return new ArrayList<Community>();
	}

	public double getMemberStability(Community comm_){
		List<List<Community>> paths = getPaths(comm_);
		double cumStability = 0;
		
		for(List<Community> path : paths){
			double result = 0;
			int length = path.size();

			for (int i = 0; i < length - 1; i++) {
				Community pred = path.get(i);
				Community succ = path.get(i + 1);

				List<Integer> predMembers = pred.getAllNodes();
				List<Integer> succMembers = succ.getAllNodes();
				int intersect = 0;

				if (predMembers.size() <= succMembers.size()) {
					for (Integer member : predMembers) {
						if (succMembers.contains(member))
							intersect++;
					}
				} else {
					for (Integer member : succMembers) {
						if (predMembers.contains(member))
							intersect++;
					}
				}

				result += ((double) intersect / (predMembers.size()
						+ succMembers.size() - intersect));
			}

			if (length > 1) {
				result /= (length - 1);
			} else {
				result = 0;
			}
			
			cumStability += result;
		}

		if(paths.size()>0)
			return (double) cumStability / paths.size();
		
		return -1;
	}

	private void computeMetrics(int time){
    	    	
		buildTraces(time);
		List<Community> snapshot = _timeLine.get(time);
			List<List<Double>> commValues = new ArrayList<List<Double>>();
			
			for (Community comm : snapshot) {
				int size = comm.getSize();
				int age = comm.getAge();
				double stability = getMemberStability(comm);
				List<Community> maxPath = getMaxPath(comm);						
				
				int position = maxPath.indexOf(comm);
				int length = maxPath.size();
				int evolTrace = length-position;
				
				List<Double> values = new ArrayList<Double>();
				values.add((double)size);
				values.add((double)age);
				values.add((double)evolTrace);
				values.add((double)stability);
				commValues.add(values);
			}
			_metrics.add(commValues);
//		}
//    		List<Community> snapshot = _timeLine.get(time);
//    		int numCommunities = snapshot.size();
//    		int cumSize=0, cumAge=0;
//    		int numTraces = 0, cumEvolTrace=0;
//    		double cumStability = 0;
//    		List<Double> results = new ArrayList<Double>();
//    		if(numCommunities>0){
//				for (Community comm : snapshot) {
//					cumSize += comm.getSize();
//					cumAge += comm.getAge();
//					List<List<Community>> traces = getPaths(comm);
//					numTraces = traces.size();
//	
//					for (List<Community> trace : traces) {
//						cumEvolTrace += trace.size();
//					}
//					cumStability += getMemberStability(comm);
//				}
//				results.add((double)cumAge/snapshot.size());
//	    		results.add((double)cumSize/snapshot.size());
//	    		results.add((double)cumEvolTrace/numTraces);
//	    		results.add((double)cumStability/snapshot.size());
//    		}
//    		else{
//    			results.add(0.0);
//    		results.add(0.0);
//    		results.add(0.0);
//    		results.add(0.0);
//    		}
//    		
//    		_metrics.add(results);    	
    }
	
	private List<List<Community>> getPaths(Community _comm){
		List<List<Community>> paths = new ArrayList<List<Community>>();
		for(List<Community> path : _traces){
			if(path.contains(_comm)){
				paths.add(path);
			}
		}
		
		return paths;
	}
	
	private void buildTraces(int time){

		List<Community> snapshot = _timeLine.get(time);
		int count = 1;
		for (Community comm : snapshot) {
			List<List<Community>> commPaths = comm.getTraces();
			
			if (!commPaths.isEmpty()) {
				for (List<Community> path : commPaths) {
					if (_traces.contains(path)) {
						_traces.remove(path);
					}

					List<Community> trace = new ArrayList<Community>();
					trace.addAll(path);
					trace.add(comm);
					_traces.add(trace);
				}
			} else {
				List<Community> trace = new ArrayList<Community>();
				trace.add(comm);
				_traces.add(trace);
			}
			count++;
		}
	}
}
