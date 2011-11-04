/**
 * 
 */
package sim.mason;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * @author biggie
 * @param <T>
 * @param <V>
 * 
 */
public class AgentNetwork<V, E> extends UndirectedSparseGraph<V, E> {

    private static final long serialVersionUID = 4272746830046079376L;

    public AgentNetwork() {
	super();
    }


    public Graph<V, E> getGraphSnapshot() {
	Graph<V, E> ss = new UndirectedSparseGraph<V, E>();
	for (V v : getVertices()) {
	    ss.addVertex(v);
	}
	for (E e : getEdges()) {
	    ss.addEdge(e, getEndpoints(e).getFirst(), getEndpoints(e).getSecond());
	}
	return ss;
    }



    /**
     * TODO Purpose
     * 
     * @param
     * @return Graph<Integer,Number>
     * @author biggie
     */
    public static Graph<Number, Number> adjListToJungGraph(Integer[][] adjList_) {
	Graph<Number, Number> graph = new UndirectedSparseGraph<Number, Number>();
	for (int i = 0; i < adjList_.length; i++) {
	    graph.addVertex(i);
	}
	for (int i = 0; i < adjList_.length; i++) {
	    for (int j = i + 1; j < adjList_[i].length; j++) {
		if (null != adjList_[i][j]) {
		    graph.addEdge(new Integer(1), i, j);
		}
	    }
	}
	return graph;
    }
}
