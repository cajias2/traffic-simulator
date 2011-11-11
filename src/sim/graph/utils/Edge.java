package sim.graph.utils;

public class Edge {

	private boolean _addEdge;
	private int _from, _to;
	
	public Edge(int source_, int dest_, boolean creation_){
		_from = source_;
		_to = dest_;
		_addEdge = creation_;
	}
	
	public boolean addEdge(){
		return _addEdge;
	}
	
	public int getSource(){
		return _from;
	}
	
	public int getDest(){
		return _to;
	}
	
}
