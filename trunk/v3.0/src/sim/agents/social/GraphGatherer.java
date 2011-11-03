package sim.agents.social;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sim.agents.Agent;
import sim.app.social.SocialSim;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.graph.social.link.FriendLink;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.io.GraphMLWriter;

/**
 * Kills the simulation after <code>SIM_TIME</code> steps
 * 
 * @author biggie
 * @param <V>
 * @param <E>
 */
public class GraphGatherer<V, E> implements Steppable {
    private static final long serialVersionUID = -1284600767084977696L;
    private final int SNAPSHOT;
    private final String OUT_FOLDR = System.getProperty("user.dir") + "/tmp";
    private final File _outDir;
    List<Graph<V, E>> _graphEvol = new ArrayList<Graph<V, E>>();

    public GraphGatherer(int snapshotSize_) {
	SNAPSHOT = snapshotSize_;
	_outDir = new File(OUT_FOLDR);
	if (_outDir.exists()) {
	    _outDir.delete();
	}
	_outDir.mkdir();
    }

    /**
	 * 
	 */
    @Override
    public void step(SimState state_) {
	SocialSim socSim = (SocialSim) state_;
	if (0 == socSim.schedule.getSteps() % SNAPSHOT) {
	    if (null != socSim.network && null != socSim.network.getJGraph()) {
		writeGraph(socSim.network.getJGraph());
		_graphEvol.add((Graph<V, E>) socSim.network.getGraphSnapshot());
	    } else {
		writeGraph(null);
		_graphEvol.add(null);
	    }

	}
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return void
     * @author biggie
     */
    private void writeGraph(Graph<Agent, FriendLink> jGraph_) {
	FileWriter outFileWrt;
	GraphMLWriter<V, E> gWriter = new GraphMLWriter<V, E>();
	try {
	    outFileWrt = new FileWriter(OUT_FOLDR + System.getProperty("file.separator") + System.currentTimeMillis()
		    + ".xml");
	    BufferedWriter outWrt;
	    outWrt = new BufferedWriter(outFileWrt);
	    gWriter.save((Hypergraph<V, E>) jGraph_, outWrt);
	    outWrt.close();
	} catch (IOException e) {

	}
    }

    /**
     * @return the graphEvol
     * @author biggie
     */
    public List<Graph<V, E>> getGraphEvol() {
	// List<Graph<V, E>> gLst = new LinkedList<Graph<V, E>>();
	// GraphMLReader<Graph<V, E>, V, E> graphReader = null;
	//
	// try {
	// graphReader = new GraphMLReader<Graph<V,E>, GraphGatherer.V,
	// GraphGatherer.E>(vertex_factory, edge_factory)
	// } catch (ParserConfigurationException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// } catch (SAXException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	//
	// File tmDir = new File(OUT_FOLDR);
	// //Sort by timestamp
	// File[] files = tmDir.listFiles();
	// Arrays.sort(files, new Comparator<File>() {
	// public int compare(File f1, File f2) {
	// return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
	// }
	// });
	//
	// for(File f : files){
	// try {
	// Graph<V,E> graph = new UndirectedSparseGraph<V, E>();
	// graphReader.load(f.getAbsolutePath(), graph);
	// gLst.add(graph);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	return _graphEvol;
    }
}