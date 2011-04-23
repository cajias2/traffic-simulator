/**
 * 
 */
package sim.mason;

import static edu.uci.ics.jung.algorithms.metrics.Metrics.clusteringCoefficients;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import sim.agents.Agent;
import sim.field.network.Edge;
import sim.field.network.Network;
import sim.graph.social.link.FriendLink;
import social.links.SimpleFriendLink;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

;
/**
 * @author biggie
 *
 */
public class AgentNetwork extends Network {


    private final Graph<Agent, FriendLink> _jungGraph;
    private UndirectedGraph<Agent, FriendLink> _jGraphT;

    public AgentNetwork() {
	super();
	_jungGraph = new UndirectedSparseGraph<Agent, FriendLink>();
	_jGraphT = new SimpleGraph<Agent, FriendLink>(SimpleFriendLink.class);
    }

    public AgentNetwork(boolean directed_) {
	super(directed_);
	_jungGraph = new UndirectedSparseGraph<Agent, FriendLink>();

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
	_jungGraph.addEdge((FriendLink) info_, pair);
	_jGraphT.addEdge((Agent) from_, (Agent) to_);
    }

    /**
     * Jung/JGraphT hook
     */
    @Override
    protected void addJungNode(Object node_) {
	_jungGraph.addVertex((Agent) node_);
	_jGraphT.addVertex((Agent) node_);
    }

    /**
     * Jung API wrapper
     * 
     * @return
     */
    public Collection<Agent> getJungNodes() {
	return _jungGraph.getVertices();
    }

    public Set<Agent> getJGraphNodes() {
	return _jGraphT.vertexSet();
    }


    /**
     * Jung API wrapper
     * 
     * @return
     */
    public Collection<FriendLink> getJungEdges() {
	return _jungGraph.getEdges();
    }

    public Set<?> getJGraphEdges() {
	return _jGraphT.edgeSet();
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
	return _jungGraph.findEdge(a_, b_) != null;
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
	Map<Agent, Double> ciMap = clusteringCoefficients(_jungGraph);
	double ciAvg = 0.0;
	for (Double ci : ciMap.values()) {
	    ciAvg += ci;
	}
	return ciAvg / ciMap.size();
    }

    public Double avgDeg() {
	Collection<Agent> agents = _jungGraph.getVertices();
	double avgDeg = 0.0;
	for (Agent ag : agents) {
	    avgDeg += _jungGraph.outDegree(ag);
	}
	return avgDeg / (_jungGraph.getVertexCount() + 0.0);
    }

    public final Graph<Agent, FriendLink> getJGraph() {
	return _jungGraph;
    }

    public final UndirectedGraph<Agent, FriendLink> getJgraphT() {
	return _jGraphT;
    }
    
    /**
     * @author agpardo
     * 
     * @param agent_ Agent Identifier
     * @return The outDegree of the specified agent
     */
    public int degreeOf(Integer agent_){
    	Collection<Agent> agents = _jungGraph.getVertices();
    	for (Agent ag : agents) {
    		if(ag.getID()== agent_)
    			return _jungGraph.outDegree(ag);
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
