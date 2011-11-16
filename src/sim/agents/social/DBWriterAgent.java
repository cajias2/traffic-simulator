package sim.agents.social;

import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.graph.utils.Edge;

public class DBWriterAgent implements Steppable {

    /**
     * 
     */
    private static final long serialVersionUID = 4420057740757549336L;
    private final int SNAPSHOT;

    /**
     * TODO Purpose
     * 
     * @param Number
     *            of steps to capture in each write.
     * @author biggie
     */
    public DBWriterAgent(int ss_) {
	SNAPSHOT = ss_;
    }

    /**
     * @param socSim
     */
    private void writeDeltaToDB(SocialSim<Agent, String> socSim) {
	for (Edge e : socSim._temporalNetwork.getEdges()) {
	    socSim.getDBManager().addEdge(socSim.getSimID(), socSim.schedule.getSteps(),
		    socSim._temporalNetwork.getEndpoints(e).getFirst(),
		    socSim._temporalNetwork.getEndpoints(e).getSecond(), e.isCreate());
	}
	socSim.getDBManager().insertEdges();
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.engine.Steppable#step(sim.engine.SimState)
     */
    @Override
    public void step(SimState state_) {
	@SuppressWarnings("unchecked")
	SocialSim<Agent, String> socSim = (SocialSim<Agent, String>) state_;
	if (0 == (socSim.schedule.getSteps() % SNAPSHOT)) {
	    writeDeltaToDB(socSim);
	    socSim.resetTemporalNetwork();
	}
    }
}
