package sim.agents.social;

import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.graph.utils.Edge;
import edu.uci.ics.jung.graph.Graph;

public class DBWriterAgent implements Steppable {


    /**
     * 
     */
    private static final long serialVersionUID = 4420057740757549336L;

    public DBWriterAgent() {
    }

    /**
     * 
     */
    public void step(SimState state_) {
	@SuppressWarnings("unchecked")
	SocialSim<Agent, String> socSim = (SocialSim<Agent, String>) state_;
	writeDeltaToDB(socSim);
    }

    /**
     * @param socSim
     */
    private void writeDeltaToDB(SocialSim<Agent, String> socSim) {
	Graph<Agent, Edge> graphChanges = socSim._temporalNetwork;
	int id = socSim.getSimID();
	int step = (int) socSim.schedule.getSteps();

	int edges = graphChanges.getEdgeCount();
	if (edges > 0) {

	    boolean changes = false;
	    for (Edge e : graphChanges.getEdges()) {
		int from = e.getSource();
		int to = e.getDest();
		boolean createEdge = e.addEdge();

		socSim.getDBManager().addEdge(id, step, from, to, createEdge);

		changes = true;
	    }

	    if (changes) {
		socSim.getDBManager().insertEdges();
	    }
	}
	socSim.resetTemporalNetwork();
    }
}
