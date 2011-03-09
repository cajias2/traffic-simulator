/**
 * 
 */
package sim.mason;

import static edu.uci.ics.jung.algorithms.metrics.Metrics.clusteringCoefficients;

import java.util.Collection;
import java.util.Map;

import sim.agents.Agent;
import sim.field.network.Network;
import sim.graph.social.link.FriendLink;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

;
/**
 * @author biggie
 *
 */
public class AgentNetwork extends Network {

    private final Graph<Agent, FriendLink> _jgraph;

    public AgentNetwork() {
	super();
	_jgraph = new UndirectedSparseGraph<Agent, FriendLink>();
    }

    public AgentNetwork(boolean directed_) {
	super(directed_);
	_jgraph = new UndirectedSparseGraph<Agent, FriendLink>();

    }

    /**
     * Jung hook
     * 
     * @param from_
     * @param to_
     * @param info_
     */
    @Override
    protected void addJungEdge(Object from_, Object to_, Object info_) {
	Pair<Agent> pair = new Pair<Agent>((Agent) from_, (Agent) to_);
	_jgraph.addEdge((FriendLink) info_, pair);
    }

    /**
     * Jung hook
     */
    @Override
    protected void addJungNode(Object node_) {
	_jgraph.addVertex((Agent) node_);
    }

    /**
     * Jung API wrapper
     * 
     * @return
     */
    public Collection<Agent> getJungNodes() {
	return _jgraph.getVertices();
    }

    /**
     * Jung API wrapper
     * 
     * @return
     */
    public Collection<FriendLink> getJungEdges() {
	return _jgraph.getEdges();
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
	return _jgraph.getNeighbors(a_).contains(b_);
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
	Map<Agent, Double> ciMap = clusteringCoefficients(_jgraph);
	double ciAvg = 0.0;
	for (Double ci : ciMap.values()) {
	    ciAvg += ci;
	}
	return ciAvg / ciMap.size();
    }

    public Double avgDeg() {
	Collection<Agent> agents = _jgraph.getVertices();
	double avgDeg = 0.0;
	for (Agent ag : agents) {
	    avgDeg += _jgraph.outDegree(ag);
	}
	return avgDeg / _jgraph.getVertexCount();
    }

    public final Graph<Agent, FriendLink> getJGraph() {
	return _jgraph;
    }

}
