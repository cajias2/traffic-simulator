package sim.agents.social;

import sim.agents.Agent;
import sim.app.social.SocialSimBatchRunner;
import sim.engine.SimState;
import sim.graph.utils.Edge;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class DBWriterAgent extends Agent {

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
    public DBWriterAgent(SimState state_) {
	super(state_);
	SNAPSHOT = ((SocialSimBatchRunner) state_).getSnapshotInterval();
    }

    /**
     * @param socSim
     */
    private void writeDeltaToDB(SocialSimBatchRunner<Agent, String> socSim) {
	socSim.getDBManager().insertNewSimStep(socSim.getSimID(), socSim.schedule.getSteps());
	for (Edge e : _deltaGraph.getEdges()) {
	    socSim.getDBManager().addEdgeToBatch(socSim.getSimID(), socSim.schedule.getSteps(),
		    _deltaGraph.getEndpoints(e).getFirst().getID(), _deltaGraph.getEndpoints(e).getSecond().getID(),
		    e.isCreate());
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
	SocialSimBatchRunner<Agent, String> socSim = (SocialSimBatchRunner<Agent, String>) state_;
	if (0 == (socSim.schedule.getSteps() % SNAPSHOT)) {
	    writeDeltaToDB(socSim);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see sim.agents.Agent#afterStep(sim.app.social.SocialSimBatchRunner)
     */
    @Override
    protected void afterStep(SocialSimBatchRunner<Agent, String> state_) {
	super.afterStep(state_);
	    _deltaGraph = new UndirectedSparseGraph<Agent, Edge>();
    }
}
