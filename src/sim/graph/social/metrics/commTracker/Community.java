package sim.graph.social.metrics.commTracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import sim.field.network.Edge;
import sim.mason.AgentNetwork;

public class Community {
	private List<Integer> _members, _core;
	private List<Community> _predecessors, _successors;
		
	public Community(List<Integer> comm_, AgentNetwork graph_) {
		_members = new ArrayList<Integer>();
		_members.addAll(comm_);
		coreDectection(comm_, graph_);
		_predecessors = new ArrayList<Community>();
		_successors = new ArrayList<Community>();
	}

	private void coreDectection(List<Integer> comm_, AgentNetwork graph_) {
		boolean sameDegree = true;
		Iterator<Integer> iterador = comm_.iterator();
		int degree = 0;
		boolean firstNode = true;

		while (iterador.hasNext() || (sameDegree == true)) {
			Integer nodo = iterador.next();
			if (firstNode) {
				degree = graph_.degreeOf(nodo);
				firstNode = false;
			} else
				sameDegree &= (graph_.degreeOf(nodo) == degree);
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

			Edge[][] adjacencyMatrix = graph_.getAdjacencyMatrix();

			for (i = 0; i < nodos.size(); i++) {
				for (int j = (i + 1); j < nodos.size(); j++) {

					if ((adjacencyMatrix[i][j] != null)|| (adjacencyMatrix[j][i] != null)) {
						Integer node1 = nodos.elementAt(i);
						Integer node2 = nodos.elementAt(j);

						int grado1 = graph_.degreeOf(node1);
						int grado2 = graph_.degreeOf(node2);

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
}
