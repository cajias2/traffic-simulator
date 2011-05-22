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
import sim.graph.social.algorithms.BronKerboschKCliqueFinder;
import sim.graph.social.algorithms.CPMCommunityFinder;
import sim.graph.social.algorithms.commTracker.Community;
import sim.graph.social.algorithms.commTracker.TimeLineList;
import sim.graph.social.link.FriendLink;
import sim.mason.AgentNetwork;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.HypergraphLayout;
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
    private static final long SEED = 320320486;

    /**
     * TODO Purpose
     * 
     * @param
     * @return void
     * @author biggie
     */
    public static void main(String[] args) {
	SocialSim sim = new SocialSim(SEED);
	// El 100 es el snapshot
	List<Edge[][]> adjList = sim.runSim(args, 4);

	System.out.println("Cliques\tCommunities");
	List<Graph<Integer, FriendLink>> graphEvol = new LinkedList<Graph<Integer, FriendLink>>();
	TimeLineList<Integer> evolution = new TimeLineList<Integer>(adjList.size());
	int snapshot = 1;

	for (Edge[][] eAr : adjList) {

	    Graph<Integer, FriendLink> graph = AgentNetwork.adjListToJungGraph(eAr);
	    graphEvol.add(graph);

	    // UndirectedGraph<Integer, FriendLink> testGraph =
	    // AgentNetwork.adjListToJGraphTList(eAr);
	    // OldBronKerboschKCliqueFinder<Integer, FriendLink> oldmaxClique =
	    // new OldBronKerboschKCliqueFinder<Integer, FriendLink>(
	    // testGraph);

	    Collection<Set<Integer>> kComs = findCommunities(graph, 3);

	    for (Set<Integer> community : kComs) {
		evolution.add(snapshot, community, graph);
	    }
	    snapshot++;
	    eAr = null;

	}
	evolution.writeMetrics();
	paintHyperGraph(graphEvol, evolution);

    }

    /**
     * @param <V>
     * @param <E>
     * @param graphEvol
     * @param evolution
     */
    private static <V, E> void paintHyperGraph(List<Graph<V, E>> graphEvol, TimeLineList<V> evolution) {
	JFrame frame = new JFrame("Simple Graph View");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	Hypergraph<V, Integer> hg = populateHg(graphEvol.get(2), evolution.getSnapshot(2));
	HypergraphLayout<V, Integer> l = new HypergraphLayout<V, Integer>(hg, CircleLayout.class);

	VisualizationViewer<V, Integer> v = new VisualizationViewer<V, Integer>(l, new Dimension(600, 600));

	v.setRenderer(new BasicHypergraphRenderer<V, Integer>());
	frame.getContentPane().add(v);
	frame.pack();
	frame.setVisible(true);
    }

    /**
     * TODO Purpose
     * 
     * @params
     * @return Collection<Set<Integer>>
     * @author biggie
     */
    private static <V, E> Collection<Set<V>> findCommunities(Graph<V, E> graph, int k_) {
	BronKerboschKCliqueFinder<V, E> maxCliques = new BronKerboschKCliqueFinder<V, E>(
		graph);

	Collection<Set<V>> kCliques = maxCliques.getAllMaxKCliques(k_);

	// Conjunto de communidades en ese snapshot
	CPMCommunityFinder<V> cpm = new CPMCommunityFinder<V>(kCliques);
	Collection<Set<V>> kComs = cpm.findCommunities(k_);
	return kComs;
    }

    /**
     * TODO Purpose
     * 
     * @param
     * @return void
     * @author biggie
     * @param <V>
     * @param <E>
     */
    private static <V, E> Hypergraph<V, Integer> populateHg(Graph<V, E> graph_, List<Community<V>> list_) {
	Hypergraph<V, Integer> hg = new SetHypergraph<V, Integer>();
	for (V node : graph_.getVertices()) {
	    hg.addVertex(node);
	}
	int cliqueCount = 0;
	if (null != list_) {
	    for (Community<V> kclique : list_) {
		hg.addEdge(cliqueCount++, kclique.getAllNodes());
	    }
	}
	return hg;
    }

}
