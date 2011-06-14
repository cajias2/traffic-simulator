/**
 * 
 */
package sim.mason;

import static edu.uci.ics.jung.algorithms.metrics.Metrics.clusteringCoefficients;

import java.util.Collection;
import java.util.Map;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import sim.agents.Agent;
import sim.field.network.Edge;
import sim.field.network.Network;
import sim.graph.social.link.FriendLink;
import sim.util.Bag;
import app.social.links.SimpleFriendLink;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * @author biggie
 *
 */
public class AgentNetwork extends Network {


    private static final long serialVersionUID = 3753409368682500764L;
    private final Graph<Agent, FriendLink> _graph;

    public AgentNetwork() {
	super();
	_graph = new UndirectedSparseGraph<Agent, FriendLink>();
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
	_graph.addEdge((FriendLink) info_, pair);
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
    public Collection<FriendLink> getJungEdges() {
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

    public final Graph<Agent, FriendLink> getJGraph() {
	return _graph;
    }

    public Graph<Agent, FriendLink> getGraphSnapshot() {
	Graph<Agent, FriendLink> ss = new UndirectedSparseGraph<Agent, FriendLink>();
	for (Agent v : _graph.getVertices()) {
	    ss.addVertex(v);
	}
	for (FriendLink e : _graph.getEdges()) {
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
     * @return UndirectedGraph<Agent,FriendLink>
     * @author biggie
     */
    public static UndirectedGraph<Integer, FriendLink> adjListToJGraphTList(Edge[][] adjList_) {
	UndirectedGraph<Integer, FriendLink> graph = new SimpleGraph<Integer, FriendLink>(SimpleFriendLink.class);

	for (int i = 0; i < adjList_.length; i++) {
	    graph.addVertex(i);
	}
	for (int i = 0; i < adjList_.length; i++) {
	    for (int j = i + 1; j < adjList_[i].length; j++) {
		if (null != adjList_[i][j]) {
		    graph.addEdge(i, j);
		}
	    }
	}
	return graph;
    }

    /**
     * TODO Purpose
     * 
     * @param
     * @return Graph<Integer,FriendLink>
     * @author biggie
     */
    public static Graph<Integer, FriendLink> adjListToJungGraph(Edge[][] adjList_) {
	Graph<Integer, FriendLink> graph = new UndirectedSparseGraph<Integer, FriendLink>();
	for (int i = 0; i < adjList_.length; i++) {
	    graph.addVertex(i);
	}
	for (int i = 0; i < adjList_.length; i++) {
	    for (int j = i + 1; j < adjList_[i].length; j++) {
		if (null != adjList_[i][j]) {
		    graph.addEdge(new SimpleFriendLink(), i, j);
		}
	    }
	}
	return graph;
    }

}
