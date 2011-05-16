package sim.graph.social.metrics.commTracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.uci.ics.jung.graph.Graph;

import sim.field.network.Edge;
import sim.graph.social.link.FriendLink;

public class Community {
	private List<Integer> _members, _core;
	private List<Community> _predecessors, _successors;
	private int _age, _maxPathLength;
	private Community _maxSuccessor;
	public String name;
	private static int numCommunities = 0;
	private List<List<Community>> _traces;
	
	public Community(Set<Integer> comm_, Graph<Integer,FriendLink> graph_) {
		_members = new ArrayList<Integer>();
		_members.addAll(comm_);
		_traces = new ArrayList<List<Community>>();
		coreDectection(comm_, graph_);
		_predecessors = new ArrayList<Community>();
		_successors = new ArrayList<Community>();
		_maxPathLength = 0;
		_maxSuccessor = null;
		numCommunities++;
	}
	
	public Community(String n_) {
		_members = new ArrayList<Integer>();
		_traces = new ArrayList<List<Community>>();
		_predecessors = new ArrayList<Community>();
		_successors = new ArrayList<Community>();
		_maxPathLength = 0;
		_maxSuccessor = null;
		name = n_;
		numCommunities++;
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

			for (i = 0; i < nodos.size(); i++) {
				for (int j = (i + 1); j < nodos.size(); j++) {
					Integer node1 = nodos.elementAt(i);
					Integer node2 = nodos.elementAt(j);
					
					FriendLink edge1 = graph_.findEdge(node1, node2);
					FriendLink edge2 = graph_.findEdge(node2, node1);
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
		if(!_predecessors.contains(pred_)){
			_predecessors.add(pred_);	
		}
	}
	
	public List<List<Community>> getTraces(){
		return _traces;
	}
	
	public List<Community> getMaxTraceSpan(){
		int maxLength = 0;
		int position = -1;
		for(int i = 0; i<_traces.size(); i++){
			if( _traces.get(i).size() > maxLength){
				maxLength = _traces.get(i).size();
				position = i;
			}
		}
		
		if(position != -1){
			return _traces.get(position);
		}
		return new ArrayList<Community>();
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
	
	}

	
	public void addSuccessors(List<Community> succs_){
		for(Community succ : succs_){
			if(!_successors.contains(succ))
				_successors.add(succ);
		}
		
	}
	
	public List<Community> getPredecessors(){
		return _predecessors;
	}
	
	public List<Community> getSuccessors(){
		return _successors;
	}

	@Override
	public String toString(){
		String community = "";
		if(_members.isEmpty()){
			community = name;
		}
		else{
			community = "( ";
		
		for(Integer node : _core){
			community += node + " ";
		}
		community += "| ";
		for(Integer node : _members){
			community += node + " ";
		}
		community += ")";
		}
		return community;
	}

	public int getSize(){
		return _members.size();
	}
	
	public int getAge(){
		return _age;
	}
		
	public void buildSpanTraces() {
		if (!_predecessors.isEmpty()) {
			for (Community predecessor : _predecessors) {
				List<List<Community>> predTraces = predecessor.getTraces();

				if (!predTraces.isEmpty()) {
					for (List<Community> trace : predTraces) {
						List<Community> newTrace = new ArrayList<Community>();
						newTrace.addAll(trace);
						newTrace.add(predecessor);
						_traces.add(newTrace);
					}

				} else {
					_traces.add(new ArrayList<Community>());
					int size = _traces.size();
					_traces.get(size - 1).add(predecessor);
				}

			}
		}
		
		List<Community> traceSpan = getMaxTraceSpan();
		_age = traceSpan.size()+1;
	}
	
	public boolean equals(Community comm_){
		return (_members.containsAll(comm_._members) && _core.containsAll(comm_._core));
	}
	
	/**
	 * TODO: Revisar este metodo en el caso de que el grafo sea dirigido.
	 * @param part1_ Primer nodo
	 * @param part2_ Segundo nodo
	 * @param matrix_ Matriz de adyacencia de la red
	 * @return Devuelve true si part1_ y part_2 est�n conectados (sin tener en cuenta la direcci�n)
	 */
	public boolean existEdge(Integer part1_, Integer part2_, Edge[][] matrix_){
		boolean result = false;
		int counter = 0;
		while (!result && counter < matrix_.length) {
			Edge[] fila = matrix_[counter];
			counter++;

			
			for (Edge ed : fila) {
				if (ed != null) {
					Integer from = (Integer) ed.getFrom();
					Integer to = (Integer) ed.getTo();

					if ((from == part1_) && (to == part2_)) {
						result = true;
					} else if ((from == part2_) && (to == part1_)) {
						result = true;
					}
				}
			}
		}

		return result;
	}

	
	
	public int getMaxPathLength(){
		return _maxPathLength;
	}
	
	public Community getMaxSuccessor(){
		return _maxSuccessor;
	}
	
	public static int getNumCommunities(){
		return numCommunities;
	}
}