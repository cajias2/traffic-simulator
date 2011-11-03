/**
 * 
 */
package sim.mason;

import static edu.uci.ics.jung.algorithms.metrics.Metrics.clusteringCoefficients;

import java.util.Collection;
import java.util.Map;

import sim.agents.Agent;
import sim.field.network.Edge;
import sim.field.network.Network;
import sim.util.Bag;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * @author biggie
 *
 */
public class AgentNetwork extends Network {


    private static final long serialVersionUID = 3753409368682500764L;
    private final Graph<Agent, Number> _graph;

    public AgentNetwork() {
	super();
	_graph = new UndirectedSparseGraph<Agent, Number>();
    }



    /**
     * Jung/JGraphT hook
     * 
     * @param from_
     * @param to_
     * @param info_
     */
    @Override
    protected void addJungEdge(Object from_, Object to_, Object info_) {
	Pair<Agent> pair = new Pair<Agent>((Agent) from_, (Agent) to_);
	_graph.addEdge((Number) info_, pair);
    }

    /**
     * Jung/JGraphT hook
     */
    @Override
    protected void addJungNode(Object node_) {
	_graph.addVertex((Agent) node_);
    }

    public void removeEdge(Object from_, Object to_) {
	removeEdgeNetwork(from_, to_);
	removeEdgeJung(from_, to_);
    }

    /**
     * @param from_
     * @param to_
     */
    private void removeEdgeJung(Object from_, Object to_) {
	_graph.removeEdge(_graph.findEdge((Agent) from_, (Agent) to_));

    }

    /**
     * @param from_
     * @param to_
     */
    private void removeEdgeNetwork(Object from_, Object to_) {
	Bag edges = new Bag();
	getEdges(from_, edges);
	for (int i = 0; i < edges.size(); i++) {
	    Edge edge = (Edge) edges.get(i);
	    if (edge.from() == to_ || edge.to() == to_) {
		removeEdge(edge);
	    }
	}

    }

    /**
     * Jung API wrapper
     * 
     * @return
     */
    public Collection<Agent> getJungNodes() {
	return _graph.getVertices();
    }

    /**
     * Jung API wrapper
     * 
     * @return
     */
    public Collection<Number> getJungEdges() {
	return _graph.getEdges();
    }

    /**
     * 
     * @author biggie
     * @name hasEdge Purpose TODO
     * 
     * @param
     * @return boolean
     */
    public boolean hasEdge(Agent a_, Agent b_) {
	return _graph.findEdge(a_, b_) != null;
    }

    /**
     * 
     * @author biggie
     * @name clusterIndex Purpose TODO
     * 
     * @param
     * @return Double
     */
    public Double avgClusterCoeff() {
	Map<Agent, Double> ciMap = clusteringCoefficients(_graph);
	double ciAvg = 0.0;
	for (Double ci : ciMap.values()) {
	    ciAvg += ci;
	}
	return ciAvg / ciMap.size();
    }

    public Double avgDeg() {
	Collection<Agent> agents = _graph.getVertices();
	double avgDeg = 0.0;
	for (Agent ag : agents) {
	    avgDeg += _graph.outDegree(ag);
	}
	return avgDeg / (_graph.getVertexCount() + 0.0);
    }

    public final Graph<Agent, Number> getJGraph() {
	return _graph;
    }

    public Graph<Agent, Number> getGraphSnapshot() {
	Graph<Agent, Number> ss = new UndirectedSparseGraph<Agent, Number>();
	for (Agent v : _graph.getVertices()) {
	    ss.addVertex(v);
	}
	for (Number e : _graph.getEdges()) {
	    ss.addEdge(e, _graph.getEndpoints(e).getFirst(), _graph.getEndpoints(e).getSecond());
	}
	return ss;

    }
    
    
    /**
     * @author agpardo
     * 
     * @param agent_ Agent Identifier
     * @return The outDegree of the specified agent
     */
    public int degreeOf(Integer agent_){
	Collection<Agent> agents = _graph.getVertices();
    	for (Agent ag : agents) {
    		if(ag.getID()== agent_)
		return _graph.outDegree(ag);
    	}
    	
    	return -1;
    }


    /**
     * TODO Purpose
     * 
     * @param
     * @return Graph<Integer,Number>
     * @author biggie
     */
    public static Graph<Integer, Number> adjListToJungGraph(Edge[][] adjList_) {
	Graph<Integer, Number> graph = new UndirectedSparseGraph<Integer, Number>();
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
