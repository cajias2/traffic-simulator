package sim.graph.social.metrics.commTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

import sim.graph.social.link.FriendLink;
import sim.mason.AgentNetwork;

public class TimeLineList {
	private List<List<Community>> _timeLine;

	public TimeLineList() {
		_timeLine = new ArrayList<List<Community>>();
	}

	public void add(int time_, Set<Integer> comm_, Graph<Integer,FriendLink> graph_) {

		if (_timeLine.isEmpty() || (time_ == _timeLine.size() + 1)) {
			List<Community> aux = new ArrayList<Community>();
			_timeLine.add(aux);
		}

		if ((time_ < 1) || (time_ > _timeLine.size() + 1)) {
			return;
		}
		
		if(!comm_.isEmpty()){
			Community newComm = new Community(comm_, graph_);
			_timeLine.get(time_ - 1).add(newComm);
			searchPredecessors(newComm);			
			newComm.computeAge();
		}
	}

	public List<Community> get(int pos_) {
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

	public int getTraceSpan(Community comm_){
		int timeLineLength = _timeLine.size();
		int i=0;
		boolean found = false;
		
		while(!found && (i<timeLineLength)){
			List<Community> snapshot = _timeLine.get(i);
			if(snapshot.contains(comm_)){
				found = true;
			}
			else
				i++;
		}
		return timeLineLength-i;
	}
	
	private List<Community> getMaxPath(Community comm_){
		List<Community> path = new ArrayList<Community>();
		
//		path.add(comm_);
//		Community aux = comm_.getMaxSuccessor();
//		while(aux!=null){
//			path.add(aux);
//			aux = aux.getMaxSuccessor();
//		}
		
		
		List<Community> successors = comm_.getSuccessors();
		path.add(comm_);
		
		if(!successors.isEmpty()){
			List<List<Community>> paths = new ArrayList<List<Community>>();
			
			for(int i=0; i<successors.size(); i++){
				Community successor = successors.get(i);
				paths.add(getMaxPath(successor));
			}
			
			int maxPathLength = 0;
			int maxPathPosition = -1;
			int index = 0;
			for(List<Community> successorPath : paths){
				int size = successorPath.size();
				if(size > maxPathLength){
					maxPathLength = size;
					maxPathPosition = index;
				}
				index++;
			}
			
			List<Community> maxPath = paths.get(maxPathPosition);
			path.addAll(maxPath);
		}
		
		return path;
	}

	public double getMemberStability(Community comm_){
		List<Community> path = getMaxPath(comm_);
		double result = 0;
		int length = path.size();
		
		for(int i = 0; i<length-1; i++){
			Community pred = path.get(i);
			Community succ = path.get(i+1);
			
			List<Integer> predMembers = pred.getAllNodes();
			List<Integer> succMembers = succ.getAllNodes();
			int intersect = 0;
			
			if(predMembers.size()<= succMembers.size()){
				for(Integer member : predMembers){
					if(succMembers.contains(member))
						intersect++;
				}
			}
			else{
				for(Integer member : succMembers){
					if(predMembers.contains(member))
						intersect++;
				}
			}
			
			result += ((double)intersect/(predMembers.size()+succMembers.size()-intersect));
		}
		
		result /= (length-1);
		
		return (double)result;
	}

	public double getMetabolism(Community comm_){
		int traceSpan = getTraceSpan(comm_);
		double stability = getMemberStability(comm_);
		
		return traceSpan/stability;
	}

	public List<List<Double>> getMetrics(){
    	List<List<Double>> metrics = new ArrayList<List<Double>>();
    	
    	for(List<Community> snapshot : _timeLine){
    		double growth = 0;
    		double metabolism = 0;
    		int count=0;
    		if(!snapshot.isEmpty()){
	    		for(Community comm : snapshot){
	    			growth += comm.getGrowth();
	    			metabolism += getMetabolism(comm);
	    			count++;
	    		}
	    		List<Double> results = new ArrayList<Double>();
	    		double value1 = (Double)(growth/count);
	    		double value2 = (Double)(metabolism/count);
	    		if(value2 == Double.NaN){
	    			System.out.println("Error!");
	    		}
	    		results.add(value1);
	    		results.add(value2);
	    		metrics.add(results);
    		}
    		else{
    			List<Double> results = new ArrayList<Double>();
	    		results.add(growth);
	    		results.add(metabolism);
	    		metrics.add(results);
    		}   		
    	}
    	return metrics;
    }
}
