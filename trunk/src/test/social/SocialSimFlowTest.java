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

import org.jgrapht.UndirectedGraph;

import sim.app.social.SocialSim;
import sim.field.network.Edge;
import sim.graph.social.algorithms.BronKerboschKCliqueFinder;
import sim.graph.social.algorithms.CPMCommunityFinder;
import sim.graph.social.algorithms.OldBronKerboschKCliqueFinder;
import sim.graph.social.algorithms.commTracker.TimeLineList;
import sim.graph.social.link.FriendLink;
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

	System.out.println("Cliques\tCommunities");
	List<Collection<Set<Integer>>> kEvol = new LinkedList<Collection<Set<Integer>>>();
	List<Graph<Integer, FriendLink>> graphEvol = new LinkedList<Graph<Integer, FriendLink>>();

	TimeLineList<Integer> evolution = new TimeLineList<Integer>(adjList.size());
	int snapshot = 1;

	for (Edge[][] eAr : adjList) {

	    Graph<Integer, FriendLink> graph = AgentNetwork.adjListToJungGraph(eAr);
	    graphEvol.add(graph);

	    UndirectedGraph<Integer, FriendLink> testGraph = AgentNetwork.adjListToJGraphTList(eAr);
	    OldBronKerboschKCliqueFinder<Integer, FriendLink> oldmaxClique = new OldBronKerboschKCliqueFinder<Integer, FriendLink>(
		    testGraph);

	    Collection<Set<Integer>> kComs = findCommunities(graph);
	    kEvol.add(kComs);

	    for (Set<Integer> community : kComs) {
		evolution.add(snapshot, community, graph);
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
     * @params
     * @return Collection<Set<Integer>>
     * @author biggie
     */
    private static Collection<Set<Integer>> findCommunities(Graph<Integer, FriendLink> graph) {
	BronKerboschKCliqueFinder<Integer, FriendLink> maxCliques = new BronKerboschKCliqueFinder<Integer, FriendLink>(
		graph);

	Collection<Set<Integer>> kFourCliques = maxCliques.getAllMaxKCliques(4);

	// Conjunto de communidades en ese snapshot
	Collection<Set<Integer>> kComs = findCPM(kFourCliques);
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
