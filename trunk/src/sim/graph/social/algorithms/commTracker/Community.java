package sim.graph.social.algorithms.commTracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import sim.field.network.Edge;
import sim.graph.social.link.FriendLink;
import edu.uci.ics.jung.graph.Graph;

/**
 * 
 * TODO Purpose
 * 
 * @author antonio
 * @param <T>
 * @date May 17, 2011
 */
public class Community<T> {
    private static int count_ = 0;
    private final int ID;
    private final List<T> _members;
    private List<T> _core;
    private final List<Community<T>> _predecessors;
    private final List<Community<T>> _successors;
    private int _age;
    private int _maxPredPathLen = 0;
    private Community<T> _maxPredecessor;

    // private final List<List<Community<T>>> _traces;

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return int
     * @author antonio
     */
    public static int getNumCommunities() {
	return count_;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @param
     * @author antonio
     */
    public Community(Set<T> comm_, Graph<T, FriendLink> graph_) {
	ID = count_++;
	_members = new ArrayList<T>();
	_members.addAll(comm_);
	// _traces = new ArrayList<List<Community<T>>>();
	coreDectection(comm_, graph_);
	_predecessors = new ArrayList<Community<T>>();
	_successors = new ArrayList<Community<T>>();
	_maxPredPathLen = 0;
    }

    /**
     * 
     * Test.
     * 
     * @param
     * @author antonio
     */
    public Community() {
	ID = count_++;
	_members = new ArrayList<T>();
	// _traces = new ArrayList<List<Community<T>>>();
	_predecessors = new ArrayList<Community<T>>();
	_successors = new ArrayList<Community<T>>();
	_maxPredPathLen = 0;
	_maxPredecessor = null;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    public void addPredecessor(Community<T> pred_) {
	if (!_predecessors.contains(pred_)) {
	    _predecessors.add(pred_);
	    if ((pred_.getMaxPredPathLen() + 1) > _maxPredPathLen) {
		_maxPredecessor = pred_;
		_maxPredPathLen = pred_.getMaxPredPathLen() + 1;
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
    public void addPredecessors(List<Community<T>> preds_) {
	for (Community<T> comm : preds_) {
	    if (!_predecessors.contains(comm))
		_predecessors.add(comm);
	}
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    public void addSuccessor(Community<T> succ_) {
	if (!_successors.contains(succ_))
	    _successors.add(succ_);

    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    public void addSuccessors(List<Community<T>> succs_) {
	for (Community<T> succ : succs_) {
	    if (!_successors.contains(succ))
		_successors.add(succ);
	}

    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return int
     * @author antonio
     */
    public int getSize() {
	return _members.size();
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return int
     * @author antonio
     */
    public int getAge() {
	return _age;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return int
     * @author antonio
     */
    public int getMaxPredPathLen() {
	return _maxPredPathLen;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return Community
     * @author antonio
     */
    public Community<T> getMaxSuccessor() {
	return _maxPredecessor;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return List<Community>
     * @author antonio
     */
    public List<Community<T>> getPredecessors() {
	return _predecessors;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return List<Community>
     * @author antonio
     */
    public List<Community<T>> getSuccessors() {
	return _successors;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return List<Integer>
     * @author antonio
     */
    public List<T> getCoreNodes() {
	return _core;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return List<Integer>
     * @author antonio
     */
    public List<T> getAllNodes() {
	return _members;
    }

    /**
     * TODO: Revisar este metodo en el caso de que el grafo sea dirigido.
     * 
     * @param part1_
     *            Primer nodo
     * @param part2_
     *            Segundo nodo
     * @param matrix_
     *            Matriz de adyacencia de la red
     * @return Devuelve true si part1_ y part_2 estann conectados (sin tener en
     *         cuenta la direcci�n)
     */
    public boolean existEdge(Integer part1_, Integer part2_, Edge[][] matrix_) {
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

    // /**
    // *
    // * TODO Purpose
    // *
    // * @params
    // * @return void
    // * @author antonio
    // */
    // public void buildSpanTraces() {
    // if (!_predecessors.isEmpty()) {
    // for (Community<T> predecessor : _predecessors) {
    // List<List<Community<T>>> predTraces = predecessor.getTraces();
    //
    // if (!predTraces.isEmpty()) {
    // for (List<Community<T>> trace : predTraces) {
    // List<Community<T>> newTrace = new ArrayList<Community<T>>();
    // newTrace.addAll(trace);
    // newTrace.add(predecessor);
    // _traces.add(newTrace);
    // }
    //
    // } else {
    // _traces.add(new ArrayList<Community<T>>());
    // int size = _traces.size();
    // _traces.get(size - 1).add(predecessor); // TODO only add
    // // node. Not lists
    // }
    //
    // }
    // }
    //
    // List<Community<T>> traceSpan = getMaxTraceSpan();
    // _age = traceSpan.size() + 1;
    // }

    /**
     * 
     */
    @Override
    public String toString() {
	String community = "_" + ID + "( ";

	for (T node : _core) {
	    community += node + " ";
	}
	community += "| ";
	for (T node : _members) {
	    community += node + " ";
	}
	community += ")";

	return community;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return boolean
     * @author antonio
     */
    @Override
    public boolean equals(Object obj_) {
	boolean isEq = false;
	if (obj_ instanceof Community) {
	    Community<T> com = (Community<T>) obj_;
	    isEq = (_members.containsAll(com._members) && _core.containsAll(com._core));
	} else {
	    isEq = super.equals(obj_);
	}
	return isEq;
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author antonio
     */
    private void coreDectection(Set<T> comm_, Graph<T, FriendLink> graph_) {
	boolean sameDegree = true;
	Iterator<T> iterador = comm_.iterator();
	int degree = 0;
	boolean firstNode = true;

	while (iterador.hasNext() && (sameDegree == true)) {
	    T nodo = iterador.next();
	    if (firstNode) {
		degree = graph_.degree(nodo);
		firstNode = false;
	    } else
		sameDegree &= (graph_.degree(nodo) == degree);
	}

	if (sameDegree) {
	    _core = new ArrayList<T>();
	    _core.addAll(comm_);
	} else {
	    int nodes = comm_.toArray().length;
	    Vector<T> nodos = new Vector<T>();

	    int[] centralDegree = new int[nodes];

	    int i = 0;
	    for (T nodo : comm_) {
		centralDegree[i] = 0;
		i++;
		nodos.add(nodo);
	    }

	    for (i = 0; i < nodos.size(); i++) {
		for (int j = (i + 1); j < nodos.size(); j++) {
		    T node1 = nodos.elementAt(i);
		    T node2 = nodos.elementAt(j);

		    FriendLink edge1 = graph_.findEdge(node1, node2);
		    FriendLink edge2 = graph_.findEdge(node2, node1);
		    if ((edge1 != null) || (edge2 != null)) {
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

	    _core = new ArrayList<T>();
	    for (i = 0; i < nodos.size(); i++) {
		if (centralDegree[i] >= 0)
		    _core.add(nodos.elementAt(i));
	    }
	}
    }

    /**
     * Returns predecessor with longest span trace by default..
     * 
     * @params
     * @return Community<T>
     * @author biggie
     */
    public Community<T> getPredecessor() {
	// TODO Auto-generated method stub
	return _maxPredecessor;
    }
}