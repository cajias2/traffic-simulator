/*
 * @(#)SocialSimFlowTest.java    %I%    %G%
 * @author biggie
 * 
 */
    
    package test.social;

import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import sim.app.social.SocialSim;
import sim.field.network.Edge;
import sim.graph.social.link.FriendLink;
import sim.graph.social.metrics.BronKerboschKCliqueFinder;
import sim.graph.social.metrics.CPMCommunityFinder;
import sim.graph.social.metrics.commTracker.TimeLineList;
import sim.mason.AgentNetwork;
import edu.uci.ics.jung.algorithms.layout.HypergraphLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.SetHypergraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.BasicHypergraphRenderer;

    /**
     * TODO Purpose
     * 
     * @author biggie
     */
public class SocialSimFlowTest {

    /**
     * TODO Purpose
     * 
     * @param
     * @return void
     * @author biggie
     */
    public static void main(String[] args) {
	SocialSim sim = new SocialSim(System.currentTimeMillis());
	// El 100 es el snapshot
	List<Edge[][]> adjList = sim.runSim(args, 1);
	System.out.println("Finding Communities....");
	System.out.println("Cliques\tCommunities");
	List<Collection<Set<Integer>>> kEvol = new LinkedList<Collection<Set<Integer>>>();
	List<Graph<Integer, FriendLink>> graphEvol = new LinkedList<Graph<Integer, FriendLink>>();
	int lastKsize = -1;

	TimeLineList evolution = new TimeLineList();
	int snapshot = 1;

	for (Edge[][] eAr : adjList) {

	    Graph<Integer, FriendLink> graph = AgentNetwork.adjListToJungGraph(eAr);
	    BronKerboschKCliqueFinder<Integer, FriendLink> maxCliques = new BronKerboschKCliqueFinder<Integer, FriendLink>(
		    graph);
	    Collection<Set<Integer>> kFourCliques = maxCliques.getAllMaxKCliques(3);

	    // Conjunto de communidades en ese snapshot
	    Collection<Set<Integer>> kComs = findCPM(kFourCliques);

	    kEvol.add(kComs);
	    graphEvol.add(AgentNetwork.adjListToJungGraph(eAr));

	    // System.out.println(kFourCliques.size() + "\t" + kComs.size());
	    Graph<Integer, FriendLink> g = AgentNetwork.adjListToJungGraph(eAr);

	    for (Set<Integer> community : kComs) {
		evolution.set(snapshot, community, g);
	    }
	    snapshot++;

	}

	evolution.writeMetrics();

	JFrame frame = new JFrame("Simple Graph View");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	Hypergraph<Integer, Integer> hg = populateHg(graphEvol.get(1), kEvol.get(1));
	HypergraphLayout<Integer, Integer> l = new HypergraphLayout<Integer, Integer>(hg, SpringLayout2.class);

	VisualizationViewer<Integer, Integer> v = new VisualizationViewer<Integer, Integer>(l, new Dimension(600, 600));

	v.setRenderer(new BasicHypergraphRenderer<Integer, Integer>());
	frame.getContentPane().add(v);
	frame.pack();
	frame.setVisible(true);

    }

    /**
     * TODO Purpose
     * 
     * @param
     * @return void
     * @author biggie
     * @param <V>
     * @param <V>
     * @param <E>
     * @param <E>
     */
    private static <V, E> Hypergraph<V, Integer> populateHg(Graph<V, E> graph_, Collection<Set<V>> collection_) {
	Hypergraph<V, Integer> hg = new SetHypergraph<V, Integer>();
	for (V node : graph_.getVertices()) {
	    hg.addVertex(node);
	}
	int cliqueCount = 0;
	for (Set<V> kclique : collection_) {
	    hg.addEdge(cliqueCount++, kclique);
	}

	return hg;

    }

    /**
     * @author biggie
     * @name findCPM Purpose TODO
     * 
     * @param
     * @return Collection<Set<Agent>>
     */
    private static Collection<Set<Integer>> findCPM(Collection<Set<Integer>> kcliques_) {

	CPMCommunityFinder<Integer> cpm = new CPMCommunityFinder<Integer>(kcliques_);
	return cpm.findCommunities(4);
    }

    }
