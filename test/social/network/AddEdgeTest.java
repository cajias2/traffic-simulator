package social.network;

import java.util.ArrayList;
import java.util.List;

import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.field.network.Network;
import sim.mason.AgentNetwork;
import social.links.SimpleFriendLink;

public class AddEdgeTest {

    private Network _graph;
    private List<Agent> _agLst;

    @BeforeClass
    public void setup() {
	_graph = new AgentNetwork(false);
	_agLst = new ArrayList<Agent>();
	SocialSim st = new SocialSim(System.currentTimeMillis());

	for (int i = 0; i <= 10; i++) {
	    Agent ag = new Agent(st);
	    _agLst.add(ag);
	    _graph.addNode(ag);
	}

    }

    @Test(groups = { "Smoke" })
    public void addEdge() {
	_graph.addEdge(_agLst.get(0), _agLst.get(1), new SimpleFriendLink(1.0));
	_graph.addEdge(_agLst.get(1), _agLst.get(9), new SimpleFriendLink(1.0));
	_graph.addEdge(_agLst.get(9), _agLst.get(0), new SimpleFriendLink(1.0));

  }
}
