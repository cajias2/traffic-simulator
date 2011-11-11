package sim.agents.social;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.graph.utils.Edge;
import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.app.social.db.DBManager;
import edu.uci.ics.jung.graph.Graph;

public class DBWriterAgent implements Steppable {

	private DBManager _dbConnection;
	
	public DBWriterAgent() {
	    }
	
	public void step(SimState state_) {
		SocialSim<Agent, String> socSim = (SocialSim<Agent, String>) state_;
		_dbConnection = socSim.getDBManager();
		Graph<Agent, Edge> graphChanges = socSim._temporalNetwork;
		int id = _dbConnection.getSimID();
		int step = (int)socSim.schedule.getSteps();
		
		int edges = graphChanges.getEdgeCount(); 
		if(edges>0){

		boolean changes = false;
		for(Edge e : graphChanges.getEdges()){
			int from = e.getSource();
			int to = e.getDest();
			boolean createEdge = e.addEdge();
							
			_dbConnection.addEdge(id, step, from, to, createEdge);
			
			changes = true;
		}
		
		if(changes){	
			_dbConnection.insertEdges();
		}	
		
		}
		
		socSim.resetTemporalNetwork();
	}

}
