package sim.graph.algorithms.metrics;

import static edu.uci.ics.jung.algorithms.metrics.Metrics.clusteringCoefficients;

import java.util.Collection;
import java.util.Map;

import edu.uci.ics.jung.graph.Graph;

/**
 * TODO Purpose
 *
 * @author biggie 
 * @date Nov 3, 2011
 */
public class LowOrderGraphMetrics {

    /**
     * 
     * @author biggie
     * @name clusterIndex Purpose TODO
     * 
     * @param
     * @return Double
     */
    public static <V, E> Double avgClusterCoeff(Graph<V, E> g_) {
	Map<V, Double> ciMap = clusteringCoefficients(g_);
	double ciAvg = 0.0;
	for (Double ci : ciMap.values()) {
	    ciAvg += ci;
	}
	return ciAvg / ciMap.size();
    }

    /**
     * 
     * TODO Purpose
     * 
     * @params
     * @return Double
     * @author biggie
     * @param <E>
     * @param <V>
     */
    public static <V, E> Double avgDeg(Graph<V, E> g_) {
	Collection<V> agents = g_.getVertices();
	double avgDeg = 0.0;
	for (V ag : agents) {
	    avgDeg += g_.outDegree(ag);
	}
	return avgDeg / (g_.getVertexCount() + 0.0);
    }

}
