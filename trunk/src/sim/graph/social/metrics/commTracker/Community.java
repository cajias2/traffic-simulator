package sim.graph.social.metrics.commTracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.uci.ics.jung.graph.Graph;

import sim.field.network.Edge;
import sim.graph.social.link.FriendLink;
import sim.mason.AgentNetwork;

public class Community {
	private List<Integer> _members, _core;
	private List<Community> _predecessors, _successors;
	private int _age, _traceSpan, _maxPathLength;
	private Community _maxSuccessor;
	
	public Community(Set<Integer> comm_, Graph<Integer,FriendLink> graph_) {
		_members = new ArrayList<Integer>();
		_members.addAll(comm_);
		_traceSpan = -1;
		coreDectection(comm_, graph_);
		_predecessors = new ArrayList<Community>();
		_successors = new ArrayList<Community>();
		_maxPathLength = 0;
		_maxSuccessor = null;
	}
	
	private void coreDectection(Set<Integer> comm_, Graph<Integer,FriendLink> graph_) {
		boolean sameDegree = true;
		Iterator<Integer> iterador = comm_.iterator();
		int degree = 0;
		boolean firstNode = true;

		while (iterador.hasNext() && (sameDegree == true)) {
			Integer nodo = iterador.next();
			if (firstNode) {
				degree = graph_.degree(nodo);
				firstNode = false;
			} else
				sameDegree &= (graph_.degree(nodo) == degree);
		}

		if (sameDegree) {
			_core = new ArrayList<Integer>();
			_core.addAll(comm_);
		} else {
			int nodes = comm_.toArray().length;
			Vector<Integer> nodos = new Vector<Integer>();

			int[] centralDegree = new int[nodes];

			int i = 0;
			for (Integer nodo : comm_) {
				centralDegree[i] = 0;
				i++;
				nodos.add(nodo);
			}

//			Edge[][] adjacencyMatrix = graph_.getAdjacencyMatrix();			

			for (i = 0; i < nodos.size(); i++) {
				for (int j = (i + 1); j < nodos.size(); j++) {
					Integer node1 = nodos.elementAt(i);
					Integer node2 = nodos.elementAt(j);
					
					FriendLink edge1 = graph_.findEdge(node1, node2);
					FriendLink edge2 = graph_.findEdge(node2, node1);
//					boolean connected = existEdge(node1, node2, adjacencyMatrix);
//					if (connected) {
					if ((edge1 != null) || (edge2!= null)){
						int grado1 = graph_.degree(node1);
						int grado2 = graph_.degree(node2);

						if (grado1 < grado2) {
							centralDegree[i] -= Math.abs((grado1 - grado2));
							centralDegree[j] += Math.abs((grado1 - grado2));
						} else {
							centralDegree[i] += Math.abs((grado1 - grado2));
							centralDegree[j] -= Math.abs((grado1 - grado2));
						}
					}
				}
			}

			_core = new ArrayList<Integer>();
			for (i = 0; i < nodos.size(); i++) {
				if (centralDegree[i] >= 0)
					_core.add(nodos.elementAt(i));

			}
		}
	}

	public List<Integer> getCoreNodes(){
		return _core;
	}
	
	public List<Integer> getAllNodes(){
		return _members;
	}
	
	public void addPredecessor(Community pred_){
		if(!_predecessors.contains(pred_))
			_predecessors.add(pred_);		
	}
	
	public void addPredecessors(List<Community> preds_){
		for(Community comm : preds_){
			if(!_predecessors.contains(comm))
				_predecessors.add(comm);
		}
	}
	
	public void addSuccessor(Community succ_){
		if(!_successors.contains(succ_))
			_successors.add(succ_);
		
		//computeMaxPath();
	}

	public void addSuccessors(List<Community> succs_){
		for(Community succ : succs_){
			if(!_successors.contains(succ))
				_successors.add(succ);
		}
		
		//computeMaxPath();
	}
	
	public List<Community> getPredecessors(){
		return _predecessors;
	}
	
	public List<Community> getSuccessors(){
		return _successors;
	}

	@Override
	public String toString(){
		String community = "( ";
		for(Integer node : _core){
			community += node + " ";
		}
		community += "| ";
		for(Integer node : _members){
			community += node + " ";
		}
		community += ")";
		return community;
	}

	public int getSize(){
		return _members.size();
	}
	
	public int getAge(){
		return _age;
	}
	
	public void computeAge(){
		if(!_predecessors.isEmpty()){
			int maxAge = 0;
			for(Community pred : _predecessors){
				int predAge = pred.getAge();
				if(predAge > maxAge)
					maxAge = predAge;
			}
			_age = maxAge+1;
		}			
		else
			_age = 1;
	}
	
	public int getTraceSpan(){
		if(_traceSpan != -1)
			return _traceSpan;
		
		if(!_predecessors.isEmpty()){
			int maxTrace = 0;
			for(Community pred : _predecessors){
				int predTrace = pred.getTraceSpan();
				if(predTrace > maxTrace)
					maxTrace = predTrace;
			}
			_traceSpan = maxTrace+1;
			return _traceSpan;
		}
		
		_traceSpan = 1;	
		return _traceSpan;
	}
	
	public double getGrowth(){			
		return _members.size()/_age;
	}
	
	public boolean equals(Community comm_){
		return (_members.containsAll(comm_._members) && _core.containsAll(comm_._core));
	}
	
	public boolean existEdge(Integer part1_, Integer part2_, Edge[][] matrix_){
		boolean result = false;
		int counter = 0;
		while( !result && counter < matrix_.length){
			Edge[] fila = matrix_[counter];
			counter++;
			
			for(Edge ed : fila){
				if(ed!=null){
				Integer from = (Integer)ed.getFrom();
				Integer to = (Integer)ed.getTo();
				
				if((from==part1_) && (to == part2_) ){
					result = true;
				}
				else if((from==part2_) && (to == part1_) ){
					result = true;
				}
				}
			}
		}
		
		return result;
	}

	public void computeMaxPath(){
			int maxPos = 0;
			int maxLength = -1;
			for(int i=0; i<_successors.size(); i++){
				Community succ = _successors.get(i);
				int maxPath = succ.getMaxPathLength();
				if(maxPath > maxLength){
					maxLength = maxPath;
					maxPos = i;
				}
			}
			_maxPathLength = maxLength+1;
			_maxSuccessor = _successors.get(maxPos);
			
			for(Community pred : _predecessors){
				pred.computeMaxPath();
			}		
	}
	
	public int getMaxPathLength(){
		return _maxPathLength;
	}
	
	public Community getMaxSuccessor(){
		return _maxSuccessor;
	}
}
