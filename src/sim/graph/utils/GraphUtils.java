package sim.graph.utils;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * TODO Purpose
 * 
 * @author biggie
 * @param <V>
 * @param <E>
 * @date Nov 7, 2011
 */
public class GraphUtils {

    /**
     * 
     * Makes an UndirectedSparseGraph copy of a graph Graph
     * 
     * @params
     * @return Graph<V,E>
     * @author biggie
     */
    public static <V, E> Graph<V, E> cloneGraph(Graph<V, E> g_) {
	Graph<V, E> ss = new UndirectedSparseGraph<V, E>();
	for (V v : g_.getVertices()) {
	    ss.addVertex(v);
	}
	for (E e : g_.getEdges()) {
	    ss.addEdge(e, g_.getEndpoints(e).getFirst(), g_.getEndpoints(e).getSecond());
	}
	return ss;
    }
}
