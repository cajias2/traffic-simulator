/**
 * 
 */
package sim.mason;

import java.util.Collection;

import sim.agents.Agent;
import sim.field.network.Network;
import sim.graph.social.link.FriendLink;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * @author biggie
 *
 */
public class AgentNetwork extends Network {
    
    private Graph<Agent, FriendLink> _jgraph;

    public AgentNetwork() {
	super();
	_jgraph = new DirectedSparseGraph<Agent, FriendLink>();
    }

    public AgentNetwork(boolean directed_) {
	super(directed_);
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

}
